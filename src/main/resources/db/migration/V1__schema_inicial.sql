-- 1. AGENTE

CREATE TABLE agente (
                        id              BIGSERIAL PRIMARY KEY,
                        nome            VARCHAR(120) NOT NULL,
                        especialidade   VARCHAR(120) NOT NULL,
                        status          VARCHAR(30)  NOT NULL,
                        versao          BIGINT       NOT NULL DEFAULT 0,

                        CONSTRAINT ck_agente_status
                            CHECK (status IN (
                                              'DISPONIVEL',
                                              'EM_MISSAO',
                                              'SUSPENSO',
                                              'INATIVO'
                                ))
);



-- 2. EVENTO HISTORICO

CREATE TABLE evento_historico (
                                  id              BIGSERIAL PRIMARY KEY,
                                  titulo          VARCHAR(150) NOT NULL,
                                  descricao       TEXT         NOT NULL,
                                  data_evento     TIMESTAMP WITH TIME ZONE NOT NULL,
                                  localizacao     VARCHAR(180) NOT NULL,
                                  importancia     VARCHAR(30)  NOT NULL,

                                  CONSTRAINT ck_evento_historico_importancia
                                      CHECK (importancia IN (
                                                             'BAIXA',
                                                             'MODERADA',
                                                             'ALTA',
                                                             'CRITICA'
                                          ))
);



-- 3. ANOMALIA

CREATE TABLE anomalia (
                          id                   BIGSERIAL PRIMARY KEY,
                          evento_historico_id  BIGINT       NOT NULL,
                          descricao            TEXT         NOT NULL,
                          detectada_em         TIMESTAMP WITH TIME ZONE NOT NULL,
                          origem               VARCHAR(180),
                          observacoes          TEXT,
                          risco                VARCHAR(30)  NOT NULL,
                          status               VARCHAR(30)  NOT NULL,

                          CONSTRAINT ck_anomalia_risco
                              CHECK (risco IN (
                                               'BAIXO',
                                               'MODERADO',
                                               'ALTO',
                                               'CRITICO'
                                  )),

                          CONSTRAINT ck_anomalia_status
                              CHECK (status IN (
                                                'DETECTADA',
                                                'EM_ANALISE',
                                                'CONFIRMADA',
                                                'EM_CORRECAO',
                                                'ESTABILIZADA',
                                                'IRREVERSIVEL'
                                  )),

                          CONSTRAINT fk_anomalia_evento_historico
                              FOREIGN KEY (evento_historico_id)
                                  REFERENCES evento_historico(id)
                                  ON DELETE RESTRICT
);



-- 4. MISSAO

CREATE TABLE missao (
                        id                  BIGSERIAL PRIMARY KEY,
                        anomalia_id         BIGINT       NOT NULL,
                        codigo              VARCHAR(50)  NOT NULL,
                        objetivo            TEXT         NOT NULL,
                        destino_temporal    TIMESTAMP WITH TIME ZONE NOT NULL,
                        prioridade          VARCHAR(30)  NOT NULL,
                        status              VARCHAR(30)  NOT NULL,

                        resultado           TEXT,
                        resumo              TEXT,
                        impacto_observado   TEXT,

                        versao              BIGINT       NOT NULL DEFAULT 0,

                        CONSTRAINT uq_missao_codigo
                            UNIQUE (codigo),

                        CONSTRAINT ck_missao_prioridade
                            CHECK (prioridade IN (
                                                  'BAIXA',
                                                  'NORMAL',
                                                  'ALTA',
                                                  'EMERGENCIA'
                                )),

                        CONSTRAINT ck_missao_status
                            CHECK (status IN (
                                              'PLANEJADA',
                                              'AUTORIZADA',
                                              'EM_EXECUCAO',
                                              'CONCLUIDA',
                                              'FALHOU',
                                              'CANCELADA'
                                )),

                        CONSTRAINT fk_missao_anomalia
                            FOREIGN KEY (anomalia_id)
                                REFERENCES anomalia(id)
                                ON DELETE RESTRICT
);



-- 5. PARTICIPACAO MISSAO

CREATE TABLE participacao_missao (
                                     id              BIGSERIAL PRIMARY KEY,
                                     missao_id       BIGINT NOT NULL,
                                     agente_id       BIGINT NOT NULL,
                                     designado_em    TIMESTAMP WITH TIME ZONE NOT NULL,

                                     CONSTRAINT uq_participacao_missao_agente
                                         UNIQUE (missao_id, agente_id),

                                     CONSTRAINT fk_participacao_missao_missao
                                         FOREIGN KEY (missao_id)
                                             REFERENCES missao(id)
                                             ON DELETE RESTRICT,

                                     CONSTRAINT fk_participacao_missao_agente
                                         FOREIGN KEY (agente_id)
                                             REFERENCES agente(id)
                                             ON DELETE RESTRICT
);



-- 6. AUTORIZACAO

CREATE TABLE autorizacao (
                             id              BIGSERIAL PRIMARY KEY,
                             missao_id       BIGINT       NOT NULL UNIQUE,
                             responsavel     VARCHAR(120) NOT NULL,
                             instante        TIMESTAMP WITH TIME ZONE NOT NULL,
                             observacao      TEXT         NOT NULL,

                             CONSTRAINT fk_autorizacao_missao
                                 FOREIGN KEY (missao_id)
                                     REFERENCES missao(id)
                                     ON DELETE RESTRICT
);



-- 7. INTERVENCAO

CREATE TABLE intervencao (
                             id              BIGSERIAL PRIMARY KEY,
                             missao_id       BIGINT      NOT NULL,
                             agente_id       BIGINT      NOT NULL,
                             instante        TIMESTAMP WITH TIME ZONE NOT NULL,
                             acao            TEXT        NOT NULL,
                             justificativa   TEXT        NOT NULL,
                             impacto         VARCHAR(30) NOT NULL,
                             resultado       TEXT,

                             CONSTRAINT ck_intervencao_impacto
                                 CHECK (impacto IN (
                                                    'MINIMO',
                                                    'CONTROLADO',
                                                    'SIGNIFICATIVO',
                                                    'SEVERO'
                                     )),

                             CONSTRAINT fk_intervencao_missao
                                 FOREIGN KEY (missao_id)
                                     REFERENCES missao(id)
                                     ON DELETE RESTRICT,

                             CONSTRAINT fk_intervencao_agente
                                 FOREIGN KEY (agente_id)
                                     REFERENCES agente(id)
                                     ON DELETE RESTRICT
);



-- INDICES

CREATE INDEX idx_agente_status
    ON agente(status);

CREATE INDEX idx_anomalia_evento_historico
    ON anomalia(evento_historico_id);

CREATE INDEX idx_anomalia_status
    ON anomalia(status);

CREATE INDEX idx_anomalia_risco
    ON anomalia(risco);

CREATE INDEX idx_missao_anomalia
    ON missao(anomalia_id);

CREATE INDEX idx_missao_status
    ON missao(status);

CREATE INDEX idx_participacao_agente
    ON participacao_missao(agente_id);

CREATE INDEX idx_intervencao_missao
    ON intervencao(missao_id);

CREATE INDEX idx_intervencao_agente
    ON intervencao(agente_id);