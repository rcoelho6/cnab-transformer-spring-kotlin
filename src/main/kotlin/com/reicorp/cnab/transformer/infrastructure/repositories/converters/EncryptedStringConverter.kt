package com.reicorp.cnab.transformer.infrastructure.repositories.converters

import jakarta.annotation.PostConstruct
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Component

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
@Component
class EncryptedStringConverter(
    @Value("\${app.security.encryption.password}") private val password: String,
    @Value("\${app.security.encryption.salt}") private val salt: String
) : AttributeConverter<String, String> {

    private lateinit var encryptor: TextEncryptor

    @PostConstruct
    fun init() {
        // Encryptors.stronger usa AES-256-GCM + PBKDF2.
        // O salt deve ser uma string hexadecimal de pelo menos 8 bytes (16 chars hex).
        encryptor = Encryptors.stronger(password, salt)
        // Registra a instância para uso pelo Hibernate (que instancia conversores sem Spring)
        instance = this
    }

    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null) return null
        return encryptor.encrypt(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) return null
        return encryptor.decrypt(dbData)
    }

    companion object {
        // Instância estática para que o Hibernate possa acessar o bean gerenciado pelo Spring
        @Volatile
        private var instance: EncryptedStringConverter? = null

        fun getInstance(): EncryptedStringConverter =
            instance ?: throw IllegalStateException(
                "EncryptedStringConverter não foi inicializado pelo Spring. " +
                "Verifique se o bean está sendo criado corretamente."
            )
    }
}
