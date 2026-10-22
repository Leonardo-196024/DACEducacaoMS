
package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BancoDados {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:postgresql://localhost:5432/dac_educacao_ms"
            );

    private static final String USUARIO =
            System.getenv("DB_USER");

    private static final String SENHA =
            System.getenv("DB_PASSWORD");

    public static Connection conectar() throws SQLException {
        if (USUARIO == null || SENHA == null) {
            throw new SQLException(
                    "Configure as variáveis de ambiente DB_USER e DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
