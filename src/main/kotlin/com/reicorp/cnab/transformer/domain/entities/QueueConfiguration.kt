package com.reicorp.cnab.transformer.domain.entities

import java.util.UUID

class QueueConfiguration(
    val clientUuid: UUID,
    val queueName: String
) { }
