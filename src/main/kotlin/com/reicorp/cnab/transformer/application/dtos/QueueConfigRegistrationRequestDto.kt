package com.reicorp.cnab.transformer.application.dtos

import com.reicorp.cnab.transformer.domain.entities.QueueConfiguration
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class QueueConfigRegistrationRequestDto(

    @NotNull(message = "O campo clientUuid é obrigatório.")
    val clientUuid: UUID,

    @NotBlank(message = "O campo queueName é obrigatório.")
    val queueName: String
) {

    fun toEntity() = QueueConfiguration(
        clientUuid = this.clientUuid,
        queueName = this.queueName
    )
}
