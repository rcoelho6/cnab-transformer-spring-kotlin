package com.reicorp.cnab.transformer.infrastructure.datasources

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration
import com.reicorp.cnab.transformer.domain.entities.enums.RegistersType
import com.reicorp.cnab.transformer.domain.ports.out.RegisterQueueConfigPort
import com.reicorp.cnab.transformer.infrastructure.repositories.FileRegisterRepository
import com.reicorp.cnab.transformer.infrastructure.repositories.models.FileRegisterModel
import org.springframework.stereotype.Component

@Component
open class FileRegisterQueueDataSource(
    private val jpaRepository: FileRegisterRepository
) : RegisterQueueConfigPort {

    override fun create(queueConfiguration: QueueConfiguration) {
        jpaRepository.save(FileRegisterModel.fromEntity(queueConfiguration, RegistersType.QUEUE))
    }
}
