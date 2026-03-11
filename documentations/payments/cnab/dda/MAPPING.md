
# DDA - Mapeamento CNAB -> Modelo

Proposta de modelo `DdaRecord` (Kotlin data class)

DdaRecord {
- numeroTitulo: String
- nossoNumero: String
- sacadoNome: String
- valor: Long
- dataVencimento: LocalDate
- situacao: enum {APRESENTADO, ACEITO, RECUSADO}
}

Campos de interesse
- Segmentos P/Q para cobrança (nosso número, número do título)
- Indicações de aceite/recusa em campos de instrução


