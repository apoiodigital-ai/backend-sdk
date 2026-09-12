# Cane SDK — Backend

Backend multi-tenant do **Cane SDK** (Apoio Digital): a nuvem multi-agente que recebe a árvore de elementos capturada pelo SDK in-app (React Native, Android/iOS) dentro do app do parceiro, decide qual elemento destacar e devolve a instrução empática (texto + áudio) para o overlay Spotlight.

Referência de produto/arquitetura: *Cane SDK — Tese de Solução e Arquitetura de Integração (V2, set/2026)*. O SDK cliente vive no repositório irmão **frontend-sdk**.

> **Origem do código**: este repositório substitui o snapshot de julho pelo refactor SDK desenvolvido originalmente no repo `backend` (commits `9dca2fb`, `5ad747a`, `3db00c2` lá). O fluxo de app consumidor standalone (login por telefone+senha, Atalho, AppSuportado, FindBestApp, JWT/refresh tokens) foi removido por ser incompatível com o modelo multi-tenant do SDK.

## Arquitetura

Pipeline de agentes (Spring AI + Gemini, hoje `gemini-2.5-flash-lite`, temperatura 0.1, saída JSON estrita):

| Agente (doc V2) | Classe | Papel |
|---|---|---|
| Agente 0 — Gatekeeper | `PendingValidatorService` | Decide se o pedido do idoso é claro o bastante para agir |
| Agente 1 — QuestionWriter | `QuestionWriterService` | Redige a pergunta fechada de desambiguação |
| Agente Y — AnswerValidator | `UserAnswerValidatorService` | Valida a resposta do idoso e fecha/continua o loop |
| Agente X — ElementSelector | `ElementSelectorService` | Escolhe o `viewID` do elemento a destacar |
| Agente Z — ScreenContextDefiner | `ScreenContextDefinerService` | Escreve a instrução empática (texto e roteiro de voz) |

### Endpoints SDK (autenticados por `x-api-key`)

| Método | Rota | Função |
|---|---|---|
| POST | `/resposta/validar/necessidade-informacoes` | Gatekeeper + QuestionWriter; cria o `Pedido` e devolve `{interromper, pergunta?, idPedido}` |
| POST | `/resposta/validar/resposta-necessidade` | Valida a resposta do usuário no loop de desambiguação |
| POST | `/resposta/achar-resposta` | ElementSelector + ScreenContextDefiner + TTS; devolve `{viewID, mensagem_escrita, mensagem_voz_url, precisao}` |
| POST | `/usuario/registrar` | Registra um usuário final (anônimo) sob o tenant autenticado |
| POST | `/componentes/comparar` | Checa se a `assinatura` (hash SHA-256 da tela) de uma `Resposta` ainda vale |
| GET | `/resposta/listar/{idPedido}` | Histórico de respostas de um pedido |
| GET | `/audio/{filename}` | Público (filename UUID não adivinhável): serve o WAV sintetizado |

### Modelo de dados (Flyway `V1__init_schema.sql`, conforme doc V2 §4.4)

`Cliente` (tenant, `access_key` única) → `Personalizacao` / `Usuario` → `Pedido` (prompt + status do loop) → `Resposta` (mensagem + raciocínio de auditoria) → `Componente` (`assinatura` = hash da hierarquia de elementos, chave de cache).

Multi-tenancy: o filtro `ApiKeyAuthenticationFilter` resolve o `Cliente` pelo header `x-api-key`; todo `userId`/`idPedido`/`idResposta` é validado contra esse tenant antes de qualquer leitura/escrita (sem IDOR entre parceiros).

## Como rodar

Pré-requisitos: JDK 17+, MySQL 8.

```bash
cd apoiodigital
export APIKEY=<chave Gemini>
export DB_USERNAME=<usuario mysql>
export DB_PASSWORD=<senha mysql>
# opcionais: DB_URL, TTS_ENABLED, TTS_MODEL, TTS_VOICE, AUDIO_STORAGE_DIR
./mvnw spring-boot:run
```

O schema é criado/versionado pelo Flyway na subida (`ddl-auto=validate` — o Hibernate nunca gera DDL). Nenhuma credencial tem default hardcoded.

## Contrato com o frontend-sdk (alinhado em 12/09/2026)

Os três bloqueadores de integração da verificação crítica foram **corrigidos**:

1. **`elementos`**: desserializado em `CapturedElementDTO`, espelho exato do `CapturedElement` do SDK (`viewId: string`, `className`, `text`, `isSecure`, `isInteractive`, `x/y/width/height`). O ElementSelector escolhe e devolve o `viewId` string, ecoado de volta como `viewID` na resposta de `/achar-resposta` — o SDK o casa com o próprio índice para posicionar o Spotlight. A `assinatura` do Componente é hash de `viewId|className|text` (coordenadas ficam de fora, para a mesma tela ter o mesmo hash em qualquer aparelho).
2. **Identidade do usuário**: `userId` é o hash anônimo emitido pelo **parceiro** (string, doc V2 §2.4), armazenado em `usuario.external_id` (único por tenant — migração `V2__usuario_external_id.sql`) e **auto-provisionado na primeira chamada** de qualquer endpoint `/resposta/*`. `POST /usuario/registrar` virou pré-registro opcional e idempotente.
3. **Loop de follow-up**: continua devolvendo `pergunta.opcoes = []` quando a resposta é livre — o `QuestionSheet` do SDK agora renderiza campo de texto nesse caso (corrigido no frontend-sdk).

> **Atenção**: as correções do backend ainda **não foram compiladas/testadas** — não há JDK 17 nesta máquina de desenvolvimento. Rode `./mvnw test` (JDK 17+) antes de fazer deploy.

## Dívidas conhecidas (plano de custos, doc §7.4)

1. **Cache de assinatura não plugado**: `Componente.assinatura` e `/componentes/comparar` existem, mas `achar-resposta` sempre roda os dois agentes e o SDK nunca consulta o cache. A promessa de >70% de hit (tela repetida → 0 chamadas de IA) exige: lookup por assinatura *antes* dos agentes + reuso cross-usuário por tela (hoje a assinatura fica presa à `Resposta`).
2. **Personalizacao inerte**: `regra_personalizada` é persistida mas nenhum agente a injeta no prompt.
3. **Sem cascata de modelos**: um único modelo para os 5 agentes (mitigado por já ser um modelo barato); sem log de tokens reais por agente.
4. **Sem poda da árvore de elementos** antes dos prompts (efeito multiplicativo no custo das chamadas).

Outros: `access_key` em texto plano no banco (avaliar hash); testes cobrem apenas `ComponenteService`.

## Estrutura

```
apoiodigital/
  src/main/java/br/com/tucunare/apoiodigital/
    agent/          # plumbing de LLM (provider Gemini, RuleBuilder, DTO de elemento)
    cliente/        # tenant (Cliente, Personalizacao)
    componente/     # assinatura de tela + comparação
    pedido/         # intenção do usuário + estado do loop de desambiguação
    resposta/       # os 3 endpoints SDK e serviços orquestradores
    security/       # filtro x-api-key, TenantContext, SecurityConfig
    tts/            # síntese Gemini TTS + serving de áudio
    tutorial/agents/ # os 5 agentes (rules em src/main/resources/rules/tutorial)
    usuario/        # usuário final anônimo por tenant
  src/main/resources/db/migration/  # schema Flyway (fonte da verdade)
```
