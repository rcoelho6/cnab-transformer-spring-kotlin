
# Tributos - Mapeamento CNAB -> Modelo

Proposta de modelo `TaxPayment` (Kotlin data class)

TaxPayment {
- codigoTributo: String
- tipo: enum {DARF, GPS, GNRE, OUTRO}
- documentoContribuinte: String (CPF/CNPJ)
- referencia: String? (competência/período)
- valor: Long
- dataPagamento: LocalDate
}

Dados e validações
- Validar código do tributo contra catálogo quando possível
- Normalizar documento do contribuinte


