
# PIX - Overview e Considerações para Parser CNAB240

Neste conjunto de páginas documentamos como o CNAB240 pode representar pagamentos relacionados a PIX (quando suportado pelo layout do banco) e como mapear os campos relevantes para os diferentes tipos de PIX: Cob (charge), Pix Agendado, Pix Instantâneo (quando via arquivo), Pix por QRCode/Identificador, e reversões.

Arquivos nesta pasta
- `overview.md` (você está aqui)
- `mapping.md` — posições comuns e como mapear campos do CNAB -> objeto PIX
- `examples.md` — exemplos de registros (exemplos fictícios) e testes para validação
- `notes.md` — observações específicas por banco

Principais campos de interesse
- Identificador do PIX (txid) — quando presente em campo de livre utilizado pelo banco
- Código do tipo de pagamento/segmento — identifica se é pagamento, estorno, agendamento
- Valor, data de pagamento, dados do favorecido (agência/conta ou chave PIX)
- Campo de instruções / observações — pode conter info sobre efetiva forma (boleto vs pix)

Estratégia de parsing
1. Mapear o segmento que representa a transação (por exemplo, segmento "Y" ou outro conforme o layout do banco).
2. Extrair campos posicionalmente mapeados para: txid, valor, data, conta/identificação do favorecido.
3. Normalizar a chave PIX: pode ser CPF/CNPJ, telefone, e-mail, EVP (endereço de pagamento virtual) ou chave aleatória (txid ou EVP). Validar formato.
4. Validar regras: valor > 0, data válida, chave PIX formatada.

Observação
- Nem todos os bancos colocam dados PIX em CNAB240 — alguns fornecem arquivos específicos (ou via APIs). Verifique o layout do banco.


