# Proposta de Classes (apenas nomes e responsabilidades)

Objetivo: listar as principais classes que podem ser implementadas para o sistema CNAB 240 usando arquitetura hexagonal.

Domain (core)
- TransactionRecord (data class) - representa uma transação extraída do CNAB
- CNABFile (data class) - representa o arquivo CNAB consolidado (header, batches, trailers)
- ParserException - exceções do parser
- DomainService (interface) - serviços de domínio (regras complexas)

Ports (interfaces)
- IngestFilePort - interface que representa entrada de arquivo (aceita InputStream ou Path)
- ProcessCnabPort - interface que expõe método para processar CNAB e retornar JSON/objeto
- PusherPort - interface de saída que envia o JSON resultante (send(payload: String))
- StoragePort - interface para salvar/arquivar arquivos recebidos

Application
- ProcessCnabUseCase - implementação do caso de uso que orquestra parsing + transformação + pusher
- CnabDto - DTO resultante consolidado para transporte
- UseCaseException - exceções do nível de aplicação

Adapters - inbound
- RestController (Spring) - endpoint para upload/push via HTTP
- FileWatcherJob - job que observa diretório e chama IngestFilePort

Adapters - outbound
- HttpPusherAdapter - implementa PusherPort usando RestTemplate/WebClient
- JmsPusherAdapter - implementa PusherPort usando JMS
- FileStorageAdapter - implementa StoragePort para salvar arquivos localmente
- S3StorageAdapter - implementa StoragePort para AWS S3

Parser
- Cnab240Parser - classe que recebe linhas posicional e mapeia para TransactionRecord(s)
- Cnab240Mapping - utilitários para mapping de posições para campos

Infra
- SpringConfig - configuração de beans (escolha de pusher, thread pools, etc.)
- JacksonConfig - configuração de ObjectMapper para serialização do JSON final
- ExceptionHandlers - ControllersAdvice para tratar erros e retornar respostas apropriadas

Observações
- Preferir interfaces e testes unitários para o domínio e casos de uso.
- O parser deve ser testado exaustivamente com arquivos de exemplo (unit + integration tests).


