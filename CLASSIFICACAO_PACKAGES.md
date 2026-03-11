Resumo (início solicitado)

- Opção A — 3 grupos: Core (Domínio) / Application & Ports / Adapters & Infra
1) Core (Domínio)
2) Application / Ports
3) Adapters / Infra
- Opção B — 4 grupos: Domain / Parser & Technical Core / Application & Ports / Adapters & Infra
1) Domain (Domínio puro)
2) Parser / Technical Core
3) Application / Ports
4) Adapters & Infra

Proposta detalhada de classificação dos packages

Objetivo
- Apresentar duas opções de agrupamento dos packages já criados, começando com um resumo contendo apenas o nome da opção e o tópico de cada uma (conforme solicitado), seguido da proposta detalhada e recomendações.

Opção A — 3 grupos (compacta)
1) Core (Domínio)
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.domain.model`
     - `com.reicorp.cnab.transformer_spring_kotlin.domain.service`
     - `com.reicorp.cnab.transformer_spring_kotlin.parser`
   - Descrição: modelos de domínio, regras de negócio puras e o parser que mapeia CNAB posicional para objetos do domínio. Tudo deve permanecer livre de dependências de infraestrutura.

2) Application / Ports
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.application`
     - `com.reicorp.cnab.transformer_spring_kotlin.ports.in`
     - `com.reicorp.cnab.transformer_spring_kotlin.ports.out`
     - `com.reicorp.cnab.transformer_spring_kotlin.pusher`
   - Descrição: casos de uso, DTOs de aplicação e contratos (ports) que descrevem as dependências externas necessárias (pusher, storage, etc.).

3) Adapters / Infra
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.inbound.rest`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.inbound.file`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.pusher.http`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.pusher.jms`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.storage`
     - `com.reicorp.cnab.transformer_spring_kotlin.infra`
   - Descrição: implementações concretas e integração com frameworks e I/O (Spring MVC, JMS, filesystem, S3, etc.).

Dependências permitidas (Opção A)
- Adapters/Infra -> Application/Ports -> Core
- Application pode usar Core
- Core não depende de Application nem de Adapters


Opção B — 4 grupos (recomendada)
1) Domain (Domínio puro)
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.domain.model`
     - `com.reicorp.cnab.transformer_spring_kotlin.domain.service`
   - Descrição: entidades, value objects e regras puras do negócio.

2) Parser / Technical Core
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.parser`
     - `com.reicorp.cnab.transformer_spring_kotlin.pusher`
   - Descrição: componentes técnicos essenciais que ainda fazem parte do núcleo funcional (o parser que transforma o CNAB em modelos e o pusher que prepara/enfileira o payload). Mantém-se próximo ao domínio mas separado por clareza técnica.

3) Application / Ports
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.application`
     - `com.reicorp.cnab.transformer_spring_kotlin.ports.in`
     - `com.reicorp.cnab.transformer_spring_kotlin.ports.out`
   - Descrição: casos de uso que orquestram parser + domínio + pusher/storage e definem contratos (ports).

4) Adapters & Infra
   - Packages incluídos:
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.inbound.rest`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.inbound.file`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.pusher.http`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.pusher.jms`
     - `com.reicorp.cnab.transformer_spring_kotlin.adapters.outbound.storage`
     - `com.reicorp.cnab.transformer_spring_kotlin.infra`
   - Descrição: todos os componentes dependentes de frameworks e infraestrutura.

Dependências permitidas (Opção B)
- Adapters & Infra -> Application/Ports -> (Domain + Parser/Technical Core)
- Application pode usar Domain e Parser/Pusher
- Parser/Pusher podem usar Domain, mas não dependem de Adapters/Infra
- Domain não depende de nada externo

Recomendações e observações
- Recomendo a Opção B (4 grupos): é mais explícita e ajuda a separar responsabilidades técnicas (parser/pusher) do domínio puramente conceitual.
- Defina regras de dependência no CI (ex.: com ArchUnit ou revisões de PR) para evitar importações indevidas entre grupos.
- Coloque bootstrap e configuração do Spring em `infra` (ou num package `entrypoint` se preferir separar o ponto de boot do resto da infra).
- Mantenha `ports` estritamente como interfaces; tudo concreto fica em `adapters`.

Como proceder
- Se desejar, posso gerar um `diagram.svg` ou um `diagram.txt` ASCII simples mostrando as setas de dependência.
- Posso também criar skeletons de interfaces para `ports` e o esqueleto de `ProcessCnabUseCase` em `application` para tornar a arquitetura imediatamente utilizável.


