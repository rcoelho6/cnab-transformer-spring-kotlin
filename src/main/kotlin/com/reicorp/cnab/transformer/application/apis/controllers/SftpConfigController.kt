package com.reicorp.cnab.transformer.application.apis.controllers

import com.reicorp.cnab.transformer.application.dtos.SftpConfigRegistrationRequestDto
import com.reicorp.cnab.transformer.domain.ports.`in`.RegisterSftpConfigUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sftp-configs")
class SftpConfigController(
    private val registerSftpConfigUseCase: RegisterSftpConfigUseCase
) {

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sftp:write')")
    fun register(@Valid @RequestBody request: SftpConfigRegistrationRequestDto): ResponseEntity<Void> {
        registerSftpConfigUseCase.register(request.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
