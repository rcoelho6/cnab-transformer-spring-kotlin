# SCHEDULER — Agendador (Proposta técnico)

Implementação
- Cron-based: job periódico que lista `incoming/<clientUuid>/` e processa arquivos finalizados
- Cloud scheduler: AWS EventBridge / GCP Cloud Scheduler + Lambda/Function para listar e processar

Fluxo
- Job roda a cada N minutos; lista arquivos, valida e enfileira para processamento; move arquivos incompletos ou locks

Considerações
- Menor imediaticidade que filewatch; bom para ambientes onde inotify não é possível
- Deve lidar com grandes volumes: paginar listagens


