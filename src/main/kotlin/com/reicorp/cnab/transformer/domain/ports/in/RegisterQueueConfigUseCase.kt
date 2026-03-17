package com.reicorp.cnab.transformer.domain.ports.`in`

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration

interface RegisterQueueConfigUseCase {
    fun register(queueConfiguration: QueueConfiguration)
}
