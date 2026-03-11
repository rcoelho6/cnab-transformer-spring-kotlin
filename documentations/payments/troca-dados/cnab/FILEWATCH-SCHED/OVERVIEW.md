# FILEWATCH-SCHED — Overview

Descrição
- Monitoramento de diretórios (filewatch) ou scheduler (cron) para detectar novos arquivos CNAB e processá-los.
- Suporta push de notificação: cliente informa via API/Message que o arquivo foi disponibilizado em local conhecido.

Arquivos nesta pasta
- `OVERVIEW.md` (você está aqui)
- `FILEWATCH.md` — implementação filewatch (inotify/WatchService)
- `SCHEDULER.md` — implementação scheduler (cron job / cloud scheduler)
- `PUSH-NOTIFY.md` — contrato de notificação / webhook para indicar disponibilidade

Princípio
- Cada cliente tem pasta dedicada `.../incoming/<clientUuid>/` (para filewatch), assim o arquivo não precisa conter clientUuid
- Quando o arquivo é detectado: validar, mover para processing, e emitir ACK / webhook

Vantagens
- Bom para parceiros que não podem iniciar conexões mas conseguem deixar arquivos em local acessível

Desvantagens
- Requer configuração de compartilhamento/perm e roteamento por clientUuid


