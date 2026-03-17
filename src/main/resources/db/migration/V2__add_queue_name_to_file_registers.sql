-- V2__add_queue_name_to_file_registers.sql
-- Adiciona suporte ao cadastro de configuração via fila (Queue) na tabela file_registers.
--
-- A coluna queue_name armazena o nome da fila a ser monitorada.
-- As colunas server_address, port, client_id, client_secret e remote_path passam a ser
-- opcionais (nullable), pois são exclusivas do tipo SFTP e não se aplicam ao tipo QUEUE.

ALTER TABLE file_registers
    ADD COLUMN queue_name VARCHAR(255) NULL;

ALTER TABLE file_registers
    ALTER COLUMN server_address DROP NOT NULL;

ALTER TABLE file_registers
    ALTER COLUMN port DROP NOT NULL;

ALTER TABLE file_registers
    ALTER COLUMN client_id DROP NOT NULL;

ALTER TABLE file_registers
    ALTER COLUMN client_secret DROP NOT NULL;

ALTER TABLE file_registers
    ALTER COLUMN remote_path DROP NOT NULL;
