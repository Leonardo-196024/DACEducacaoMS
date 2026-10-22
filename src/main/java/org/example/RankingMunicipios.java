
package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RankingMunicipios extends JFrame {

    private final JComboBox<Integer> anoCombo = new JComboBox<>();
    private final JTable tabela = new JTable();

    public RankingMunicipios() {
        setTitle("Ranking de Matrículas por Município");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
        carregarAnos();
    }

    private void criarInterface() {
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        painelSuperior.add(new JLabel("Ano de referência:"));
        painelSuperior.add(anoCombo);

        JButton consultarButton = new JButton("Gerar ranking");
        consultarButton.addActionListener(e -> consultarRanking());
        painelSuperior.add(consultarButton);

        tabela.setFillsViewportHeight(true);
        tabela.setAutoCreateRowSorter(true);

        add(painelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarAnos() {
        String sql = """
                SELECT DISTINCT ano_referencia
                FROM registros_educacionais
                ORDER BY ano_referencia
                """;

        try (Connection conexao = BancoDados.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                anoCombo.addItem(resultado.getInt("ano_referencia"));
            }

        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void consultarRanking() {
        Integer ano = (Integer) anoCombo.getSelectedItem();

        if (ano == null) {
            JOptionPane.showMessageDialog(this, "Não há anos disponíveis.");
            return;
        }

        String sql = """
                SELECT
                    e.nome_municipio,
                    COUNT(*) AS quantidade_escolas,
                    COALESCE(SUM(r.matriculas_total), 0) AS total_matriculas
                FROM registros_educacionais r
                JOIN escolas e ON e.id_escola = r.id_escola
                WHERE r.ano_referencia = ?
                GROUP BY e.nome_municipio
                ORDER BY total_matriculas DESC, e.nome_municipio ASC
                """;

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Posição", "Município", "Escolas", "Matrículas"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conexao = BancoDados.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, ano);

            try (ResultSet resultado = comando.executeQuery()) {
                int posicao = 1;

                while (resultado.next()) {
                    modelo.addRow(new Object[]{
                            posicao++,
                            resultado.getString("nome_municipio"),
                            resultado.getLong("quantidade_escolas"),
                            resultado.getLong("total_matriculas")
                    });
                }
            }

            tabela.setModel(modelo);
            tabela.setAutoCreateRowSorter(true);

        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void mostrarErro(SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                "Erro ao consultar o banco: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void abrir() {
        SwingUtilities.invokeLater(() -> {
            RankingMunicipios janela = new RankingMunicipios();
            janela.setVisible(true);
        });
    }
}
