package com.reicorp.cnab.transformer.infrastructure.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * Configuração central do Spring Security.
 *
 * Define a aplicação como um OAuth2 Resource Server que valida tokens JWT.
 * A chave pública para validação é obtida automaticamente do Authorization Server
 * via JWK Set URI configurado em application.yaml.
 *
 * Controle de acesso granular por escopo (scope) é feito via @PreAuthorize
 * nos próprios controllers, usando a anotação @EnableMethodSecurity.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Desabilita CSRF pois a API é stateless (autenticada via token JWT)
            .csrf { it.disable() }

            // Garante que a aplicação não cria sessões HTTP (stateless)
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { auth ->
                // Endpoints públicos de monitoramento e saúde
                auth.requestMatchers(
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()

                // Todos os demais endpoints exigem autenticação via JWT
                auth.anyRequest().authenticated()
            }

            // Configura a aplicação como Resource Server OAuth2 com validação de JWT
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }

        return http.build()
    }
}
