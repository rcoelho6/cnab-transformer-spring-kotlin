# ESTRUTURA JSON (RESUMO VISUAL)

Objetivo: mostrar a estrutura em tabelas, exemplos coloridos e regras visuais rápidas.

⚠️ Convenção: todos os arquivos nesta pasta são MAIÚSCULOS e nomes base <= 25 chars.

## Estrutura top-level

| Campo | Tipo | Obrigatório | Descrição |
|---|---:|:---:|---|
| file | object | Sim | Raiz do documento CNAB representado em JSON |
| file.header | object | Sim | bankCode, fileDate, layoutVersion |
| file.batches | array | Sim | Lista de lotes (1 lote = 1 paymentType) |
| file.trailer | object | Sim | batchCount, recordCount |


<div style="background:#f0f8ff;padding:8px;border-radius:6px;margin:8px 0">
  <b style="color:#1f618d">Regra chave:</b> cada <code>batch</code> deve conter apenas um <code>paymentType</code>.
</div>

## Exemplo de um `batch` (visual)

| Campo | Exemplo | Obs |
|---|---|---|
| header.batchNumber | 1 | número do lote |
| paymentType | "PIX" | obrigatório (PIX/BOLETO/TED/...) |
| details[] | array | cada detalhe tem campos mínimos (ver abaixo) |
| trailer.detailCount | 1 | contado pelo generator (recalcular) |

## Campos mínimos por `detail` (tabela)

| Campo | Tipo | Obrigatório | Nota |
|---|---:|:---:|---|
| recordSeq | int | Sim | Sequência no lote |
| segment | string | Sim | Ex: 'P','T' — guia o mapping |
| paymentType | string | Sim | PIX/BOLETO/TED/TRIBUTE/TAX_VEHICLE/DDA |
| amount | int | Sim | em centavos |
| date | string | Sim | ISO YYYY-MM-DD (dueDate/valueDate) |
| payer.name | string | Sim | Nome do pagador |
| payer.document | string | Sim | CPF/CNPJ |
| extra | object | Depende | Campos mínimos por tipo (abaixo) |
| requestId | string | Recomendado | idempotência/rastreio |


### `extra` por tipo (resumo rápido)
- PIX: `pixKey` ou `txid`
- BOLETO: `barcode` (44) ou `ourNumber` + `documentNumber`
- TED: `bankCode, agency, account, holderName, holderDocument`
- TRIBUTE: `taxCode, reference`
- TAX_VEHICLE: `vehicleId, taxCode`
- DDA: `nossoNumero, titleNumber`

---

## Cores (convenção visual nesta doc)
- <span style="color:green">Verde</span>: campos válidos / passos OK
- <span style="color:orange">Laranja</span>: atenção / validações necessárias
- <span style="color:red">Vermelho</span>: erro crítico / invalida geração do CNAB

Exemplo rápido (JSON mínimo colorido - inline):

```json
{
  "recordSeq": 1,
  "segment": "P",
  "paymentType": "BOLETO",
  "amount": 12345,
  "date": "2026-03-20",
  "payer": { "name": "Fulano", "document": "12345678901" },
  "extra": { "barcode": "2129000119..." }
}
```

---

Links rápidos:
- ver `JSON-SCHEMA.md` para explicações completas e pseudocódigo
- ver `EXAMPLE-CNAB.JSON` para um exemplo com 1 lote por tipo


