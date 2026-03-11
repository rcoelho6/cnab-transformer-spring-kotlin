# Arquitetura Hexagonal aplicada ao componente

Resumo rápido
- A Arquitetura Hexagonal (Ports & Adapters) separa o núcleo da aplicação de detalhes de infraestrutura. O núcleo (Application + Domain + Technical Core) expõe portas (interfaces) que são implementadas por adapters na camada de Infra.

Mapeamento para este componente
- Núcleo (hexágono interno): `domain`, `parser` (technical core) e `application`.
- Ports (interfaces): `ports.in` (entrada) e `ports.out` (saída). Exemplos: `ProcessCnabPort` (in), `StoragePort` (out), `PusherPort` (out), `NotifierPort` (out).
- Adapters (outside): `adapters.inbound.rest`, `adapters.inbound.file`, `adapters.outbound.storage`, `adapters.outbound.pusher.*`.

Diagrama Hexagonal (Mermaid)

```mermaid
graph LR
  subgraph Core[Core (Domain + Application + Parser)]
    UC[ProcessCnabUseCase]
  end

  UC -- calls --> PortStorage[StoragePort]
  UC -- calls --> PortPusher[PusherPort]
  UC -- calls --> PortNotifier[NotifierPort]

  subgraph Adapters[Adapters/Infra]
    AdapterHTTP[HTTP Controller] -.-> UC
    AdapterSFTP[SFTP/FileWatch] -.-> UC
    AdapterStorage[S3/Filesystem] --> PortStorage
    AdapterPusherHTTP[Pusher HTTP] --> PortPusher
    AdapterJMS[Pusher JMS] --> PortPusher
    AdapterNotifier[Webhook Sender] --> PortNotifier
  end

  AdapterHTTP -. triggers .-> UC
  AdapterSFTP -. triggers .-> UC
```

Port examples (interfaces)

```kotlin
interface StoragePort {
  fun storeRawFile(clientUuid: UUID, fileName: String, bytes: ByteArray): StorageResult
}

interface PusherPort {
  fun enqueue(jsonPayload: JsonNode): EnqueueResult
}

interface NotifierPort {
  fun sendWebhook(callbackUrl: String, body: String, headers: Map<String,String>)
}
```

Como os adapters interagem
- Adapters inbound recebem eventos/requests e convertem para chamadas a `Application` (use-cases) via `ports.in`.
- Application executa o fluxo (parse -> domain -> enqueue) e chama `ports.out` para persistência/entrega.
- Adapters outbound implementam `ports.out` e cuidam de detalhes (retries, backoff, serialization, auth).

Vantagens dessa abordagem
- Testabilidade: núcleo testado isoladamente com mocks de ports.
- Independência de frameworks: domínio e parser não dependem de Spring, JMS ou bibliotecas infra.
- Facilidade para trocar implementações (ex.: trocar S3 por outro storage) sem tocar o núcleo.

