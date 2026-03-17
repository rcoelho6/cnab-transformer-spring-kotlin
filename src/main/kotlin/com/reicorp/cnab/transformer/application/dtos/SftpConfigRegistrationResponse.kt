package com.reicorp.cnab.transformer.application.dtos

import java.util.UUID

data class SftpConfigRegistrationResponse(
    val id: UUID,
    val clientUuid: UUID,
    val serverAddress: String,
    val port: Int,
    val remotePath: String
)
