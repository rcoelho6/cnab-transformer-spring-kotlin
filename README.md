# transformer-spring-kotlin

Estrutura proposta: criação de pacotes para suportar uma arquitetura hexagonal (Ports & Adapters) para o processamento de arquivos CNAB 240.

Motivação
- Separar regras de negócio (domínio) de detalhes de infraestrutura (adapters) para facilitar testes, manutenção e evolução.
- Permitir múltiplos adaptadores de entrada (HTTP REST, filewatch/job) e saída (HTTP, JMS, filas) sem alterar o núcleo.

Estrutura criada em `src/main/kotlin/com/reicorp/cnab/transformer_spring_kotlin`

- domain.model - Entidades e value objects
- domain.service - Serviços do domínio (regras que não pertencem a entidades)
- application - Casos de uso, DTOs de aplicação e orquestração
- ports.in - Interfaces de entrada (use cases) que adapters inbound chamam
- ports.out - Interfaces de saída (pusher, storage) que adapters outbound implementam
- adapters.inbound.rest - Controllers e handlers REST
- adapters.inbound.file - Filewatch / job para processamento em lote
- adapters.outbound.pusher.http - Implementação HTTP do pusher
- adapters.outbound.pusher.jms - Implementação JMS do pusher
- adapters.outbound.storage - Persistência/arquivamento dos arquivos
- parser - Lógica de parsing do CNAB 240 para modelos de domínio
- pusher - Strategy/factory para envio do JSON consolidado
- infra - Configurações e utilitários (beans do Spring, converters)

Por que cada pacote existe
- domain.*: núcleo puro, sem dependências de frameworks
- application: orquestração de casos de uso, entrada para adapters
- ports: definições de contratos que preservam isolamento do núcleo
- adapters: implementações concretas que conversam com o mundo externo
- parser/pusher: responsabilidades técnicas claras (parser transforma posicional em modelo, pusher envia JSON resultante)

Como seguir com a implementação
1. Implementar os ports (interfaces) em `ports.*`.
2. Implementar os casos de uso em `application` que dependem apenas das `ports`.
3. Implementar `parser` para mapear CNAB 240 para modelos de domínio.
4. Implementar adaptadores inbound/outbound e injetá-los com Spring.


