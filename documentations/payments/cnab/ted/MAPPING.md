
# TED - Mapeamento CNAB -> Modelo

Proposta de modelo `TransferPayment` (Kotlin data class)

TransferPayment {
- bancoFavorecido: String (código do banco)
- agencia: String
- conta: String
- tipoConta: String?
- titular: String?
- valor: Long (centavos)
- dataPagamento: LocalDate
- identificadorPagamento: String? (documento/nosso numero)
}

Posições comuns
- campos de banco/agencia/conta normalmente possuem posições fixas em segmentos de detalhe (~agência: 21-25, conta: 26-36, etc. ver layout)

Validações
- Código do banco válido (3 dígitos)
- Valor > 0


