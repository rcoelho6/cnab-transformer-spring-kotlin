package com.reicorp.cnab.transformer.domain.entities

import java.util.UUID

/**
 * Entidade de domínio que representa a configuração de acesso SFTP de um cliente.
 *
 * Esta classe é pura: não possui anotações de framework e não depende de nenhuma
 * camada externa. Ela valida suas próprias invariantes de negócio no momento da criação.
 */
class SftpConfiguration(
    val id: UUID,
    val clientUuid: UUID,
    val serverAddress: String,
    val port: Int,
    val clientId: String,
    val clientSecret: String,
    val publicCertificate: String?,
    val remotePath: String
) {
    init {
        require(serverAddress.isNotBlank()) { "O endereço do servidor não pode ser vazio." }
        require(port in 1..65535) { "A porta deve estar entre 1 e 65535." }
        require(clientId.isNotBlank()) { "O clientId não pode ser vazio." }
        require(clientSecret.isNotBlank()) { "O clientSecret não pode ser vazio." }
        require(remotePath.isNotBlank()) { "O caminho remoto não pode ser vazio." }
    }

    companion object {
        fun create(
            clientUuid: UUID,
            serverAddress: String,
            port: Int,
            clientId: String,
            clientSecret: String,
            publicCertificate: String?,
            remotePath: String
        ): SftpConfiguration = SftpConfiguration(
            id = UUID.randomUUID(),
            clientUuid = clientUuid,
            serverAddress = serverAddress,
            port = port,
            clientId = clientId,
            clientSecret = clientSecret,
            publicCertificate = publicCertificate,
            remotePath = remotePath
        )
    }
}
