package com.reicorp.cnab.transformer.infrastructure.repositories.models

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration
import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import com.reicorp.cnab.transformer.domain.entities.enums.RegistersType
import com.reicorp.cnab.transformer.infrastructure.repositories.converters.EncryptedStringConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "file_registers")
class FileRegisterModel(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "register_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val registerType: RegistersType,

    @Column(name = "client_uuid", nullable = false, unique = true, updatable = false)
    val clientUuid: UUID,

    @Column(name = "server_address")
    val serverAddress: String? = null,

    @Column(name = "port")
    val port: Int? = null,

    @Convert(converter = EncryptedStringConverter::class)
    @Column(name = "client_id")
    val clientId: String? = null,

    @Convert(converter = EncryptedStringConverter::class)
    @Column(name = "client_secret")
    val clientSecret: String? = null,

    @Column(name = "public_certificate", columnDefinition = "TEXT")
    val publicCertificate: String? = null,

    @Column(name = "remote_path")
    val remotePath: String? = null,

    @Column(name = "queue_name")
    val queueName: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null
) {

    companion object {
        fun fromEntity(sftpConfiguration: SftpConfiguration, registerType: RegistersType) = FileRegisterModel(
            registerType = registerType,
            clientUuid = sftpConfiguration.clientUuid,
            serverAddress = sftpConfiguration.serverAddress,
            port = sftpConfiguration.port,
            clientId = sftpConfiguration.clientId,
            clientSecret = sftpConfiguration.clientSecret,
            publicCertificate = sftpConfiguration.publicCertificate,
            remotePath = sftpConfiguration.remotePath
        )

        fun fromEntity(queueConfiguration: QueueConfiguration, registerType: RegistersType) = FileRegisterModel(
            registerType = registerType,
            clientUuid = queueConfiguration.clientUuid,
            queueName = queueConfiguration.queueName
        )
    }
}
