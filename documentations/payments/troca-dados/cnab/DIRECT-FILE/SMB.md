# DIRECT-FILE — SMB/CIFS (Proposta)

Fluxo
- Cliente tem credenciais para montar share SMB em path dedicado: `\\server\incoming\<clientUuid>\`
- Operator process monitors share and moves files to processing folder after integrity checks

Segurança
- Recomendações consolidadas em `../../segurança/CNAB-EXCHANGE.md` (Kerberos/NTLM, ACLs por share). SMB é recomendado apenas para redes internas seguras.

Considerações
- Locks e concorrência em rede; possível latência de atualização; bom para parceiros com infraestrutura local


