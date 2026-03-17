package com.reicorp.cnab.transformer.domain.entities

import java.util.*

class SftpConfiguration(
    val clientUuid: UUID,
    val serverAddress: String,
    val port: Int,
    val clientId: String,
    val clientSecret: String,
    val publicCertificate: String?,
    val remotePath: String
) { }
