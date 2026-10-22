
package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JFrame {

    private final JComboBox<String> municipioCombo =
            new JComboBox<>();

    private final JComboBox<Integer> anoCombo =
            new JComboBox<>();

    private final JLabel escolasLabel =
            new JLabel("—");

    private final JLabel matriculasLabel =
            new JLabel("—");

    private final JLabel aprovadosLabel =
            new JLabel("—");

    private final JLabel abandonoLabel =
            new JLabel("—");

    private final JTextArea resultadoArea =
            new JTextArea();

    public Dashboard() {
        setTitle("DAC Educação MS");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));

        criarInterface();
        carregarFiltros();
    }


    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );


        JPanel cabecalho = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("DAC Educação MS");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitulo = new JLabel(
                "Análise de dados da educação pública de Mato Grosso do Sul"
        );

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 5));
        textos.add(titulo);
        textos.add(subtitulo);
        cabecalho.add(textos, BorderLayout.CENTER);


        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.add(new JLabel("Município:"));

        municipioCombo.setPreferredSize(new Dimension(220, 30));
        filtros.add(municipioCombo);

        filtros.add(new JLabel("Ano:"));

        anoCombo.setPreferredSize(new Dimension(100, 30));
        filtros.add(anoCombo);

        JButton consultarBotao = new JButton("Consultar");
        consultarBotao.addActionListener(e -> consultarDados());
        filtros.add(consultarBotao);


        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));

        JButton graficoButton = new JButton("Ver evolução histórica");
        graficoButton.addActionListener(e -> {
            String municipio = (String) municipioCombo.getSelectedItem();

            if (municipio == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecione um município primeiro."
                );
                return;
            }

            GraficoHistorico.abrir(municipio);
        });
        botoes.add(graficoButton);

        JButton rankingButton = new JButton("Ranking de municípios");
        rankingButton.addActionListener(e -> RankingMunicipios.abrir());
        botoes.add(rankingButton);


        JPanel conteudoFiltros = new JPanel();
        conteudoFiltros.setLayout(new BoxLayout(conteudoFiltros, BoxLayout.Y_AXIS));
        conteudoFiltros.add(filtros);
        conteudoFiltros.add(botoes);

        JPanel painelFiltros = new JPanel(new BorderLayout());
        painelFiltros.setBorder(
                BorderFactory.createTitledBorder("Filtros de consulta")
        );
        painelFiltros.add(conteudoFiltros, BorderLayout.CENTER);


        JPanel indicadores = new JPanel(new GridLayout(2, 2, 12, 12));
        indicadores.add(criarCartao("Escolas", escolasLabel));
        indicadores.add(criarCartao("Total de matrículas", matriculasLabel));
        indicadores.add(criarCartao("Aprovações registradas", aprovadosLabel));
        indicadores.add(criarCartao("Abandono registrado", abandonoLabel));


        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultadoArea.setLineWrap(true);
        resultadoArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(resultadoArea);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Detalhes da consulta")
        );


        JPanel topo = new JPanel(new BorderLayout(10, 15));
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(painelFiltros, BorderLayout.SOUTH);


        JSplitPane divisao = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                indicadores,
                scroll
        );

        divisao.setResizeWeight(0.55);
        divisao.setDividerLocation(250);
        divisao.setContinuousLayout(true);
        divisao.setOneTouchExpandable(true);

        principal.add(topo, BorderLayout.NORTH);
        principal.add(divisao, BorderLayout.CENTER);

        setContentPane(principal);
    }


    private JPanel criarCartao(String titulo, JLabel valor) {
        JPanel cartao = new JPanel(new BorderLayout(5, 10));
        cartao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel textoTitulo = new JLabel(titulo);
        textoTitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        valor.setFont(new Font("Arial", Font.BOLD, 24));

        cartao.add(textoTitulo, BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);

        return cartao;
    }

    private void carregarFiltros() {
        String sqlMunicipios =
                "SELECT DISTINCT nome_municipio " +
                        "FROM escolas ORDER BY nome_municipio";

        String sqlAnos =
                "SELECT DISTINCT ano_referencia " +
                        "FROM registros_educacionais ORDER BY ano_referencia";

        try (Connection conexao = BancoDados.conectar();
             Statement comando = conexao.createStatement()) {

            try (ResultSet rs = comando.executeQuery(sqlMunicipios)) {
                while (rs.next()) {
                    municipioCombo.addItem(
                            rs.getString("nome_municipio")
                    );
                }
            }

            try (ResultSet rs = comando.executeQuery(sqlAnos)) {
                while (rs.next()) {
                    anoCombo.addItem(rs.getInt("ano_referencia"));
                }
            }

            if (municipioCombo.getItemCount() > 0
                    && anoCombo.getItemCount() > 0) {
                consultarDados();
            }

        } catch (SQLException e) {
            mostrarErro("Não foi possível carregar os filtros.", e);
        }
    }

    private void consultarDados() {
        String municipio =
                (String) municipioCombo.getSelectedItem();

        Integer ano = (Integer) anoCombo.getSelectedItem();

        if (municipio == null || ano == null) {
            return;
        }

        String sql = """
                SELECT
                    COUNT(*) AS quantidade_escolas,
                    SUM(matriculas_total) AS total_matriculas,
                    SUM(aprovados) AS total_aprovados,
                    SUM(abandono) AS total_abandono
                FROM registros_educacionais r
                JOIN escolas e ON e.id_escola = r.id_escola
                WHERE e.nome_municipio = ?
                  AND r.ano_referencia = ?
                """;

        try (Connection conexao = BancoDados.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setString(1, municipio);
            comando.setInt(2, ano);

            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next() && rs.getLong("quantidade_escolas") > 0) {
                    escolasLabel.setText(
                            String.valueOf(
                                    rs.getLong("quantidade_escolas")
                            )
                    );

                    matriculasLabel.setText(
                            String.format(
                                    "%,d",
                                    rs.getLong("total_matriculas")
                            )
                    );

                    aprovadosLabel.setText(
                            String.format(
                                    "%,d",
                                    rs.getLong("total_aprovados")
                            )
                    );

                    abandonoLabel.setText(
                            String.format(
                                    "%,d",
                                    rs.getLong("total_abandono")
                            )
                    );

                    resultadoArea.setText(
                            "Município: " + municipio + "\n" +
                                    "Ano: " + ano + "\n\n" +
                                    "Escolas encontradas: " +
                                    escolasLabel.getText() + "\n" +
                                    "Matrículas registradas: " +
                                    matriculasLabel.getText() + "\n" +
                                    "Aprovações registradas: " +
                                    aprovadosLabel.getText() + "\n" +
                                    "Abandono registrado: " +
                                    abandonoLabel.getText() + "\n\n" +
                                    "Fonte: arquivos oficiais de matrículas."
                    );

                } else {
                    limparIndicadores();
                    resultadoArea.setText(
                            "Não foram encontrados registros para " +
                                    municipio + " em " + ano + "."
                    );
                }
            }

        } catch (SQLException e) {
            mostrarErro("Erro ao consultar os dados.", e);
        }
    }

    private void limparIndicadores() {
        escolasLabel.setText("—");
        matriculasLabel.setText("—");
        aprovadosLabel.setText("—");
        abandonoLabel.setText("—");
    }

    private void mostrarErro(String mensagem, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                mensagem + "\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
