
# Boleto - Mapeamento CNAB -> Modelo

Proposta de modelo `BoletoPayment` (Kotlin data class)

BoletoPayment {
- nossoNumero: String?
- numeroDocumento: String?
- codigoBarras: String? (44 dígitos)
- valor: Long (centavos)
- dataVencimento: LocalDate
- sacadoNome: String?
- sacadoCpfCnpj: String?
- instrucoes: String?
}

Posições comuns (exemplo genérico)
- segmento P: campos do título (valor, vencimento, identificadores)
- segmento Q: informações do sacado
- campo livre / complemento: quando banco separa o código de barras em componentes

Validações
- Verificar se o código de barras tem 44 dígitos válidos (módulo 10/11 conforme layout)
- Normalizar nosso número removendo espaços/prefixos


