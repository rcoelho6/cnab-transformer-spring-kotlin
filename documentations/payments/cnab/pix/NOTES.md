
# PIX - Observações por banco

- Santander: verificar se o layout de cobrança traz campo livre com txid ou apenas referências internas. Alguns bancos usam o campo de "nosso número" para mapear o pagamento.
- Layout padrão (Febraban/Bacen): nem sempre contém txid; o mais comum é encontrar dados de conta/agência e identificador do favorecido.

Recomendação
- Sempre validar se o banco fornece txid em campo específico; caso não, combinar conta/agência + identificação do favorecido.


