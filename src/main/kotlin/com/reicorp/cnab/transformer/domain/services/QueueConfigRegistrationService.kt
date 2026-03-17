package com.reicorp.cnab.transformer.domain.services

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration
import com.reicorp.cnab.transformer.domain.ports.`in`.RegisterQueueConfigUseCase
import com.reicorp.cnab.transformer.domain.ports.out.RegisterQueueConfigPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QueueConfigRegistrationService(
    private val registerQueueConfigPort: RegisterQueueConfigPort
) : RegisterQueueConfigUseCase {

    @Transactional
    override fun register(queueConfiguration: QueueConfiguration) {
        registerQueueConfigPort.create(queueConfiguration)
    }
}
