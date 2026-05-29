# Roteiro de Vídeo — Atividade Mensageria PUC

> Duração sugerida: **8 a 12 minutos**
> Grave a tela + narração. Mostre cada evidência de forma clara antes de avançar.

---

## 0. Abertura (30s)

**[Tela: slide simples ou README no GitHub]**

> "Olá, somos [NOME DO GRUPO]. Este vídeo demonstra o sistema de mensageria desenvolvido para a atividade da PUC, usando Java, Spring Boot, RabbitMQ e PostgreSQL, com envio assíncrono de e-mails em lote."

**Integrantes:**
- Gabriel Martins Torres — 20241012001820
- [Outros integrantes, se houver]

---

## 1. Arquitetura (1–2 min)

**[Tela: README.md com diagramas Mermaid ou desenho simples]**

> "A ideia central é separar a solicitação do processamento. Quando o usuário pede o envio, a API **não envia e-mail na hora** — ela publica uma mensagem na fila do RabbitMQ. Um consumer processa depois, de forma assíncrona."

**Mencionar os componentes:**

| Componente | Classe / Nome |
|------------|---------------|
| Exchange | `email.exchange` |
| Fila | `email.send.queue` |
| Routing Key | `email.send` |
| Producer | `EmailProducer` |
| Consumer | `EmailConsumer` |
| Config RabbitMQ | `RabbitMQConfig` |

> "O front-end é HTML/JavaScript consumindo a API REST. O banco PostgreSQL guarda destinatários, mensagens e o histórico de jobs."

---

## 2. Infraestrutura rodando (1 min)

**[Tela: terminal + Docker]**

Mostrar:

```powershell
docker compose ps
```

> "Subimos PostgreSQL e RabbitMQ via Docker Compose."

**[Tela: http://localhost:15672 — guest/guest]**

> "Este é o painel de gerenciamento do RabbitMQ. Aqui vamos ver a fila recebendo e esvaziando mensagens durante o envio."

**[Tela: terminal com Spring Boot]**

```powershell
.\mvnw.cmd spring-boot:run
```

> "A aplicação Spring Boot roda na porta 8081."

---

## 3. Front-end — cadastro (1–2 min)

**[Tela: http://localhost:8081]**

### 3.1 Destinatários

> "Na aba Destinatários, cadastramos os e-mails que receberão a mensagem em lote."

- Cadastrar 2 ou 3 e-mails
- Mostrar a listagem atualizando

### 3.2 Mensagens

> "Na aba Mensagens, criamos o conteúdo: assunto e corpo do e-mail."

- Criar uma mensagem (ex.: "Aviso PUC")
- Mostrar na listagem

---

## 4. Banco de dados (30s–1 min)

**[Tela: DBeaver, pgAdmin ou `docker exec` no PostgreSQL]**

Mostrar as tabelas com dados:

```sql
SELECT * FROM recipients;
SELECT * FROM email_messages;
SELECT * FROM send_jobs;
```

> "Os destinatários e mensagens ficam persistidos no PostgreSQL. A tabela `send_jobs` registra cada solicitação de envio com status."

---

## 5. Envio assíncrono — parte principal (2–3 min)

**[Tela: RabbitMQ — aba Queues, fila `email.send.queue`]**

> "Antes do envio, a fila está vazia ou com poucas mensagens."

**[Tela: front-end — botão "Enviar para todos"]**

Clicar em **Enviar para todos**.

> "Ao clicar, a API retorna imediatamente. Ela cria um job com status PENDING e publica **uma mensagem** na fila — uma por solicitação, não uma por destinatário."

**[Tela: RabbitMQ]**

> "Veja a mensagem entrando na fila e sendo consumida em seguida."

**[Tela: log do Spring Boot no terminal]**

Apontar as linhas:

```
Publicando na exchange 'email.exchange'...
Mensagem recebida da fila 'email.send.queue'
Iniciando processamento do job X
E-mail enviado para ...
Job X finalizado: 3 enviados, 0 falhas, status=COMPLETED
```

> "O producer publica na exchange. O consumer recebe da fila, busca todos os destinatários no banco e envia em lote via MailService."

**[Tela: aba Envios no front-end]**

> "O job aparece como COMPLETED com a contagem de enviados e falhas."

---

## 6. Mailtrap (1 min)

**[Tela: inbox do Mailtrap — Email Testing]**

> "Usamos o Mailtrap em modo sandbox. Os e-mails **não vão para caixas reais** — ficam aqui para teste. Cada destinatário cadastrado gera um e-mail na inbox."

Mostrar:
- Assunto da mensagem
- Destinatário (To)
- Corpo do e-mail

> "Isso comprova que o consumer processou o lote e o MailService enviou via SMTP."

---

## 7. Principais classes do projeto (1 min)

**[Tela: IDE com a estrutura de pastas]**

Percorrer rapidamente:

```
config/
  RabbitMQConfig.java      → exchange, fila, binding
messaging/
  EmailProducer.java         → publica na fila
  EmailConsumer.java         → @RabbitListener
  SendEmailCommand.java      → payload da fila
service/
  SendService.java           → cria job + publica (202 Accepted)
  EmailProcessingService.java → envio em lote
  MailService.java           → SMTP Mailtrap
controller/
  RecipientController.java
  MessageController.java
  SendController.java
domain/
  Recipient, EmailMessage, SendJob
```

> "A separação segue camadas: Controller, Service, Repository, além da camada de mensageria com producer e consumer."

---

## 8. Encerramento (30s)

**[Tela: README ou diagrama]**

> "Concluímos um sistema de mensageria assíncrona: a requisição HTTP publica na fila RabbitMQ, o consumer processa depois, persiste status no banco e envia e-mails em lote. Obrigado!"

---

## Checklist antes de gravar

- [ ] Docker rodando (`postgres` + `rabbitmq`)
- [ ] Spring Boot na porta 8081
- [ ] `application-local.yml` com credenciais Mailtrap
- [ ] Pelo menos 2 destinatários cadastrados
- [ ] Pelo menos 1 mensagem criada
- [ ] RabbitMQ aberto em outra aba
- [ ] Mailtrap aberto em outra aba
- [ ] Terminal com logs visível
- [ ] Esperar ~15s entre testes (limite do Mailtrap free)

## Checklist de evidências (entrega)

- [ ] Link do repositório GitHub
- [ ] Link do vídeo
- [ ] Nomes dos integrantes
- [ ] Explicação da arquitetura (seção 1)
- [ ] Print/vídeo do RabbitMQ com a fila
- [ ] Print/vídeo do banco com e-mails cadastrados
- [ ] Demonstração do front-end
- [ ] Demonstração do envio / Mailtrap
- [ ] Indicação das classes principais (seção 7)

## Dicas de gravação

1. **Fale devagar** ao mostrar logs e painéis — o avaliador precisa ler.
2. **Pause 2–3 segundos** em cada tela importante (RabbitMQ, Mailtrap, job COMPLETED).
3. Se o rate limit do Mailtrap aparecer, explique: *"O plano gratuito limita e-mails por segundo; por isso implementamos delay e retry."*
4. Jobs antigos com status PENDING são de testes anteriores (race condition corrigida) — pode ignorar ou mencionar brevemente.
5. Grave em **1080p** se possível; use OBS ou gravador nativo do Windows.

---

## Roteiro curto (caso o tempo seja curto — 5 min)

1. Apresentação + arquitetura (1 min)
2. Front-end: cadastrar + enviar (1 min)
3. RabbitMQ + logs do consumer (1,5 min)
4. Mailtrap + job COMPLETED (1 min)
5. Classes principais + encerramento (30s)
