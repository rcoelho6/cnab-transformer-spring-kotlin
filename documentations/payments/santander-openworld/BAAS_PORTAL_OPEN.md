# BAAS / PORTAL / OPEN — RESUMO

- BaaS: serviços bancários expostos via APIs (conta, pagamentos, cobrança, conciliação).
- Developer Portal: documentação, sandbox, onboarding, credenciais, certificação.
- Open Finance: ecossistema de compartilhamento de dados e iniciação de pagamentos com consentimento.

Mermaid — visão resumida

```mermaid
flowchart LR
  CLI[CLIENT APP] -->|onboard| DEVPORTAL[DEV PORTAL]
  DEVPORTAL -->|provision| BAAS[BaaS PLATFORM]
  CLI -->|enqueue| MQ[QUEUE]
  WORKER[ASYNC WORKER] -->|consume| MQ
  WORKER -->|call| SANTANDER[SANTANDER APIs]
  SANTANDER -->|webhook| CLI
  SANTANDER -->|open finance| OPEN[OPEN FINANCE]
```

Pontos chave

- Autenticação: OAuth2 (Client Credentials), mTLS e API keys em produção.
- Sandboxes: testar fluxos com webhooks simulados.
- Assincronismo: enfileirar pedidos, workers, reconciliadores e webhooks para status finais.


