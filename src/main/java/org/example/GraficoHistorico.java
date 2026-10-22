
package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GraficoHistorico {

    public static void abrir(String municipio) {
        DefaultCategoryDataset dados = new DefaultCategoryDataset();

        String sql = """
                SELECT
                    r.ano_referencia,
                    SUM(r.matriculas_total) AS total_matriculas
                FROM registros_educacionais r
                JOIN escolas e ON e.id_escola = r.id_escola
                WHERE e.nome_municipio = ?
                GROUP BY r.ano_referencia
                ORDER BY r.ano_referencia
                """;

        try (Connection conexao = BancoDados.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, municipio);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    int ano = resultado.getInt("ano_referencia");
                    long total = resultado.getLong("total_matriculas");

                    dados.addValue(
                            total,
                            "Matrículas",
                            String.valueOf(ano)
                    );
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao carregar o histórico: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (dados.getColumnCount() == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Não existem dados históricos para esse município."
            );
            return;
        }

        JFreeChart grafico = ChartFactory.createLineChart(
                "Evolução das matrículas — " + municipio,
                "Ano de referência",
                "Total de matrículas",
                dados,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        grafico.getCategoryPlot().getDomainAxis()
                .setCategoryLabelPositions(
                        org.jfree.chart.axis.CategoryLabelPositions.UP_45
                );

        JFrame janela = new JFrame("Histórico de matrículas");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janela.setSize(900, 550);
        janela.setLocationRelativeTo(null);
        janela.add(new ChartPanel(grafico), BorderLayout.CENTER);
        janela.setVisible(true);
    }
}
