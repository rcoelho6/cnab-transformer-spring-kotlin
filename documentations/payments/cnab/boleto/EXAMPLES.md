
# Boleto - Exemplos (fictícios)

Exemplo 1 - Segmento P (título)
- posição 77-91: valor do título (00000000012345 => R$ 123,45)
- posição 147-156: data de vencimento (YYYYMMDD)
- posições 62-81: nosso número
- posições 120-160: campo livre (pode conter código de barras parcial)

Exemplo 2 - Segmento Q (sacado)
- posições 30-79: nome do sacado
- posições 80-93: CPF/CNPJ do sacado

Testes recomendados
- validação de código de barras (44 dígitos)
- validação de datas e valores


