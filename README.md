# CNAB Transformer (Spring + Kotlin)

Este projeto propõe uma estrutura robusta para o processamento de arquivos **CNAB 240**, utilizando os princípios da **Arquitetura Hexagonal (Ports & Adapters)**. O objetivo principal é garantir que o núcleo de negócio seja isolado de detalhes técnicos e de infraestrutura.

## Arquitetura do Projeto

A arquitetura hexagonal permite que a aplicação seja testável e independente de frameworks, bancos de dados ou interfaces externas.

![Arquitetura Hexagonal](documentations/images/hexagonal_architecture.jpg)

### Camadas Principais

| Camada | Responsabilidade |
| :--- | :--- |
| **Domain** | Núcleo puro contendo entidades, value objects e serviços de domínio. Sem dependências de frameworks. |
| **Application** | Orquestração de casos de uso, DTOs e lógica de aplicação. |
| **Ports** | Definições de contratos (interfaces) que preservam o isolamento do núcleo. |
| **Adapters** | Implementações concretas (inbound/outbound) que conversam com o mundo externo. |

## Fluxo de Processamento

O processamento de um arquivo CNAB segue um fluxo linear e bem definido através das camadas:

![Fluxo de Processamento](documentations/images/class_flow.jpg)

1.  **Entrada**: O arquivo é recebido via REST ou detectado por um watcher de diretório.
2.  **Caso de Uso**: A aplicação orquestra o início do processamento.
3.  **Parser**: Transforma o conteúdo posicional do CNAB em modelos de domínio.
4.  **Lógica de Domínio**: Aplica as regras de negócio necessárias.
5.  **Saída**: O resultado é enviado através de uma porta de saída para um adaptador (JMS, HTTP ou Storage).

## Estrutura de Pacotes

A organização dos pacotes em `src/main/kotlin/com/reicorp/cnab/transformer` reflete essa arquitetura:

-   `domain.entities`: Entidades e objetos de valor.
-   `domain.services`: Serviços com regras de negócio complexas.
-   `application`: Casos de uso e orquestração.
-   `domain.ports.in`: Interfaces chamadas por adaptadores de entrada.
-   `domain.ports.out`: Interfaces implementadas por adaptadores de saída.
-   `infrastructure`: Implementações concretas (REST, JMS, Repositórios, Mappers).

## Motivação

-   **Isolamento**: Separar regras de negócio de detalhes de infraestrutura.
-   **Flexibilidade**: Permitir múltiplos adaptadores sem alterar o núcleo.
-   **Testabilidade**: Facilitar a criação de testes unitários e de integração.

## Como Contribuir

1.  Implemente os contratos em `domain.ports.*`.
2.  Desenvolva os casos de uso em `application`.
3.  Crie a lógica de parsing para mapear o CNAB 240.
4.  Implemente os adaptadores necessários na camada de `infrastructure`.

## Segurança (OAuth2)

O projeto utiliza **Spring Security com OAuth2 Client Credentials** para proteger os endpoints de API, especialmente aqueles que lidam com dados sensíveis, como o cadastro de configurações SFTP.

### Como Funciona o Fluxo Client Credentials

O fluxo *Client Credentials* é ideal para comunicação máquina-a-máquina (M2M), onde não há um usuário final envolvido, mas sim um sistema cliente (ex: um backoffice ou outro microserviço) acessando a API.

1. **Registro Prévio**: O sistema cliente recebe previamente um `client_id` e um `client_secret` do Authorization Server (ex: Keycloak, AWS Cognito, Auth0).
2. **Solicitação de Token**: O cliente faz uma requisição POST para o endpoint `/token` do Authorization Server, enviando suas credenciais (geralmente via Basic Auth) e solicitando o `grant_type=client_credentials`.
3. **Emissão do Token**: Se as credenciais forem válidas, o Authorization Server retorna um JWT (JSON Web Token) de acesso.
4. **Acesso à API**: O cliente faz a requisição para a nossa API (ex: `POST /api/v1/sftp-configs`), incluindo o token no cabeçalho: `Authorization: Bearer <token>`.
5. **Validação**: O Spring Security na nossa aplicação intercepta a requisição, valida a assinatura do JWT (usando a chave pública do Authorization Server) e verifica se o token não expirou e possui os escopos necessários.

### Como Gerar o Token (Exemplo)

Para testar localmente ou integrar sistemas, você precisará gerar o token no seu Authorization Server. Abaixo um exemplo genérico usando `curl`:

```bash
curl -X POST "https://seu-auth-server.com/oauth/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -u "seu-client-id:seu-client-secret" \
     -d "grant_type=client_credentials" \
     -d "scope=sftp.write"
```

A resposta será um JSON contendo o `access_token`:

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "sftp.write"
}
```

Utilize este `access_token` nas requisições para a API do CNAB Transformer.
