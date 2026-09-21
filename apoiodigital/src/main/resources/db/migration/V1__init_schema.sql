CREATE TABLE cliente (
    id VARCHAR(36) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    access_key VARCHAR(255) NOT NULL,
    area_atuacao VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT uk_cliente_access_key UNIQUE (access_key)
);

CREATE TABLE personalizacao (
    id VARCHAR(36) NOT NULL,
    id_cliente VARCHAR(36) NOT NULL,
    regra_personalizada TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_personalizacao_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
);

CREATE TABLE usuario (
    id VARCHAR(36) NOT NULL,
    nome VARCHAR(150),
    id_cliente VARCHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
);

CREATE TABLE pedido (
    id VARCHAR(36) NOT NULL,
    id_usuario VARCHAR(36) NOT NULL,
    prompt TEXT,
    timestamp DATETIME,
    status VARCHAR(30) NOT NULL DEFAULT 'PRONTO',
    tipo_pendencia VARCHAR(30),
    descricao_duvida TEXT,
    pergunta_pendente TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

CREATE TABLE resposta (
    id VARCHAR(36) NOT NULL,
    id_pedido VARCHAR(36) NOT NULL,
    mensagem TEXT,
    raciocinio TEXT,
    timestamp DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_resposta_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id)
);

CREATE TABLE componente (
    id VARCHAR(36) NOT NULL,
    id_resposta VARCHAR(36) NOT NULL,
    assinatura TEXT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_componente_resposta FOREIGN KEY (id_resposta) REFERENCES resposta (id)
);
