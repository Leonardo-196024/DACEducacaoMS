
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultaEducacional {

    public static void consultarMunicipio(
            String municipio, int ano) throws SQLException {

        String sql = """
                SELECT
                    e.nome_municipio,
                    r.ano_referencia,
                    COUNT(*) AS quantidade_escolas,
                    SUM(r.matriculas_total) AS total_matriculas,
                    SUM(r.aprovados) AS total_aprovados,
                    SUM(r.abandono) AS total_abandono
                FROM registros_educacionais r
                JOIN escolas e
                    ON e.id_escola = r.id_escola
                WHERE e.nome_municipio = ?
                  AND r.ano_referencia = ?
                GROUP BY e.nome_municipio, r.ano_referencia
                """;

        try (
                Connection conexao = BancoDados.conectar();
                PreparedStatement comando =
                        conexao.prepareStatement(sql)
        ) {
            comando.setString(1, municipio);
            comando.setInt(2, ano);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    System.out.println("\n=== Consulta Educacional ===");
                    System.out.println(
                            "Município: "
                                    + resultado.getString("nome_municipio")
                    );
                    System.out.println(
                            "Ano: "
                                    + resultado.getInt("ano_referencia")
                    );
                    System.out.println(
                            "Escolas: "
                                    + resultado.getLong("quantidade_escolas")
                    );
                    System.out.println(
                            "Matrículas: "
                                    + resultado.getLong("total_matriculas")
                    );
                    System.out.println(
                            "Aprovações: "
                                    + resultado.getLong("total_aprovados")
                    );
                    System.out.println(
                            "Abandono: "
                                    + resultado.getLong("total_abandono")
                    );
                } else {
                    System.out.println(
                            "Nenhum registro encontrado para "
                                    + municipio + " em " + ano + "."
                    );
                }
            }
        }
    }


    public static void main(String[] args) {

        java.util.Scanner scanner =
                new java.util.Scanner(System.in);

        try {
            System.out.print("Digite o município: ");
            String municipio = scanner.nextLine().trim();

            System.out.print("Digite o ano: ");
            int ano = Integer.parseInt(scanner.nextLine().trim());

            consultarMunicipio(municipio, ano);

        } catch (NumberFormatException e) {
            System.out.println("Erro: digite um ano válido.");
        } catch (SQLException e) {
            System.err.println(
                    "Erro na consulta: " + e.getMessage()
            );
        }
    }

}
