# Cane SDK — Backend

Backend multi-tenant do **Cane SDK** (Apoio Digital): a nuvem multi-agente que recebe a árvore de elementos capturada pelo SDK in-app (React Native, Android/iOS) dentro do app do parceiro, decide qual elemento destacar e devolve a instrução empática (texto + áudio) para o overlay Spotlight.

Referência de produto/arquitetura: *Cane SDK — Tese de Solução e Arquitetura de Integração (V2, set/2026)*. O SDK cliente vive no repositório irmão **frontend-sdk**.

> **Origem do código**: este repositório substitui o snapshot de julho pelo refactor SDK desenvolvido originalmente no repo `backend` (commits `9dca2fb`, `5ad747a`, `3db00c2` lá). O fluxo de app consumidor standalone (login por telefone+senha, Atalho, AppSuportado, FindBestApp, JWT/refresh tokens) foi removido por ser incompatível com o modelo multi-tenant do SDK.

## Arquitetura

Pipeline de agentes (Spring AI + Gemini, hoje `gemini-2.5-flash-lite`, temperatura 0.1, saída JSON estrita):

| Agente | Classe | Papel |
|---|---|---|
| Agente 0 — Gatekeeper | `PendingValidatorService` | Decide se o pedido do idoso é claro o bastante para agir |
| Agente 1 — QuestionWriter | `QuestionWriterService` | Redige a pergunta fechada de desambiguação |
| Agente Y — AnswerValidator | `UserAnswerValidatorService` | Valida a resposta do idoso e fecha/continua o loop |
| Agente X — ElementSelector | `ElementSelectorService` | Escolhe o `viewID` do elemento a destacar |
| Agente Z — ScreenContextDefiner | `ScreenContextDefinerService` | Escreve a instrução empática (texto e roteiro de voz) |

O documento de arquitetura fala em quatro agentes; o código tem cinco porque a validação da resposta do idoso (Agente Y) é um agente próprio.

### Endpoints SDK (autenticados por `x-api-key`)

| Método | Rota | Função |
|---|---|---|
| POST | `/resposta/validar/necessidade-informacoes` | Gatekeeper + QuestionWriter; cria o `Pedido` e devolve `{interromper, pergunta, idPedido}` |
| POST | `/resposta/validar/resposta-necessidade` | Valida a resposta do usuário no loop de desambiguação e a registra no `Pedido` |
| POST | `/resposta/achar-resposta` | ElementSelector + ScreenContextDefiner + TTS; devolve `{viewID, mensagem_escrita, mensagem_voz_url, precisao, idResposta}` |
| POST | `/usuario/registrar` | Pré-registro opcional e idempotente de um usuário final (anônimo) sob o tenant |
| POST | `/componentes/comparar` | Recebe `{idResposta, elementos}` e diz se a `assinatura` (hash SHA-256 da tela) daquela `Resposta` ainda vale |
| GET | `/resposta/listar/{idPedido}` | Histórico de respostas de um pedido |
| GET | `/audio/{filename}` | Público (nome de arquivo UUID, não adivinhável): serve o WAV sintetizado |

### Fluxo de uma ajuda

1. `necessidade-informacoes` cria o `Pedido` com o prompt e devolve o `idPedido`.
2. Enquanto `interromper = true`, o SDK mostra a `pergunta` e envia a resposta do idoso para `resposta-necessidade` com o mesmo `idPedido`. Cada pergunta e resposta é anexada ao `prompt` do pedido.
3. `achar-resposta` recebe o `idPedido` e usa o `prompt` do pedido (já com os esclarecimentos) para escolher o elemento. Sem `idPedido`, cria um pedido novo com o `prompt` do corpo, como antes.

Cada ajuda gera um único `Pedido`, com a sua `Resposta` e o `Componente` da tela.

### Modelo de dados (Flyway em `src/main/resources/db/migration`, fonte da verdade)

`Cliente` (tenant, `access_key` única) → `Personalizacao` / `Usuario` (`external_id` = hash anônimo do parceiro, único por tenant) → `Pedido` (prompt com esclarecimentos + status do loop) → `Resposta` (mensagem + raciocínio de auditoria) → `Componente` (`assinatura` = hash de `viewId|className|text` da tela).

Multi-tenancy: o filtro `ApiKeyAuthenticationFilter` resolve o `Cliente` pelo header `x-api-key`; todo `userId`/`idPedido`/`idResposta` é validado contra esse tenant antes de qualquer leitura/escrita (sem IDOR entre parceiros).

### Limite de requisições

Limite de MVP, em memória, por janela fixa:

| Escopo | Padrão | Onde |
|---|---|---|
| Por usuário (`userId` dentro de uma chave) | 20 requisições a cada 60 s | `/resposta/*` e `/usuario/registrar` |
| Por chave (`x-api-key`) | 600 requisições a cada 60 s | todas as rotas autenticadas |

Acima do limite a resposta é `429` com o header `Retry-After` (segundos). O limite por chave existe porque o `userId` é livre: sem ele, quem extraísse a chave do app poderia inventar um `userId` por chamada. O contador fica na memória da instância; com mais de uma instância o limite passa a valer por instância.

## Como rodar

Pré-requisitos: **JDK 21** (exigido pelo `pom.xml`) e MySQL 8.

```bash
cd apoiodigital
export APIKEY=<chave Gemini>
export DB_USERNAME=<usuario mysql>
export DB_PASSWORD=<senha mysql>
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd`. Variáveis de ambiente:

| Variável | Obrigatória | Padrão |
|---|---|---|
| `APIKEY` | sim | — |
| `DB_USERNAME`, `DB_PASSWORD` | sim | — |
| `DB_URL` | não | `jdbc:mysql://localhost:3306/ApoioDigitalDB` |
| `TTS_ENABLED`, `TTS_MODEL`, `TTS_VOICE` | não | `true`, `gemini-2.5-flash-preview-tts`, `Kore` |
| `AUDIO_STORAGE_DIR` | não | `audio-storage` |
| `RATE_LIMIT_USUARIO_MAXIMO`, `RATE_LIMIT_USUARIO_JANELA_SEGUNDOS` | não | `20`, `60` |
| `RATE_LIMIT_CHAVE_MAXIMO`, `RATE_LIMIT_CHAVE_JANELA_SEGUNDOS` | não | `600`, `60` |

O schema é criado/versionado pelo Flyway na subida (`ddl-auto=validate` — o Hibernate nunca gera DDL). Nenhuma credencial tem default hardcoded.

A chave do Gemini (`APIKEY`) nunca vai na URL: tanto o Spring AI quanto o `GeminiTtsService` a enviam no header `x-goog-api-key`, então ela não aparece em logs de acesso nem nas mensagens de exceção. Mantenha-a fora do git (variável de ambiente ou cofre de segredos).

## Testes

```bash
cd apoiodigital
./mvnw test
```

São 23 testes: unitários de `AcharRespostaService`, `RespostaNecessidadeService`, `ComponenteService`, limite de requisições e `GeminiTtsService`, mais o `ApoiodigitalApplicationTests`, que sobe a aplicação inteira e valida o mapeamento JPA contra o schema. Esse último precisa do MySQL no ar e das mesmas variáveis de ambiente do `spring-boot:run`.

## Contrato com o frontend-sdk

1. **`elementos`**: desserializado em `CapturedElementDTO`, espelho exato do `CapturedElement` do SDK (`viewId: string`, `className`, `text`, `isSecure`, `isInteractive`, `x/y/width/height` em dp/pt). O ElementSelector escolhe e devolve o `viewId`, ecoado como `viewID` na resposta de `/achar-resposta`; o SDK o casa com o próprio índice para posicionar o Spotlight. A `assinatura` do Componente é hash de `viewId|className|text` (coordenadas ficam de fora).
2. **Identidade do usuário**: `userId` é o hash anônimo emitido pelo **parceiro**, armazenado em `usuario.external_id` e **auto-provisionado na primeira chamada** de qualquer endpoint `/resposta/*`.
3. **Loop de esclarecimento**: `pergunta.opcoes = []` indica resposta livre (o `QuestionSheet` do SDK mostra um campo de texto). O `idPedido` devolvido deve ser enviado em `resposta-necessidade` e em `achar-resposta`.
4. **Campos nulos**: quando não há pergunta, `pergunta` vem `null`; quando a síntese de voz falha, `mensagem_voz_url` vem `null` e o restante da resposta segue válido.
5. **`idResposta`**: devolvido por `achar-resposta`, é o parâmetro de `/componentes/comparar`.
6. **Erros**: `401` para `x-api-key` ausente ou inválida, `404` para pedido/resposta de outro usuário ou tenant, `429` com `Retry-After` para limite excedido.

## Dívidas conhecidas

1. **Latência**: o `achar-resposta` levou de 8 s a 69 s no teste local (a síntese de voz é síncrona e sem timeout), bem acima do limite de 10 s do SDK e da meta do documento de arquitetura.
2. **Cache de assinatura não plugado**: `/componentes/comparar` já pode ser chamado (o `idResposta` é exposto), mas o `achar-resposta` sempre roda os agentes. Aproveitar o cache exige consultar a assinatura *antes* dos agentes e reusá-la entre usuários da mesma tela.
3. **Personalizacao inerte**: `regra_personalizada` é persistida mas nenhum agente a injeta no prompt.
4. **Sem cascata de modelos**: um único modelo para os 5 agentes; sem log de tokens reais por agente.
5. **Sem poda da árvore de elementos** antes dos prompts (efeito multiplicativo no custo das chamadas).
6. **Limite de requisições em memória**: precisa de armazenamento compartilhado (por exemplo Redis) quando houver mais de uma instância.

Outros: `access_key` em texto plano no banco (avaliar hash); arquivos de áudio em `AUDIO_STORAGE_DIR` nunca são apagados.

## Estrutura

```
apoiodigital/
  src/main/java/br/com/tucunare/apoiodigital/
    agent/            plumbing de LLM (provider Gemini, RuleBuilder, DTO de elemento)
    cliente/          tenant (Cliente, Personalizacao)
    componente/       assinatura de tela + comparação
    limite/           limite de requisições por usuário e por chave
    pedido/           intenção do usuário + estado do loop de desambiguação
    resposta/         os 3 endpoints SDK e serviços orquestradores
    security/         filtro x-api-key, TenantContext, SecurityConfig
    tts/              síntese Gemini TTS + serving de áudio
    tutorial/agents/  os 5 agentes (rules em src/main/resources/rules/tutorial)
    usuario/          usuário final anônimo por tenant
  src/main/resources/db/migration/  schema Flyway (fonte da verdade)
```
