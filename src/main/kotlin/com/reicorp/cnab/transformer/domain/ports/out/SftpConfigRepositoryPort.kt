package com.reicorp.cnab.transformer.domain.ports.out

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration

/**
 * Porta de saída (contrato de persistência) para [SftpConfiguration].
 *
 * Define o contrato que o domínio usa para persistir dados,
 * sem conhecer detalhes de banco de dados ou JPA.
 * A implementação concreta reside em infrastructure.repositories.
 */
interface SftpConfigRepositoryPort {
    fun save(sftpConfiguration: SftpConfiguration): SftpConfiguration
}
