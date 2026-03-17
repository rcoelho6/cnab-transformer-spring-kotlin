package com.reicorp.cnab.transformer.domain.services

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import com.reicorp.cnab.transformer.domain.ports.`in`.RegisterSftpConfigUseCase
import com.reicorp.cnab.transformer.domain.ports.out.RegisterSftpConfigPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SftpConfigRegistrationService(
    private val registerSftpConfigPort: RegisterSftpConfigPort
) : RegisterSftpConfigUseCase {

    @Transactional
    override fun register(sftpConfiguration: SftpConfiguration) {

        registerSftpConfigPort.create(sftpConfiguration)
    }
}
