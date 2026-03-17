-- V1__create_sftp_configurations_table.sql
-- Cria a tabela de configurações SFTP por cliente.
-- Os campos client_id e client_secret são armazenados criptografados (AES-256-GCM via Spring Security Crypto).
-- O tamanho VARCHAR(512) acomoda o texto cifrado codificado em hexadecimal gerado pelo Encryptors.stronger().

CREATE TABLE sftp_configurations (
    id            UUID         NOT NULL,
    client_uuid   UUID         NOT NULL,
    server_address VARCHAR(255) NOT NULL,
    port          INT          NOT NULL,
    client_id     VARCHAR(512) NOT NULL,
    client_secret VARCHAR(512) NOT NULL,
    public_certificate TEXT,
    remote_path   VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_sftp_configurations PRIMARY KEY (id),
    CONSTRAINT uq_sftp_configurations_client_uuid UNIQUE (client_uuid)
);
