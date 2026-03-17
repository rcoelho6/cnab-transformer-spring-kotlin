package com.reicorp.cnab.transformer.infrastructure.repositories

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import com.reicorp.cnab.transformer.domain.ports.out.SftpConfigRepositoryPort
import com.reicorp.cnab.transformer.infrastructure.repositories.models.SftpConfigModel
import org.springframework.stereotype.Component

/**
 * Adaptador de saída que implementa [SftpConfigRepositoryPort].
 *
 * Faz a ponte entre a entidade de domínio [SftpConfiguration] e o modelo JPA [SftpConfigModel],
 * delegando a persistência ao [SftpConfigJpaRepository].
 *
 * A conversão entre domínio e modelo é feita inline neste adaptador,
 * mantendo o mapeamento próximo ao ponto de uso.
 */
@Component
class SftpConfigRepositoryAdapter(
    private val jpaRepository: SftpConfigJpaRepository
) : SftpConfigRepositoryPort {

    override fun save(sftpConfiguration: SftpConfiguration): SftpConfiguration {
        val model = toModel(sftpConfiguration)
        val saved = jpaRepository.save(model)
        return toDomain(saved)
    }

    private fun toModel(domain: SftpConfiguration): SftpConfigModel =
        SftpConfigModel(
            id = domain.id,
            clientUuid = domain.clientUuid,
            serverAddress = domain.serverAddress,
            port = domain.port,
            clientId = domain.clientId,
            clientSecret = domain.clientSecret,
            publicCertificate = domain.publicCertificate,
            remotePath = domain.remotePath
        )

    private fun toDomain(model: SftpConfigModel): SftpConfiguration =
        SftpConfiguration(
            id = model.id,
            clientUuid = model.clientUuid,
            serverAddress = model.serverAddress,
            port = model.port,
            clientId = model.clientId,
            clientSecret = model.clientSecret,
            publicCertificate = model.publicCertificate,
            remotePath = model.remotePath
        )
}
