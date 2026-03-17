-- V1__create_sftp_configurations_table.sql
-- Cria a tabela de configurações SFTP por cliente.
-- Os campos client_id e client_secret são armazenados criptografados (AES-256-GCM via Spring Security Crypto).
-- O tamanho VARCHAR(2056) extrapola o texto cifrado codificado em hexadecimal gerado pelo Encryptors.delux().

CREATE TABLE sftp_configurations (
    id                 LONG          NOT NULL,
    register_type      VARCHAR(50)   NOT NULL,
    client_uuid        UUID          NOT NULL,
    server_address     VARCHAR(255)  NOT NULL,
    port               INT           NOT NULL,
    client_id          VARCHAR(2056) NOT NULL,
    client_secret      VARCHAR(2056) NOT NULL,
    public_certificate TEXT,
    remote_path        VARCHAR(255)  NOT NULL,
    created_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_sftp_configurations PRIMARY KEY (id)
);
