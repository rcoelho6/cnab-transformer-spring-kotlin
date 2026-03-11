
# Taxas Veiculares - Mapeamento

Proposta de modelo `VehicleFeePayment` (Kotlin data class)

VehicleFeePayment {
- tipoTaxa: enum {IPVA, LICENCIAMENTO, MULTA, OUTRO}
- renavam: String?
- placa: String?
- valor: Long
- dataPagamento: LocalDate
- codigoTributo: String?
}

Campos e posições
- Código do tributo: normalmente definido em campos de identificação do título/instrução
- Identificador do veículo: pode estar em campo observação ou campo específico (varia por banco)


