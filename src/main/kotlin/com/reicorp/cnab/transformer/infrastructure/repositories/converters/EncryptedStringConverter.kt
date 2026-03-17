package com.reicorp.cnab.transformer.infrastructure.repositories.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor

/**
 * Conversor JPA que criptografa e descriptografa campos String sensíveis no banco de dados.
 *
 * Utiliza [Encryptors.stronger] do Spring Security Crypto, que implementa
 * AES-256 no modo GCM (Galois/Counter Mode) com derivação de chave via PBKDF2.
 *
 * Características de segurança:
 * - AES-256-GCM: fornece confidencialidade E integridade (Authenticated Encryption).
 * - PBKDF2: a chave real é derivada da senha configurada, dificultando ataques de força bruta.
 * - IV aleatório por operação: cada criptografia produz um ciphertext diferente,
 *   impedindo análise de padrões no banco de dados.
 *
 * A senha e o salt são injetados via variáveis de ambiente (ver application.yaml).
 */
@Converter
class EncryptedStringConverter(
    private var encryptor: TextEncryptor
) : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null)
            return null
        return encryptor.encrypt(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) return null
            return encryptor.decrypt(dbData)
    }
}
