package com.reicorp.cnab.transformer.domain.services

import com.reicorp.cnab.transformer.application.dtos.SftpConfigRegistrationRequest
import com.reicorp.cnab.transformer.application.dtos.SftpConfigRegistrationResponse
import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import com.reicorp.cnab.transformer.domain.ports.`in`.RegisterSftpConfigUseCase
import com.reicorp.cnab.transformer.domain.ports.out.SftpConfigRepositoryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Serviço de domínio que implementa o caso de uso [RegisterSftpConfigUseCase].
 *
 * Orquestra a criação da entidade de domínio e a delegação da persistência
 * para a porta de saída [SftpConfigRepositoryPort].
 */
@Service
class SftpConfigRegistrationService(
    private val sftpConfigRepositoryPort: SftpConfigRepositoryPort
) : RegisterSftpConfigUseCase {

    @Transactional
    override fun register(request: SftpConfigRegistrationRequest): SftpConfigRegistrationResponse {
        val sftpConfiguration = SftpConfiguration.create(
            clientUuid = request.clientUuid,
            serverAddress = request.serverAddress,
            port = request.port,
            clientId = request.clientId,
            clientSecret = request.clientSecret,
            publicCertificate = request.publicCertificate,
            remotePath = request.remotePath
        )

        val saved = sftpConfigRepositoryPort.save(sftpConfiguration)

        return SftpConfigRegistrationResponse(
            id = saved.id,
            clientUuid = saved.clientUuid,
            serverAddress = saved.serverAddress,
            port = saved.port,
            remotePath = saved.remotePath
        )
    }
}
