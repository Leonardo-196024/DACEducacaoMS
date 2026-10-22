
package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ImportadorCSV {

    private static final Pattern PADRAO_ANO =
            Pattern.compile("(20\\d{2})");

    private static final String[] COLUNAS = {
            "MatriculasTotal",
            "MatriculaInicial",
            "MatriculaAposCenso",
            "Transferidos",
            "Cancelados",
            "Falecido",
            "Abandono",
            "Aprovados",
            "Reprovados",
            "Cursando",
            "OutrasSituacoes"
    };

    public static void processarDiretorio(Path pasta)
            throws IOException, SQLException {

        if (!Files.isDirectory(pasta)) {
            throw new IOException(
                    "Pasta não encontrada: " + pasta.toAbsolutePath()
            );
        }

        try (Stream<Path> arquivos = Files.list(pasta)) {
            for (Path arquivo : arquivos
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString()
                            .toLowerCase().endsWith(".csv"))
                    .sorted()
                    .toList()) {

                processarArquivo(arquivo);
            }
        }
    }

    private static void processarArquivo(Path arquivo)
            throws IOException, SQLException {

        Matcher matcher = PADRAO_ANO.matcher(
                arquivo.getFileName().toString()
        );

        if (!matcher.find()) {
            System.out.println(
                    "Arquivo ignorado: " + arquivo.getFileName()
            );
            return;
        }

        int ano = Integer.parseInt(matcher.group(1));

        // Lê o CSV e organiza os objetos em memória.
        List<RegistroEducacional> registros = new ArrayList<>();

        try (Reader leitor = Files.newBufferedReader(
                arquivo, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(leitor)) {

            for (CSVRecord r : parser) {
                String municipio = r.get("NomeMunicipio").trim();
                String nomeEscola = r.get("NomeUnidadeEscolar").trim();

                if (municipio.isEmpty() || nomeEscola.isEmpty()) {
                    throw new IOException(
                            "Escola ou município vazio na linha "
                                    + r.getRecordNumber()
                                    + " do arquivo " + arquivo.getFileName()
                    );
                }

                Integer[] valores = new Integer[COLUNAS.length];

                for (int i = 0; i < COLUNAS.length; i++) {
                    valores[i] = lerInteiro(r.get(COLUNAS[i]));
                }

                registros.add(new RegistroEducacional(
                        ano,
                        municipio,
                        nomeEscola,
                        valores[0],
                        valores[1],
                        valores[2],
                        valores[3],
                        valores[4],
                        valores[5],
                        valores[6],
                        valores[7],
                        valores[8],
                        valores[9],
                        valores[10]
                ));
            }
        }

        // Persiste a lista no banco de dados.
        String sqlEscola = """
                INSERT INTO escolas (nome_municipio, nome_escola)
                VALUES (?, ?)
                ON CONFLICT (nome_municipio, nome_escola)
                DO NOTHING
                """;

        String sqlBuscarEscola = """
                SELECT id_escola FROM escolas
                WHERE nome_municipio = ? AND nome_escola = ?
                """;

        String sqlRegistro = """
                INSERT INTO registros_educacionais (
                    id_escola, ano_referencia,
                    matriculas_total, matricula_inicial,
                    matricula_apos_censo, transferidos,
                    cancelados, falecido, abandono,
                    aprovados, reprovados, cursando,
                    outras_situacoes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id_escola, ano_referencia)
                DO UPDATE SET
                    matriculas_total = EXCLUDED.matriculas_total,
                    matricula_inicial = EXCLUDED.matricula_inicial,
                    matricula_apos_censo = EXCLUDED.matricula_apos_censo,
                    transferidos = EXCLUDED.transferidos,
                    cancelados = EXCLUDED.cancelados,
                    falecido = EXCLUDED.falecido,
                    abandono = EXCLUDED.abandono,
                    aprovados = EXCLUDED.aprovados,
                    reprovados = EXCLUDED.reprovados,
                    cursando = EXCLUDED.cursando,
                    outras_situacoes = EXCLUDED.outras_situacoes
                """;

        try (Connection conexao = BancoDados.conectar()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement inserirEscola =
                         conexao.prepareStatement(sqlEscola);
                 PreparedStatement buscarEscola =
                         conexao.prepareStatement(sqlBuscarEscola);
                 PreparedStatement inserirRegistro =
                         conexao.prepareStatement(sqlRegistro)) {

                for (RegistroEducacional registro : registros) {
                    inserirEscola.setString(
                            1, registro.getNomeMunicipio()
                    );
                    inserirEscola.setString(
                            2, registro.getNomeEscola()
                    );
                    inserirEscola.executeUpdate();

                    buscarEscola.setString(
                            1, registro.getNomeMunicipio()
                    );
                    buscarEscola.setString(
                            2, registro.getNomeEscola()
                    );

                    long idEscola;

                    try (ResultSet resultado =
                                 buscarEscola.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException(
                                    "Escola não encontrada: "
                                            + registro.getNomeEscola()
                            );
                        }

                        idEscola = resultado.getLong("id_escola");
                    }

                    inserirRegistro.setLong(1, idEscola);
                    inserirRegistro.setInt(
                            2, registro.getAnoReferencia()
                    );

                    Integer[] valores = {
                            registro.getMatriculasTotal(),
                            registro.getMatriculaInicial(),
                            registro.getMatriculaAposCenso(),
                            registro.getTransferidos(),
                            registro.getCancelados(),
                            registro.getFalecido(),
                            registro.getAbandono(),
                            registro.getAprovados(),
                            registro.getReprovados(),
                            registro.getCursando(),
                            registro.getOutrasSituacoes()
                    };

                    for (int i = 0; i < valores.length; i++) {
                        if (valores[i] == null) {
                            inserirRegistro.setNull(i + 3, Types.INTEGER);
                        } else {
                            inserirRegistro.setInt(i + 3, valores[i]);
                        }
                    }

                    inserirRegistro.executeUpdate();
                }

                conexao.commit();

            } catch (Exception e) {
                conexao.rollback();

                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }

                throw new SQLException(
                        "Falha ao importar " + arquivo.getFileName(), e
                );
            }
        }

        System.out.println(
                "Ano " + ano + ": " + registros.size()
                        + " registros importados/atualizados."
        );
    }

    private static Integer lerInteiro(String valor)
            throws IOException {

        String texto = valor == null ? "" : valor.trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException e) {
            throw new IOException(
                    "Valor inteiro inválido no CSV: " + texto, e
            );
        }
    }
}
