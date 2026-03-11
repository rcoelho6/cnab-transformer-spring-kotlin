 # PIX - Mapeamento CNAB -> Modelo

 Este arquivo descreve uma proposta prática de mapeamento entre posições comuns do CNAB240 e um objeto `PixPayment` usado pelo parser.

 Proposta de modelo (Kotlin data class)

 PixPayment {
 - txid: String?
 - chave: String? (cpf/cnpj/telefone/email/aleatoria)
 - tipoChave: enum {CPF, CNPJ, TELEFONE, EMAIL, ALEATORIA}
 - valor: Long (centavos)
 - dataPagamento: LocalDate
 - favorecido: {
  - agencia: String?
  - conta: String?
  - nome: String?
}
- observacoes: String?
}

Posições sugeridas (exemplo genérico)
- segmento: identificar qual segmento contém dados financeiros (ex.: segmento 'T' ou 'U' dependendo do layout)
- campo livre (ex.: positions 80-119) pode conter txid ou chave — o BankProfile deve definir isso
- valor: posições padrão de valor (ex.: 77-91 dependendo do segmento)
- data: posições padrão de data

Regras de validação
- Se `txid` presente, preferir txid como identificador; se não, usar `chave` + conta/agencia
- Validar tipo de chave por regex e normalizar (ex.: remover formatação de CPF/CNPJ)

Exemplos e notas
- Fornecer exemplos concretos por banco em `examples.md`.


