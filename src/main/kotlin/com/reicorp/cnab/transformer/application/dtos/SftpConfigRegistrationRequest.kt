package com.reicorp.cnab.transformer.application.dtos

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class SftpConfigRegistrationRequest(

    @field:NotNull(message = "O campo clientUuid é obrigatório.")
    val clientUuid: UUID,

    @field:NotBlank(message = "O campo serverAddress é obrigatório.")
    val serverAddress: String,

    @field:NotNull(message = "O campo port é obrigatório.")
    @field:Min(value = 1, message = "A porta deve ser maior que 0.")
    @field:Max(value = 65535, message = "A porta deve ser menor ou igual a 65535.")
    val port: Int,

    @field:NotBlank(message = "O campo clientId é obrigatório.")
    val clientId: String,

    @field:NotBlank(message = "O campo clientSecret é obrigatório.")
    val clientSecret: String,

    val publicCertificate: String? = null,

    @field:NotBlank(message = "O campo remotePath é obrigatório.")
    val remotePath: String
)
