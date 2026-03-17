package com.reicorp.cnab.transformer.infrastructure.repositories.models

import com.reicorp.cnab.transformer.infrastructure.repositories.converters.EncryptedStringConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo JPA mapeado para a tabela [sftp_configurations].
 *
 * Os campos [clientId] e [clientSecret] são automaticamente criptografados
 * antes de serem persistidos e descriptografados ao serem lidos, via [EncryptedStringConverter].
 */
@Entity
@Table(name = "sftp_configurations")
class SftpConfigModel(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "client_uuid", nullable = false, unique = true, updatable = false)
    val clientUuid: UUID,

    @Column(name = "server_address", nullable = false)
    val serverAddress: String,

    @Column(name = "port", nullable = false)
    val port: Int,

    @Convert(converter = EncryptedStringConverter::class)
    @Column(name = "client_id", nullable = false)
    val clientId: String,

    @Convert(converter = EncryptedStringConverter::class)
    @Column(name = "client_secret", nullable = false)
    val clientSecret: String,

    @Column(name = "public_certificate", columnDefinition = "TEXT")
    val publicCertificate: String?,

    @Column(name = "remote_path", nullable = false)
    val remotePath: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null
)
