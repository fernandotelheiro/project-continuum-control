CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,

                         login VARCHAR(100) NOT NULL UNIQUE,
                         senha_hash VARCHAR(100) NOT NULL,

                         perfil VARCHAR(30) NOT NULL,

                         agente_id BIGINT NULL,

                         CONSTRAINT ck_usuario_perfil
                             CHECK (
                                 perfil IN (
                                            'OPERADOR',
                                            'AGENTE',
                                            'SUPERVISOR',
                                            'ADMINISTRADOR'
                                     )
                                 ),

                         CONSTRAINT uk_usuario_agente
                             UNIQUE (agente_id),

                         CONSTRAINT fk_usuario_agente
                             FOREIGN KEY (agente_id)
                                 REFERENCES agente(id)
                                 ON DELETE SET NULL
);