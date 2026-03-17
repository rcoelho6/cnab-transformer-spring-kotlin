package com.reicorp.cnab.transformer.infrastructure.datasources

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import com.reicorp.cnab.transformer.domain.entities.enums.RegistersType
import com.reicorp.cnab.transformer.domain.ports.out.RegisterSftpConfigPort
import com.reicorp.cnab.transformer.infrastructure.repositories.FileRegisterRepository
import com.reicorp.cnab.transformer.infrastructure.repositories.models.FileRegisterModel
import org.springframework.stereotype.Component

@Component
open class FileRegisterSftpDataSource(
    private val jpaRepository: FileRegisterRepository
): RegisterSftpConfigPort {

    override fun create(configuration: SftpConfiguration) {
        jpaRepository.save(FileRegisterModel.fromEntity(configuration, RegistersType.SFTP))
    }
}