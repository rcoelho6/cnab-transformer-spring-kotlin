package com.reicorp.cnab.transformer.domain.ports.out

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration

interface RegisterQueueConfigPort {
    fun create(queueConfiguration: QueueConfiguration)
}
