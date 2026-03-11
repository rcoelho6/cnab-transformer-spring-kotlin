# DIRECT-FILE — Overview

Descrição
- Transferência direta de arquivos CNAB sem uso de REST (alternativas técnicas propostas):
  1. SFTP (recomendado) — cliente faz PUT no diretório seguro do servidor SFTP
  2. SMB/CIFS (rede compartilhada) — montar pasta de entrega por cliente
  3. Cloud Storage (S3/GCS) — cliente faz upload em bucket com prefixo por clientUuid
  4. FTP-SSL (FTPS) — legacy, menos recomendado

Cada método exige autenticação e controles de permissão por `clientUuid`. Oferecer um ACK (arquivo ou API) para confirmar recebimento.

Arquivos nesta pasta
- `OVERVIEW.md`
- `SFTP.md`
- `S3-GCS.md`
- `SMB.md`

Vantagens e desvantagens são discutidas em cada subdoc.

Retorno (arquivos .RET) e ACK
- Para métodos de `DIRECT-FILE` o servidor deverá, após processamento, gerar um arquivo de retorno com extensão `.RET` usando a mesma base do nome do arquivo original (ex.: `cnab_20260310_001.RET`) e gravá-lo em um local acordado (ex.: prefixo `outgoing/` ou `RETORNO/`).
- Além do `.RET`, gerar um arquivo ACK em JSON contendo `processingId`, `status` e `validationReport` quando aplicável.

