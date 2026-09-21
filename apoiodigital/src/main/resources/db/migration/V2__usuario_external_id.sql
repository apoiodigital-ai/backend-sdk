ALTER TABLE usuario
    ADD COLUMN external_id VARCHAR(255) NOT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT uk_usuario_cliente_external UNIQUE (id_cliente, external_id);
