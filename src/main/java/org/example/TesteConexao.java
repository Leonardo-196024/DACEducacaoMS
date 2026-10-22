
package org.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = BancoDados.conectar()) {

            DatabaseMetaData meta =
                    conexao.getMetaData();

            System.out.println("Conexão estabelecida!");
            System.out.println(
                    "Banco: " + conexao.getCatalog()
            );
            System.out.println(
                    "PostgreSQL: " + meta.getDatabaseProductVersion()
            );

        } catch (Exception e) {
            System.err.println(
                    "Falha na conexão: " + e.getMessage()
            );
        }
    }
}
