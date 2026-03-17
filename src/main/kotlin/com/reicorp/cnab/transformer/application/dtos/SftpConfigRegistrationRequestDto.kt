package com.reicorp.cnab.transformer.application.dtos

import com.reicorp.cnab.transformer.domain.entities.SftpConfiguration
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class SftpConfigRegistrationRequestDto(

    @NotNull(message = "O campo clientUuid é obrigatório.")
    val clientUuid: UUID,
    @NotBlank(message = "O campo serverAddress é obrigatório.")
    val serverAddress: String,
    @NotNull(message = "O campo port é obrigatório.")
    @Min(value = 1, message = "A porta deve ser maior que 0.")
    @Max(value = 65535, message = "A porta deve ser menor ou igual a 65535.")
    val port: Int,
    @NotBlank(message = "O campo clientId é obrigatório.")
    val clientId: String,
    @NotBlank(message = "O campo clientSecret é obrigatório.")
    val clientSecret: String,
    val publicCertificate: String? = null,
    @NotBlank(message = "O campo remotePath é obrigatório.")
    val remotePath: String
) {

    fun toEntity() = SftpConfiguration(
        clientUuid = this.clientUuid,
        serverAddress = this.serverAddress,
        port = this.port,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        publicCertificate = this.publicCertificate,
        remotePath = this.remotePath
    );
}
