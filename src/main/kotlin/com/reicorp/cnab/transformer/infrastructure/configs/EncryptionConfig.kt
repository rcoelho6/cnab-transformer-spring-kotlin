package com.reicorp.cnab.transformer.infrastructure.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor

/**
 * Configuração do bean de criptografia simétrica da aplicação.
 *
 * Expõe um [TextEncryptor] configurado com AES-256-GCM + PBKDF2,
 * disponível para injeção em qualquer componente que precise criptografar
 * dados fora do contexto JPA (ex: logs, respostas de API, etc.).
 *
 * Para a criptografia de campos JPA, o [EncryptedStringConverter] usa
 * sua própria instância interna com os mesmos parâmetros.
 *
 * Variáveis de ambiente necessárias:
 * - ENCRYPTION_PASSWORD: senha mestra para derivação da chave.
 * - ENCRYPTION_SALT: salt hexadecimal (mínimo 16 caracteres hex / 8 bytes).
 *   Gere um salt seguro com: KeyGenerators.string().generateKey()
 */
@Configuration
class EncryptionConfig(
    @Value("\${app.security.encryption.password}") private val password: String,
    @Value("\${app.security.encryption.salt}") private val salt: String
) {

    @Bean
    fun textEncryptor(): TextEncryptor = Encryptors.delux(password, salt)
}
