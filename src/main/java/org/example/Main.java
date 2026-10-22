
package org.example;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        try {
            ImportadorCSV.processarDiretorio(Path.of("dados"));

            System.out.println(
                    "\nImportação concluída com sucesso!"
            );

        } catch (Exception e) {
            System.err.println(
                    "Erro na importação: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
