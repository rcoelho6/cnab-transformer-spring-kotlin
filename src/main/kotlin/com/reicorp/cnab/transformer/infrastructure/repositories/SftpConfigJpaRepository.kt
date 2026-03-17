package com.reicorp.cnab.transformer.infrastructure.repositories

import com.reicorp.cnab.transformer.infrastructure.repositories.models.SftpConfigModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repositório Spring Data JPA para [SftpConfigModel].
 *
 * Fornece as operações de persistência básicas (CRUD) para a tabela sftp_configurations.
 */
@Repository
interface SftpConfigJpaRepository : JpaRepository<SftpConfigModel, UUID>
