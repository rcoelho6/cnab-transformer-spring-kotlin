package com.reicorp.cnab.transformer.application.apis.controllers

import com.reicorp.cnab.transformer.application.dtos.QueueConfigRegistrationRequestDto
import com.reicorp.cnab.transformer.domain.ports.`in`.RegisterQueueConfigUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/queue-configs")
class QueueConfigController(
    private val registerQueueConfigUseCase: RegisterQueueConfigUseCase
) {

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_queue:write')")
    fun register(@Valid @RequestBody request: QueueConfigRegistrationRequestDto): ResponseEntity<Void> {
        registerQueueConfigUseCase.register(request.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
