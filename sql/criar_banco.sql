
CREATE TABLE IF NOT EXISTS escolas (
    id_escola BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_municipio VARCHAR(150) NOT NULL,
    nome_escola VARCHAR(250) NOT NULL,
    UNIQUE (nome_municipio, nome_escola)
);

CREATE TABLE IF NOT EXISTS registros_educacionais (
    id_registro BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_escola BIGINT NOT NULL
        REFERENCES escolas(id_escola),
    ano_referencia INTEGER NOT NULL,
    matriculas_total INTEGER,
    matricula_inicial INTEGER,
    matricula_apos_censo INTEGER,
    transferidos INTEGER,
    cancelados INTEGER,
    falecido INTEGER,
    abandono INTEGER,
    aprovados INTEGER,
    reprovados INTEGER,
    cursando INTEGER,
    outras_situacoes INTEGER,
    UNIQUE (id_escola, ano_referencia)
);

CREATE INDEX IF NOT EXISTS idx_registros_ano
    ON registros_educacionais(ano_referencia);

CREATE INDEX IF NOT EXISTS idx_escolas_municipio
    ON escolas(nome_municipio);
