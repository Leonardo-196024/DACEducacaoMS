# DAC Educação MS

Sistema desktop para importação, armazenamento, consulta e visualização de dados educacionais da rede pública de Mato Grosso do Sul.

## Objetivo

O projeto integra arquivos CSV de matrículas por unidade escolar a um banco de dados relacional, permitindo consultar indicadores por município e ano de referência.

## Funcionalidades

- Importação de arquivos CSV anuais.
- Armazenamento dos dados no PostgreSQL.
- Consulta de indicadores por município e ano.
- Visualização da evolução histórica das matrículas.
- Ranking de municípios por total de matrículas registradas.

## Tecnologias utilizadas

- Java 21
- Maven
- PostgreSQL
- JDBC
- Apache Commons CSV
- JFreeChart
- Java Swing
- IntelliJ IDEA

## Dados utilizados

O projeto utiliza arquivos públicos de matrículas por unidade escolar, disponíveis no [Portal de Dados Abertos de Mato Grosso do Sul](https://www.dados.ms.gov.br/dataset/matriculas-por-unidade-escolar).

Foram utilizados arquivos referentes a 2018–2024 e 2026. O arquivo referente a 2025 não estava disponível entre os dados coletados.

## Pré-requisitos

- JDK 21
- Maven
- PostgreSQL
- Arquivos CSV utilizados na importação

As dependências Java são declaradas no arquivo `pom.xml`.

## Configuração do banco de dados

Crie no PostgreSQL um banco chamado `dac_educacao_ms` e configure as variáveis de ambiente abaixo no ambiente de execução:

- `DB_URL`: `jdbc:postgresql://localhost:5432/dac_educacao_ms`
- `DB_USER`: usuário do PostgreSQL
- `DB_PASSWORD`: senha do PostgreSQL

Não armazene credenciais diretamente no código ou neste arquivo.

## Execução

1. Abra o projeto no IntelliJ IDEA.
2. Configure as variáveis de ambiente necessárias.
3. Confira se o PostgreSQL está em execução e se o banco foi criado.
4. Para importar os arquivos, execute a classe `org.example.Main`.
5. Para abrir a interface gráfica, execute a classe `org.example.Dashboard`.

Os arquivos CSV devem estar na pasta `dados/`, conforme esperado pelo código de importação.

## Estrutura do banco

O banco utiliza duas tabelas principais:

- `escolas`: armazena município e nome da unidade escolar.
- `registros_educacionais`: armazena os indicadores educacionais por escola e ano.

## Limitações

A série histórica utilizada não contém arquivo referente a 2025. Valores ausentes ou zerados nos arquivos de origem devem ser interpretados com cautela.

## Autoria

Projeto acadêmico desenvolvido para a disciplina de Ciência da Computação.

Leonardo Modesto de Paula, RA 196024