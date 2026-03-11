 # PIX - Exemplos de registros (fictícios)

 Exemplo 1 — transação PIX com txid no campo livre (exemplo fictício)

 Registro (240 bytes representado como colunas):
 - segmento: P (detalhe)
 - posições 80-119: txid: 123e4567-e89b-12d3-a456-426614174000
 - posições (valor): 153-165: 0000000012345 (R$ 123,45)

 Exemplo 2 — transação PIX por chave CPF
 - campo livre: 80-99: 12345678901 (CPF sem formatação)

 Testes recomendados
 - validar parsing de txid com UUID
 - validar parsing de chaves por regex (CPF/CNPJ/telefone/email)
 - validar valores e datas


