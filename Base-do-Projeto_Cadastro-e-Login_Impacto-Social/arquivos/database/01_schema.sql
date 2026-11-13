-- =====================================================================
-- PRATO SOLIDÁRIO — Script de criação das tabelas (DDL)
-- Banco: PostgreSQL 14+   |   ODS 2 — Fome Zero
--
-- O script é idempotente (CREATE ... IF NOT EXISTS): pode ser executado
-- várias vezes sem erro. A aplicação Spring Boot o executa
-- automaticamente ao iniciar (spring.sql.init), mas ele também pode ser
-- rodado manualmente no psql ou no pgAdmin.
--
-- Mapeamento com as classes Java (pacote br.com.pratosolidario.model):
--   usuarios      -> classe abstrata Usuario
--   doadores      -> Doador extends Usuario       (herança table-per-subclass)
--   instituicoes  -> Instituicao extends Usuario  (herança table-per-subclass)
--   lotes_doacao  -> LoteDoacao
--   reservas      -> Reserva (associativa LoteDoacao x Instituicao)
--   notificacoes  -> Notificacao (RF06)
-- =====================================================================

-- Superclasse: dados comuns a todo usuário (classe abstrata Usuario)
CREATE TABLE IF NOT EXISTS usuarios (
    id            SERIAL        PRIMARY KEY,
    nome          VARCHAR(150)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    senha_hash    VARCHAR(255)  NOT NULL,
    tipo_usuario  VARCHAR(20)   NOT NULL
        CONSTRAINT ck_usuarios_tipo CHECK (tipo_usuario IN ('DOADOR', 'INSTITUICAO'))
);

-- Subclasse Doador (estabelecimento com excedente de alimentos)
CREATE TABLE IF NOT EXISTS doadores (
    usuario_id            INTEGER       PRIMARY KEY
        REFERENCES usuarios (id) ON DELETE CASCADE,
    documento             VARCHAR(20)   NOT NULL UNIQUE,   -- CPF (11) ou CNPJ (14 dígitos)
    tipo_estabelecimento  VARCHAR(50)   NOT NULL,
    regiao                VARCHAR(100)  NOT NULL            -- usada no RF06 (notificação por região)
);

-- Subclasse Instituicao (ONG / entidade assistencial receptora)
CREATE TABLE IF NOT EXISTS instituicoes (
    usuario_id              INTEGER       PRIMARY KEY
        REFERENCES usuarios (id) ON DELETE CASCADE,
    area_atuacao            VARCHAR(100)  NOT NULL,         -- região atendida pela instituição
    capacidade_atendimento  INTEGER       NOT NULL
        CONSTRAINT ck_instituicoes_capacidade CHECK (capacidade_atendimento > 0)
);

-- Lote de alimento publicado por um doador (composição Doador 1 — 0..* LoteDoacao)
CREATE TABLE IF NOT EXISTS lotes_doacao (
    id               SERIAL         PRIMARY KEY,
    doador_id        INTEGER        NOT NULL
        REFERENCES doadores (usuario_id),
    tipo_alimento    VARCHAR(100)   NOT NULL,
    quantidade_kg    DECIMAL(10,2)  NOT NULL
        CONSTRAINT ck_lotes_quantidade CHECK (quantidade_kg > 0),
    data_validade    DATE           NOT NULL,
    retirada_inicio  TIMESTAMP      NOT NULL,               -- janela de retirada (RF01)
    retirada_fim     TIMESTAMP      NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'DISPONIVEL'
        CONSTRAINT ck_lotes_status CHECK (status IN ('DISPONIVEL', 'RESERVADO', 'RETIRADO')),
    data_publicacao  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_lotes_janela CHECK (retirada_fim > retirada_inicio)
);

-- Reserva: classe associativa entre LoteDoacao e Instituicao
CREATE TABLE IF NOT EXISTS reservas (
    id                  SERIAL       PRIMARY KEY,
    lote_id             INTEGER      NOT NULL
        REFERENCES lotes_doacao (id),
    instituicao_id      INTEGER      NOT NULL
        REFERENCES instituicoes (usuario_id),
    data_reserva        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_retirada       TIMESTAMP,                           -- preenchida ao concluir a retirada
    familias_atendidas  INTEGER
        CONSTRAINT ck_reservas_familias CHECK (familias_atendidas IS NULL OR familias_atendidas > 0),
    status              VARCHAR(20)  NOT NULL DEFAULT 'ATIVA'
        CONSTRAINT ck_reservas_status CHECK (status IN ('ATIVA', 'CONCLUIDA', 'CANCELADA'))
);

-- Garante no próprio banco que um lote não seja reservado por duas
-- instituições ao mesmo tempo (condição de corrida citada na 1ª entrega):
-- só pode existir UMA reserva ativa ou concluída por lote.
CREATE UNIQUE INDEX IF NOT EXISTS ux_reservas_lote_ativo
    ON reservas (lote_id)
    WHERE status IN ('ATIVA', 'CONCLUIDA');

-- Notificações geradas quando um lote "crítico" é publicado (RF06)
CREATE TABLE IF NOT EXISTS notificacoes (
    id              SERIAL        PRIMARY KEY,
    instituicao_id  INTEGER       NOT NULL
        REFERENCES instituicoes (usuario_id) ON DELETE CASCADE,
    lote_id         INTEGER       NOT NULL
        REFERENCES lotes_doacao (id) ON DELETE CASCADE,
    mensagem        VARCHAR(255)  NOT NULL,
    data_criacao    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lida            BOOLEAN       NOT NULL DEFAULT FALSE
);

-- Índices de apoio às consultas mais frequentes
CREATE INDEX IF NOT EXISTS ix_lotes_status_validade ON lotes_doacao (status, data_validade);
CREATE INDEX IF NOT EXISTS ix_lotes_doador          ON lotes_doacao (doador_id);
CREATE INDEX IF NOT EXISTS ix_reservas_instituicao  ON reservas (instituicao_id);
CREATE INDEX IF NOT EXISTS ix_notificacoes_inst     ON notificacoes (instituicao_id, lida);
