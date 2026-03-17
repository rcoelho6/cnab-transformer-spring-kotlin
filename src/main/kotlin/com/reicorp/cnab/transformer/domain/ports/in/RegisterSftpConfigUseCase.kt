package com.reicorp.cnab.transformer.domain.ports.`in`

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration

/**
 * Porta de entrada (Use Case) para o cadastro de uma nova configuração SFTP.
 *
 * Define o contrato que o Controller (camada Application) deve invocar.
 * A implementação reside em domain.services.
 */
interface RegisterSftpConfigUseCase {
    fun register(sftpConfiguration: SftpConfiguration)
}
