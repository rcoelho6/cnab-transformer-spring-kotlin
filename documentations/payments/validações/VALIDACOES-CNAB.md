# VALIDAÇÕES CNAB — GUIA COMPLETO PRÉ-PARSER (AMPLIADO)

Próxima ação: vou descrever de forma completa todas as validações que devem ser aplicadas ao arquivo CNAB antes do parser, incluindo regras por campo, regexes, algoritmos (CPF/CNPJ), validações de boletos, PIX, TED, tributos, taxas veiculares e DDA; e requisitos operacionais das APIs (headers e idempotência) para integração com o Santander.

## Plano e checklist (o que este documento cobre)
- [x] Severidades e ação esperada (FATAL/WARN/INFO)
- [x] Validações de arquivo (estrutura, encoding, tamanho)
- [x] Validações do File Header (registro 0)
- [x] Validações de Batch Header (registro 1) e Batch Trailer (registro 5)
- [x] Validações de Detalhe (registro 3) — estrutura, campos obrigatórios
- [x] Validações por tipo de pagamento: PIX, BOLETO/CÓDIGO-BARRAS, TED, TRIBUTOS, TAXA-VEICULAR, DDA
- [x] Validações de campos comuns: CPF/CNPJ (algoritmo), valores, datas, barcode checksum, contas/agências
- [x] Regras operacionais: idempotência, duplicados, DLQ, retries
- [x] Regras para integração com APIs Santander (headers, idempotency, content-type)
- [x] Catálogo de códigos de erro e formato do relatório de validação
- [x] Pseudocódigo e exemplos de mensagens de erro

---

## Severidade (níveis e ações)

| Símbolo | Nível | Ação recomendada |
|---|---:|---|
| 🔴 | FATAL | Abort parse; mover arquivo para quarantine/DLQ; notificar time; investigação manual |
| 🟠 | WARNING | Registrar, segregar registros afetados, processar o resto com atenção; gerar relatório |
| 🟢 | INFO | Ajuste automático possível (normalização), registrar informação |

---

## 1. Validações de arquivo (pré‑processo)

1.1 Estrutura e tamanho
- Verificar que o tamanho total do arquivo é múltiplo de 240 bytes.
  - Condição: fileSize % 240 == 0
  - Severidade: 🔴 (FATAL)
- Verificar que o arquivo não esteja vazio.
  - Severidade: 🔴
- Verificar que cada registro (linha) lido tenha exatamente 240 bytes.
  - Severidade: 🔴

1.2 Encoding
- Recomendado: ISO-8859-1 (Latin-1) por padrão para CNAB; se layout do banco indicar UTF-8, use UTF-8.
- Se detectar outro encoding, tentar conversão e logar (🟠). Falha de conversão -> 🔴.

1.3 Quebras e EOF
- Remover eventual CRLF sobrando ao final sem alterar registros (🟠).
- Verificar permissões de leitura (🔴 se não legível).

1.4 Checksum/assinatura (se aplicável)
- Se o sistema exige verificação de assinatura/checksum externo, validar antes do parse (🔴 se falhar).

---

## 2. Validações no File Header (registro tipo 0)

Campos essenciais e validações detalhadas

| Campo | Posição (ex) | Regra | Severidade |
|---|---:|---|---:|
| registroTipo | pos 1 | deve ser '0' | 🔴 |
| bankCode | pos 2-4 | 3 dígitos; se houver expectativa (ex.: 033 para Santander) validar igualdade | 🔴 |
| fileDate | pos 95-102 (ex) | formato YYYYMMDD; plausibilidade (não muito futuro) | 🟠/🔴 |
| layoutVersion | pos (depend) | validar versão suportada (ex.: 240) | 🟠 |
| filler | restante | caracteres permitidos (espaco/0) | 🟢 |

Notas:
- As posições exatas dependem do layout; consulte o PDF/BankProfile.
- Se `bankCode` difere do esperado: política local — WARN (aceitar) ou FATAL (rejeitar).

---

## 3. Validações por Lote (Batch Header 1 / Batch Trailer 5)

3.1 Batch Header
- registroTipo == '1' (🔴)
- batchNumber: inteiro positivo, preferencialmente sequencial (WARN se duplicado)
- serviceType: concorda com tipo de serviço (se aplicável) (WARN)
- companyId / companyDocument: CPF/CNPJ válido (🔴 se obrigatório e ausente)

3.2 Batch Trailer
- registroTipo == '5' (🔴)
- detailCount (campo trailer) deve ser >=0; verificar igual ao número real de detalhes no lote (🔴 se diferente)
- se presentes somatórios (valorTotal): comparar com soma de `amount` dos detalhes; diferença -> 🟠 (WARN) e investigar

Regra forte: cada lote deve conter um único tipo de pagamento (enforce)
- Verificação: agrupar os segments/details por tipo; se houver mix -> 🔴 (FATAL)
- Motivo: layout CNAB e validações do banco normalmente esperam homogeneidade por lote

---

## 4. Validações de Detalhe (registro tipo 3) — genéricas e de integridade

4.1 Estrutura
- registroTipo == '3' (🔴)
- segmento (pos14) deve pertencer ao conjunto aceito pelo layout (ex.: P,Q,T,U,Z) (🔴)
- linhas com comprimento !=240 -> 🔴

4.2 Campos obrigatórios (genéricos)
- amount: inteiro (centavos) > 0 (🔴)
- date: formato YYYYMMDD ou ISO, plausível (🔴)
- payer.name: não vazio (🟠)
- payer.document: CPF (11) ou CNPJ (14) válido (🔴)
- requestId: se presente, validar formato (UUID) — se o sistema exige idempotência, exigir (🔴)

4.3 Charset e trimming
- Remover caracteres de controle; validar que strings estejam no charset aceito
- Normalizar espaços; não remover zeros significativos

4.4 Segment vs paymentType
- Validar que `segment` e `paymentType` sejam consistentes: ex. segmento P/Q -> BOLETO; segmento usado para PIX conforme bankProfile -> PIX (🔴 se inconsistente)

---

## 5. Validações EXAUSTIVAS por tipo de pagamento

> Observação: abaixo as validações são abrangentes — alguns campos/limites dependem do banco; inclua regras específicas do `BankProfile` e PDFs do banco (Santander) como última referência.

### A. PIX
Campos esperados (extra): `pixKey` OR `txid` (um dos dois obrig.)

Validações:
- Se `pixKey`:
  - detect tipo: email / phone / CPF / CNPJ / EVP
  - regex/validações:
    - email: `^[^@\s]+@[^@\s]+\.[^@\s]+$`
    - phone (E.164): `^\+?[1-9][0-9]{7,14}$`
    - CPF: `^\d{11}$` + algoritmo módulo 11 (ver função `validateCPF` abaixo)
    - CNPJ: `^\d{14}$` + algoritmo módulo 11 (ver `validateCNPJ`)
    - UUID (txid): `^[0-9a-fA-F\-]{36}$` (se for EVP/txid)
- `amount` > 0
- `valueDate` não pode ser muito no futuro (policy)
- Limites: checar `maxPerPayment` e `dailyLimit` (configurável) — caso ultrapasse, ERROR/REJECT ou require approval
- Duplicidade: se `txid` já existe e status final é LIQUIDADO => 🔴 duplicate

### B. BOLETO / CÓDIGO DE BARRAS
Campos esperados: `barcode` (44 dígitos) preferencialmente; se ausente `ourNumber`+`documentNumber`

Validações:
- `barcode` apenas dígitos, length == 44 (🔴)
- Checksum do código de barras: módulo 10/11 conforme documentação de cobrança — usar biblioteca de boletos local (🔴 se inválido)
- `dueDate` deve existir e ser plausível (🔴)
- `payer.document` validar CPF/CNPJ
- Comprimento de campos textuais (nome sacado) não exceder limites do layout — WARN se exceder e cortar/truncar se policy permitir

### C. TED
Campos esperados: `bankCode` (3), `agency`, `account`, `holderName`, `holderDocument`

Validações:
- `bankCode`: 3 dígitos; verificar se está no catálogo de bancos (opcional) — WARN se desconhecido
- agencia/account: remover formatações e verificar dígitos; validar check digit se aplicável (por banco) — WARN/🔴
- `holderDocument`: validar CPF/CNPJ
- `amount` e `date`: plausíveis

### D. TRIBUTOS
Campos: `taxCode` (obrig.), `taxpayerDocument`, `reference`

Validações:
- `taxCode` comparar com catálogo interno (CND/DARF/GPS/...), se desconhecido -> 🟠 ou 🔴 conforme política
- `taxpayerDocument`: validar CNPJ/CPF
- `amount` não negativo; validar regras de tributo (algumas guias exigem competências, format)

### E. TAX_VEHICLE
Campos: `vehicleId` (placa/RENAVAM), `taxCode`

Validações:
- `vehicleId`: placa regex (ex.: `^[A-Z]{3}\d{4}$` BR padrão) ou RENAVAM numérico length conforme padrão local
- `taxCode`: reconhecido (ex.: IPVA, LICENCIAMENTO)

### F. DDA
Campos: `nossoNumero`, `titleNumber`, possivelmente status

Validações:
- `nossoNumero` presente (identificador do título) — 🔴 se ausente
- `titleNumber`: plausível
- `amount` e `dueDate` coerentes

---

## 6. Validações de campos comuns e algoritmos (detalhado)

### 6.1 CPF — algoritmo módulo 11 (pseudo)

```kotlin
fun validateCPF(cpf: String): Boolean {
  val numbers = cpf.filter { it.isDigit() }
  if (numbers.length != 11) return false
  // calc first verifier
  var sum = 0
  for (i in 0..8) sum += (numbers[i].digitToInt()) * (10 - i)
  var rest = sum % 11
  val dv1 = if (rest < 2) 0 else 11 - rest
  // calc second verifier
  sum = 0
  for (i in 0..9) sum += (numbers[i].digitToInt()) * (11 - i)
  rest = sum % 11
  val dv2 = if (rest < 2) 0 else 11 - rest
  return numbers[9].digitToInt() == dv1 && numbers[10].digitToInt() == dv2
}
```

### 6.2 CNPJ — algoritmo módulo 11 (pseudo)
- Similar ao CPF, mas com pesos diferentes (seguir especificação). Use biblioteca confiável para CNPJ.

### 6.3 Código de Barras (Boleto) — validação
- Verificar 44 dígitos; validar campo verificador (módulo 10 ou 11 conforme banco/convênio)
- Recomenda-se usar implementação consolidada (biblioteca `boleto` ou util interno) e não implementar algoritmo manualmente sem testes.

### 6.4 PIX key formats
- Ver regexes e validações:
  - Email: `^[^@\s]+@[^@\s]+\.[^@\s]+$`
  - Phone (E.164): `^\+?[1-9][0-9]{7,14}$`
  - CPF: `^\d{11}$` + validação algoritmo
  - CNPJ: `^\d{14}$` + validação
  - EVP/UUID: `^[0-9a-fA-F\-]{36}$` (ou padrão do PSP)

### 6.5 Bank code / agency / account
- bankCode: 3 digits (numeric)
- agency/account: remover separadores; validar length; validar check digit se layout exigir

### 6.6 Amounts / currencies
- amount: inteiro em centavos (no JSON) — > 0
- currency: default "BRL"; se diferente, validar com o layout/API

---

## 7. Contagens, somatórios e trailers (detalhado)

- Recalcular sempre detailCount por lote a partir da leitura real.
- Recalcular recordCount do arquivo: 1 (file header) + sum(for each batch: 1 batch header + details.size + 1 batch trailer) + 1 file trailer.
- Se trailer do banco contiver somatório de valores, comparar com soma local e:
  - diferença 0 -> OK
  - diferença pequena (ex.: 1 cent) -> WARN (ex.: rounding)
  - diferença grande -> 🔴 (investigar)

---

## 8. Idempotência, duplicates e requestId (política prática)

- Requer `requestId` ou `Idempotency-Key` por detalhe preferencialmente.
- Armazenar estado por `requestId` (PENDING/PROCESSING/SUCCESS/FAILED).
- Políticas:
  - se novo requestId já em SUCCESS -> retornar idempotent response (não reenfileirar)
  - se em PENDING/PROCESSING -> tratar como retry (logar)
  - se ausente requestId -> gerar hash (concatenar campos-chave) e WARN

---

## 9. Requisitos de integração com APIs Santander (headers e práticas)

Ao enviar requests ao Santander (p.ex. via REST para registrar pagamentos), os headers e práticas comuns são:

| Header | Exemplo | Nota |
|---|---|---|
| Authorization | Bearer <token> | OAuth2 Client Credentials / token válido (obrigatório)
| X-Request-Id | UUID | Correlation + idempotency auxiliar
| Idempotency-Key | UUID | Para endpoints que suportam idempotência (ex.: POST pagamentos)
| Content-Type | application/json | JSON body expected

Boas práticas
- Gerar `Idempotency-Key` por operação de escrita (create payment/charge)
- Incluir `X-Request-Id` para rastreabilidade em logs e webhooks
- Respeitar rate limits (consultar Developer Portal do Santander) e detectar 429 para backoff

Referências locais:
- `documentations/payments/santander-openworld/*/ENDPOINTS-SUMMARY.md` (ex.: PIX-TRANSFERS/ENDPOINTS-SUMMARY.md) — contém exemplos de headers

---

## 10. Códigos de erro sugeridos (catálogo)

Use códigos curtos e consistentes para o relatório de validação.

| Code | Level | Descrição |
|---|---:|---|
| FILE_SIZE_NOT_MULTIPLE_OF_240 | FATAL | Arquivo não tem múltiplo de 240 bytes |
| MISSING_FILE_HEADER | FATAL | Header do arquivo ausente/inválido |
| UNKNOWN_RECORD_TYPE | FATAL | Tipo de registro desconhecido |
| DETAIL_WITHOUT_BATCH | FATAL | Detalhe encontrado fora de lote |
| INVALID_AMOUNT | FATAL | Valor inválido (missing/<=0) |
| INVALID_DATE | FATAL | Data inválida |
| INVALID_CPF | FATAL | CPF inválido |
| INVALID_CNPJ | FATAL | CNPJ inválido |
| MISSING_BARCODE | FATAL | Boleto sem barcode/ourNumber |
| INVALID_BARCODE | FATAL | Código de barras inválido (checksum) |
| MISSING_BANK_ACCOUNT | FATAL | Bank/agency/account ausente |
| MIXED_PAYMENT_TYPES_IN_BATCH | FATAL | Lote contém múltiplos tipos de pagamento |
| DUPLICATED_REQUEST_ID | WARN | requestId duplicado (ver status) |
| TRAILER_MISMATCH_COUNT | FATAL | trailer.detailCount != detalhes reais |
| TRAILER_SUM_MISMATCH | WARN | trailer.valueSum != sum(details.amount) |

---

## 11. Exemplo de relatório de validação (JSON)

```json
{
  "file": "infile.cnab",
  "status": "FAILED",
  "errors": [
    {"level":"FATAL","code":"FILE_SIZE_NOT_MULTIPLE_OF_240","message":"File size 12345 is not multiple of 240"},
    {"level":"WARN","code":"DUPLICATED_REQUEST_ID","message":"requestId 'abc' duplicated in batch 2"}
  ]
}
```

---

## 12. Pseudocódigo para validação detalhada (fluxo de execução)

```kotlin
fun validateFile(path): ValidationReport {
  val report = ValidationReport()
  if (fileSize % 240 != 0) { report.fatal("FILE_SIZE_NOT_MULTIPLE_OF_240") ; return report }
  val reader = openStream(path)
  var lineNo=0
  var currentBatch=null
  while (line = reader.read240()) {
    lineNo++
    val type = line[0]
    when(type) {
      '0' -> validateFileHeader(line, report, lineNo)
      '1' -> { currentBatch = startBatch(line); validateBatchHeader(line, report, lineNo) }
      '3' -> {
         if (currentBatch==null) report.fatal("DETAIL_WITHOUT_BATCH", lineNo)
         else {
           val detail = parseDetailMinimal(line, currentBatch)
           validateDetailCommon(detail, report, lineNo)
           when(detail.paymentType) {
             PIX -> validatePix(detail, report, lineNo)
             BOLETO -> validateBoleto(detail, report, lineNo)
             TED -> validateTed(detail, report, lineNo)
             TRIBUTE -> validateTribute(detail, report, lineNo)
             TAX_VEHICLE -> validateTaxVehicle(detail, report, lineNo)
             DDA -> validateDda(detail, report, lineNo)
           }
         }
      }
      '5' -> validateBatchTrailer(line, currentBatch, report, lineNo)
      '9' -> validateFileTrailer(line, report, lineNo)
      else -> report.fatal("UNKNOWN_RECORD_TYPE", lineNo)
    }
    if (report.hasFatal()) break
  }
  return report
}
```

---

## 13. Testes práticos e amostras
- Fornecer amostras reais (Santander) em `documentations/payments/cnab/samples/` e usar para validar parser
- Testes: unitários por detalhe; integração por arquivo; round-trip JSON->CNAB->JSON

---

## 14. Observações finais
- Sempre consulte o `BankProfile` e o PDF do banco (Santander) para offsets exatos e regras de composição (ex.: como recompor barcode a partir de campos fragmentados)
- Este documento deve ser a fonte única de regras de validação no pipeline de ingestão; mantê-lo atualizado e versionado

---

## 15. Requisitos e práticas específicas (Santander / Developer Portal)

Observação: detalhes exatos (endpoints, headers adicionais, nomes de campos) devem ser confirmados no Developer Portal do Santander. As práticas abaixo representam o padrão observado em APIs bancárias e também constam na documentação pública de muitos provedores (Santander incluso).

- Autenticação
  - OAuth2 (Client Credentials) para servidores -> `Authorization: Bearer <token>` (obrigatório em produção)
  - Em alguns serviços sensíveis pode ser exigido mTLS (mutual TLS) ou certificados aplicacionais
- Idempotência e correlação
  - `Idempotency-Key` header para POSTs que criam recursos (pagamentos/cobranças)
  - `X-Request-Id` (ou similar) para rastreabilidade em logs e webhooks
- Conteúdo e formato
  - `Content-Type: application/json` para endpoints REST
  - Tamanho de payload e limites de campos são impostos; truncar/validar antes de enviar
- Respostas assíncronas
  - Muitos endpoints retornam `202 Accepted` com `jobId` ou `processingId` => o client deve usar polling ou aguardar webhook
- Webhooks
  - Registrar URL de callback no portal; validar assinatura/headers enviados com o webhook (ver Developer Portal)
  - Implementar verificação de replay (timestamp + signature) e validar certs quando aplicável
- Rate Limits
  - Verificar limites por minuto/hora (consultar Portal). Implementar backoff exponencial ao receber 429.
- Sandbox vs Produção
  - Usar credenciais de sandbox para testes; processos de onboarding (certificação/whitelisting) são necessários para produção

Referência: Developer Portal do Santander — consultar página do produto (pix, cobranças, pagamentos) e política de security/webhooks.

---

## 16. Campos e limites recomendados (sugestões práticas)

Esses limites são recomendações para validação e truncamento; ajuste conforme o layout do banco.

| Campo | Sugestão de tamanho | Observação |
|---|---:|---|
| payer.name | 40 | nome do sacado/sacado - truncar com WARN se exceder
| payer.document | 11 ou 14 | CPF (11) ou CNPJ (14) somente dígitos
| barcode | 44 | exato 44 dígitos; campo crítico
| ourNumber | 20 | tamanho comum para "nosso número"
| bankCode | 3 | sempre 3 dígitos
| agency | 4-5 | pode variar por banco
| account | 6-12 | incluir dígito verificador quando aplicável
| amount | numeric (centavos) | inteiro; máximo policy (ex.: 1e12 centavos)
| requestId / Idempotency-Key | 36 | UUID recomendado
| extra.txid | 36 | UUID ou formato PSP

Use esses tamanhos como base de validação; não subtrair sem consultar o BankProfile e PDF do banco.

---

## 17. Tratamento de respostas HTTP e políticas de retry (integração com APIs)

- 2xx (200/201): operação concluída ou resource criado — tratar como success
- 202 Accepted: operação aceita (async) — extrair jobId e enfileirar reconcile/polling
- 400 Bad Request: payload inválido — RESPONSE do tipo FATAL para aquele detalhe; registrar e reportar (não retry automático)
- 401/403: autenticação/autorização — FATAL para integração; verificar credenciais; notificar equipe
- 404: recurso não encontrado — WARN/ERROR, decidir se é irrelevante
- 429: rate limit — aplicar backoff exponencial (retry) com jitter; logar e alertar se persistir
- 5xx: erro servidor — retry com backoff; mover para DLQ após N tentativas (ex.: 5 tentativas)

Política de retry sugerida
- Retries em falhas transitórias (429, 5xx): 3-5 tentativas com exponencial backoff (e.g., 1s, 2s, 4s, 8s)
- Dead Letter Queue: após X tentativas (ex.: 5) mover mensagem com metadados de erro e payload original
- Timeouts: configurar timeouts de socket e request (ex.: 30s)

---

## 18. Segurança, webhooks e auditoria

- Validar assinatura dos webhooks: comparar header de assinatura com HMAC do corpo usando shared secret (consultar Portal para algoritmo e header)
- Proteger endpoints de callback (IP allow-list, TLS obrigatório)
- Log: mascarar PII (CPF/CNPJ parcial) em logs de aplicação, mas manter `rawRecord` em storage seguro para auditoria
- Retenção: políticas de retenção de dados sensíveis (GDPR/LGPD) — criptografar dados em repouso quando necessário

---

## 19. Monitoramento, métricas e alertas

Sugestões de métricas a emitir
- arquivos.recebidos.total
- arquivos.validados.fatals, arquivos.validados.warns
- registros.processados.total, registros.rejeitados.total
- fila.retry.count, dlq.count
- latência por etapa (parsing, calling API, reconcile)

Alertas
- alta taxa de FATAL em validação (> X por hora)
- aumento de 5xx do PSP / 429 persistente
- erros de assinatura em webhooks

---

## 20. Algoritmo CNPJ (pseudo) — complemento ao CPF

```kotlin
fun validateCNPJ(cnpj:String): Boolean {
  val num = cnpj.filter { it.isDigit() }
  if (num.length != 14) return false
  val weights1 = intArrayOf(5,4,3,2,9,8,7,6,5,4,3,2)
  val weights2 = intArrayOf(6,5,4,3,2,9,8,7,6,5,4,3,2)
  fun calcDV(nums: String, weights: IntArray): Int {
    var sum = 0
    for (i in weights.indices) sum += (nums[i].digitToInt()) * weights[i]
    val rest = sum % 11
    return if (rest < 2) 0 else 11 - rest
  }
  val dv1 = calcDV(num.substring(0,12), weights1)
  val dv2 = calcDV(num.substring(0,12) + dv1.toString(), weights2)
  return num[12].digitToInt() == dv1 && num[13].digitToInt() == dv2
}
```

---

## 21. Catálogo expandido de códigos de erro (sugestão)

- BAD_ENCODING (FATAL): arquivo em encoding inválido
- MISSING_BATCH_HEADER (FATAL)
- INVALID_SEGMENT (FATAL)
- MISSING_REQUIRED_FIELD (FATAL) - identificar campo
- INVALID_FIELD_FORMAT (WARN/FATAL) - regex/fmt
- PAYMENT_TYPE_MISMATCH (FATAL)
- EXTERNAL_API_AUTH_ERROR (FATAL)
- EXTERNAL_API_RATE_LIMIT (WARN)
- EXTERNAL_API_SERVER_ERROR (WARN)

---

## 22. Próximos passos (implementação)

- Implementar validador (script/tool) com saída JSON (relatório de validação)
- Integrar validador no pipeline (pré-parser) e no CI
- Gerar testes automatizados usando amostras reais (sandbox Santander)

---

*Fim da versão ampliada das validações CNAB.*
