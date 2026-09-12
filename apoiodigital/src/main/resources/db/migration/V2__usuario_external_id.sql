-- The SDK contract (doc V2 §2.4) says userId is an anonymized identifier minted by the
-- PARTNER app and forwarded verbatim by the SDK — not a UUID minted by this backend. Store it
-- as external_id, unique per tenant; the internal UUID PK stays as the FK target for pedido.
-- The usuario table is empty at this point (no production data), so the NOT NULL add is safe.

ALTER TABLE usuario
    ADD COLUMN external_id VARCHAR(255) NOT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT uk_usuario_cliente_external UNIQUE (id_cliente, external_id);
