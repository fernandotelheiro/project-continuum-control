# Continuum Control

O Continuum Control é uma API REST para gerenciamento de operações temporais, envolvendo agentes, eventos históricos, anomalias, missões, autorizações e intervenções.

A aplicação foi desenvolvida como um monólito em camadas com três formas de persistência, cada uma com uma responsabilidade específica: PostgreSQL para o estado operacional e transações, MongoDB para o histórico temporal e Redis para cache de dados derivados. A API possui autenticação HTTP Basic, autorização baseada em perfis, regras de negócio no domínio, erros padronizados com `ProblemDetail` e testes de integração utilizando Testcontainers.

**Organização das camadas:**

```text
api → service → domain → repository / historico
```

- `api`: endpoints HTTP, DTOs e tratamento da entrada e saída;
- `service`: transações e orquestração dos casos de uso;
- `domain`: entidades e regras de negócio;
- `repository`: persistência operacional no PostgreSQL;
- `historico`: persistência do histórico temporal no MongoDB.

---

## Pré-requisitos

Para executar o projeto são necessários:

- JDK 21;
- Docker Desktop;
- Git.

O projeto inclui Maven Wrapper, portanto não é necessário instalar Maven separadamente.

Para conferir o Java:

```powershell
java -version
```

Para conferir o Docker:

```powershell
docker version
```

---

## Configuração

O repositório contém um arquivo `.env.example` com valores adequados para um ambiente local de demonstração.

Crie o seu `.env` a partir dele.

### PowerShell

```powershell
Copy-Item .env.example .env
```

### Linux/macOS

```bash
cp .env.example .env
```

O arquivo `.env` não é versionado.

### Variáveis principais

| Variável | Descrição |
|---|---|
| `POSTGRES_DB` | Nome do banco PostgreSQL criado pelo container |
| `POSTGRES_USER` | Usuário do PostgreSQL |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL |
| `DB_URL` | URL JDBC usada pela aplicação fora do Compose |
| `DB_USER` | Usuário usado pela aplicação para acessar o PostgreSQL |
| `DB_PASSWORD` | Senha usada pela aplicação para acessar o PostgreSQL |
| `CONTINUUM_SENHA_INICIAL` | Senha inicial dos usuários HTTP criados no perfil `dev` |

No arquivo `.env.example`, a senha do PostgreSQL e a senha utilizada pela aplicação para acessar o banco possuem o mesmo valor.

As credenciais do banco e as credenciais HTTP são independentes.

Por exemplo:

```text
POSTGRES_PASSWORD=senha-postgres-exemplo
DB_PASSWORD=senha-postgres-exemplo

CONTINUUM_SENHA_INICIAL=senha-usuarios-exemplo
```

---

## Como executar todo o ambiente

A forma recomendada de executar o projeto é pelo Docker Compose.

Na raiz do projeto:

```powershell
docker compose up --build -d
```

O comando inicia:

- aplicação Spring Boot;
- PostgreSQL 16;
- MongoDB 7;
- Redis 7.

O container da aplicação utiliza o perfil Spring `dev`.

Nesse perfil também são criados os dados iniciais para demonstração.

Para verificar os containers:

```powershell
docker compose ps
```

A aplicação fica disponível em:

```text
http://localhost:8080
```

---

## Verificar a saúde da aplicação

Execute:

```powershell
curl.exe -s http://localhost:8080/actuator/health
```

Uma execução saudável apresenta o status geral:

```json
"status": "UP"
```

e também os componentes:

```text
db     UP
mongo  UP
redis  UP
```

O endpoint `/actuator/health` é público para permitir a utilização por healthchecks.

---

## Parar o ambiente

Para parar os containers preservando os volumes:

```powershell
docker compose down
```

Para apagar também os volumes e começar com bancos vazios:

```powershell
docker compose down -v
```

O segundo comando remove os dados do PostgreSQL e MongoDB associados ao Compose.

---

## Executar fora do Docker

Durante o desenvolvimento, também é possível executar somente PostgreSQL, MongoDB e Redis pelo Docker e iniciar a aplicação diretamente pela JVM ou pela IDE.

Primeiro, suba somente as persistências:

```powershell
docker compose up -d postgres mongo redis
```

O arquivo `.env` é utilizado pelo Docker Compose, mas **não é lido automaticamente pelo Spring Boot**.

Portanto, ao executar a aplicação fora do Compose, valores diferentes dos padrões precisam estar disponíveis como variáveis de ambiente do processo.

Configure `DB_PASSWORD` com o mesmo valor utilizado em `POSTGRES_PASSWORD` no seu `.env`:

```powershell
$env:DB_PASSWORD = "<o mesmo valor de POSTGRES_PASSWORD do seu .env>"
```

Depois:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Utilizando diretamente os valores fornecidos pelo `.env.example`, por exemplo:

```powershell
$env:DB_PASSWORD = "senha-postgres-exemplo"

.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Caso também sejam modificados o nome do banco, o usuário ou o endereço do PostgreSQL, configure as demais variáveis necessárias:

```powershell
$env:DB_USER = "continuum"
$env:DB_URL = "jdbc:postgresql://localhost:5432/continuum"
```

O mesmo princípio vale para outras configurações externalizadas.

---

## Construção do projeto

No Windows:

```powershell
.\mvnw.cmd clean package
```

Em Linux/macOS:

```bash
./mvnw clean package
```

O projeto gera o JAR:

```text
target/continuum-control.jar
```

Ele pode ser executado diretamente:

```powershell
java -jar target/continuum-control.jar
```

Nesse modo, PostgreSQL, MongoDB e Redis precisam estar disponíveis e as configurações necessárias devem estar presentes no ambiente do processo.

---

## Seed de desenvolvimento

O perfil `dev` possui um `ApplicationRunner` responsável por criar dados iniciais.

O seed só é executado quando ainda não existem agentes no banco, evitando duplicação após reinicializações.

Ele cria:

- três agentes;
- agentes em estados diferentes;
- um evento histórico;
- uma anomalia de baixo risco;
- uma anomalia crítica;
- uma anomalia irreversível;
- uma missão planejada;
- um agente designado para a missão;
- usuários para testar os quatro perfis de segurança.

A missão criada para demonstração utiliza o código:

```text
MIS-DEV-001
```

Entre os agentes criados estão:

```text
Agente Um
Agente Dois
Agente Três
```

---

## Como executar os testes

No Windows:

```powershell
.\mvnw.cmd test
```

Em Linux/macOS:

```bash
./mvnw test
```

É necessário apenas que o Docker esteja em execução.

Os testes utilizam Testcontainers para iniciar instâncias reais e descartáveis de:

- PostgreSQL;
- MongoDB;
- Redis.

Não é utilizado H2.

A escolha de bancos reais nos testes evita que diferenças de dialeto ou de comportamento façam um teste passar em uma tecnologia diferente daquela utilizada pela aplicação.

A suíte possui atualmente **21 testes** e cobre:

- regras de domínio;
- transições de estado;
- atomicidade da inicialização da missão;
- rollback transacional;
- `401` sem credencial;
- `403` por perfil;
- `403` por autorização de recurso;
- `400` para payload inválido;
- `404` para recurso inexistente.

O teste de atomicidade utiliza PostgreSQL real e não depende do rollback automático do próprio teste para provar o comportamento.

---

## Autenticação

A API utiliza HTTP Basic.

Com exceção do healthcheck, as consultas exigem autenticação.

No perfil `dev`, são criados os seguintes usuários:

| Usuário | Perfil |
|---|---|
| `operador` | `OPERADOR` |
| `supervisor` | `SUPERVISOR` |
| `admin` | `ADMINISTRADOR` |
| `agente1` | `AGENTE` |

Todos utilizam a senha definida pela variável:

```text
CONTINUUM_SENHA_INICIAL
```

Portanto, **a senha usada nos comandos deve ser o valor de `CONTINUUM_SENHA_INICIAL` existente no seu `.env`**.

Se o arquivo tiver sido criado diretamente a partir do `.env.example`, o teste pode ser feito com:

```powershell
curl.exe -s -u supervisor:senha-usuarios-exemplo http://localhost:8080/missoes
```

---

## Perfis e permissões

### OPERADOR

Pode consultar os recursos autenticados e criar ou alterar:

- eventos;
- anomalias;
- missões.

Não pode executar as ações administrativas da missão, como iniciar ou concluir.

### AGENTE

Pode consultar recursos autenticados e registrar intervenções.

Para um usuário com perfil `AGENTE`, existe também autorização por recurso: ele precisa estar associado a um agente participante da missão.

### SUPERVISOR

Pode consultar recursos e executar as operações de missão, incluindo:

- criar eventos;
- criar anomalias;
- criar missões;
- designar agentes;
- autorizar missão;
- iniciar missão;
- registrar intervenção;
- concluir missão;
- registrar falha.

### ADMINISTRADOR

É responsável pelo cadastro de agentes.

Pode:

- criar agentes;
- alterar agentes;
- excluir agentes quando a integridade referencial permitir.

---

## Endpoints principais

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| GET | `/agentes` | autenticado | Lista agentes |
| GET | `/agentes/{id}` | autenticado | Consulta agente por ID |
| POST | `/agentes` | ADMINISTRADOR | Cria agente |
| PUT | `/agentes/{id}` | ADMINISTRADOR | Atualiza cadastro do agente |
| DELETE | `/agentes/{id}` | ADMINISTRADOR | Exclui agente; retorna `409` se possuir histórico relacionado |
| GET | `/eventos` | autenticado | Lista eventos históricos |
| GET | `/eventos/{id}` | autenticado | Consulta evento |
| POST | `/eventos` | OPERADOR ou SUPERVISOR | Cria evento |
| PUT | `/eventos/{id}` | OPERADOR ou SUPERVISOR | Atualiza evento |
| GET | `/anomalias` | autenticado | Lista anomalias |
| GET | `/anomalias/{id}` | autenticado | Consulta anomalia |
| POST | `/anomalias` | OPERADOR ou SUPERVISOR | Cria anomalia |
| PUT | `/anomalias/{id}` | OPERADOR ou SUPERVISOR | Atualiza/reavalia anomalia |
| GET | `/missoes` | autenticado | Lista missões |
| GET | `/missoes/{id}` | autenticado | Consulta missão |
| POST | `/missoes` | OPERADOR ou SUPERVISOR | Cria missão |
| PUT | `/missoes/{id}` | OPERADOR ou SUPERVISOR | Atualiza dados da missão |
| POST | `/missoes/{id}/agentes` | SUPERVISOR | Designa agente |
| POST | `/missoes/{id}/autorizacao` | SUPERVISOR | Autoriza missão |
| POST | `/missoes/{id}/iniciar` | SUPERVISOR | Inicia missão |
| POST | `/missoes/{id}/intervencoes` | AGENTE ou SUPERVISOR | Registra intervenção |
| POST | `/missoes/{id}/concluir` | SUPERVISOR | Conclui missão |
| POST | `/missoes/{id}/falhar` | SUPERVISOR | Registra falha |
| GET | `/missoes/{id}/historico` | autenticado | Consulta histórico temporal da missão |
| GET | `/estado-operacional` | autenticado | Consulta painel operacional |
| GET | `/estabilidade` | autenticado | Consulta índice de estabilidade |
| GET | `/actuator/health` | público | Healthcheck da aplicação |

A pasta `http/` contém exemplos de requisições e corpos JSON utilizados para testes manuais da API.

---

# Estratégia de persistência

O Continuum Control utiliza três tecnologias de persistência porque os dados possuem características diferentes.

## PostgreSQL

O PostgreSQL é a fonte de verdade do **estado operacional atual**.

Nele são persistidos dados como:

- agentes;
- eventos históricos;
- anomalias;
- missões;
- participações;
- autorizações;
- intervenções;
- usuários.

Esses dados possuem relacionamentos fortes e exigem integridade.

O schema utiliza recursos como:

- `FOREIGN KEY`;
- `CHECK`;
- `UNIQUE`;
- transações ACID;
- lock otimista com `@Version`.

A inicialização de uma missão, por exemplo, modifica várias entidades: a missão entra em execução, os agentes entram em missão e a anomalia entra em correção.

Essas mudanças são executadas dentro de uma única transação.

Se uma etapa falhar, toda a transação sofre rollback. Dessa forma, não é possível deixar apenas parte da operação persistida.

O PostgreSQL é adequado para essa responsabilidade porque os dados operacionais dependem fortemente de consistência, relacionamentos e transações.

---

## MongoDB

O MongoDB armazena o **histórico temporal das missões**.

Esse histórico funciona como um log de acontecimentos que cresce por adição.

Entre os eventos registrados podem existir:

- criação de missão;
- designação de agente;
- autorização;
- início;
- intervenção;
- conclusão;
- falha.

Cada tipo de evento pode precisar de detalhes diferentes, o que combina com o modelo flexível de documentos do MongoDB.

O histórico é consultado por missão em ordem temporal.

A gravação não ocorre dentro da mesma transação do PostgreSQL.

Em vez disso, a aplicação publica um evento e utiliza:

```text
@TransactionalEventListener(AFTER_COMMIT)
```

Assim, o registro no MongoDB acontece somente depois que a operação principal foi efetivamente confirmada no PostgreSQL.

Se a operação sofrer rollback, o histórico correspondente não é persistido.

PostgreSQL e MongoDB, porém, não participam de uma única transação distribuída. Se o PostgreSQL confirmar a transação e o MongoDB ficar indisponível logo depois, pode existir uma lacuna no histórico.

Uma solução mais robusta para produção seria utilizar o padrão **Transactional Outbox**.

---

## Redis

O Redis possui uma responsabilidade diferente: armazenar **cache de dados derivados**.

Ele é usado principalmente para:

- estado operacional;
- índice de estabilidade.

Essas informações dependem de várias consultas e contagens no PostgreSQL e podem ser solicitadas com frequência.

O cache possui TTL de aproximadamente:

```text
60 segundos
```

O Redis não utiliza volume persistente porque não é fonte de verdade.

Se o Redis perder todo o conteúdo, nenhum dado de negócio é perdido. A aplicação simplesmente recalcula os valores a partir do PostgreSQL.

A aplicação também possui tolerância à indisponibilidade do cache: se ocorrer erro no Redis, a consulta pode continuar buscando e calculando os dados diretamente da fonte operacional.

---

## Por que não utilizar apenas um banco

As três tecnologias atendem problemas diferentes.

O PostgreSQL atende dados operacionais relacionados que exigem transações e integridade.

O MongoDB atende um histórico temporal que cresce por adição, possui formato variável e é consultado como sequência de acontecimentos.

O Redis atende valores derivados, temporários e muito consultados.

Guardar todas essas cargas no mesmo modelo aumentaria o acoplamento e colocaria sobre o banco operacional responsabilidades que não precisam das mesmas garantias transacionais.

A separação mantém cada tecnologia com uma responsabilidade clara.

---

# Índice de estabilidade

O índice de estabilidade representa a situação operacional em uma escala entre `0` e `100`.

O cálculo implementado no `PainelService` parte de 100 pontos e aplica as seguintes penalidades:

```text
índice = 100
  - (anomalias críticas ativas × 15)
  - (anomalias ativas não críticas × 5)
  - (missões em execução × 3)
  - (missões que falharam (total) × 10)
  - (intervenções SEVERO nos últimos 30 dias × 8)
```

O valor final é limitado ao intervalo entre:

```text
0 e 100
```

Uma anomalia em estado `ESTABILIZADA` ou `IRREVERSIVEL` não é considerada ativa.

---

## Exemplo 1: cenário utilizado nos testes

Considere:

```text
0 anomalias críticas ativas
1 anomalia ativa não crítica
1 missão em execução
0 missões que falharam
0 intervenções severas nos últimos 30 dias
```

Cálculo:

```text
100
- (0 × 15)
- (1 × 5)
- (1 × 3)
- (0 × 10)
- (0 × 8)

= 100 - 5 - 3
= 92
```

Índice de estabilidade:

```text
92
```

---

## Exemplo 2: seed de desenvolvimento

O seed possui:

```text
1 anomalia crítica ativa
1 anomalia ativa não crítica
1 anomalia irreversível
0 missões em execução
0 missões que falharam
0 intervenções severas
```

A anomalia irreversível não é contabilizada como ativa.

Cálculo:

```text
100
- (1 × 15)
- (1 × 5)
- (0 × 3)
- (0 × 10)
- (0 × 8)

= 100 - 15 - 5
= 80
```

Índice de estabilidade:

```text
80
```

---

# Regras de domínio e atomicidade

As transições de estado são realizadas por métodos das entidades.

Não existem setters públicos permitindo alterar livremente os status.

Entre as regras implementadas estão:

- somente agente disponível pode entrar em missão;
- uma anomalia irreversível não aceita nova missão;
- missão precisa possuir pelo menos um agente para iniciar;
- missão emergencial ou associada a anomalia crítica exige autorização;
- uma intervenção só pode ser registrada durante a execução;
- o agente de uma intervenção precisa participar da missão.

As operações que modificam múltiplas entidades são executadas em métodos transacionais.

A inicialização de uma missão modifica simultaneamente:

```text
missão
agentes participantes
anomalia
```

Se ocorrer uma exceção no meio da operação, o PostgreSQL executa rollback e nenhuma alteração parcial permanece persistida.

A suíte possui um teste específico para provar essa atomicidade utilizando PostgreSQL real.

---

# Erros HTTP

A API utiliza `ProblemDetail` como formato padronizado para erros.

| Status | Situação |
|---|---|
| `400 Bad Request` | Payload inválido ou JSON malformado |
| `401 Unauthorized` | Credencial ausente ou inválida |
| `403 Forbidden` | Usuário autenticado sem autorização |
| `404 Not Found` | Recurso inexistente |
| `409 Conflict` | Regra de negócio, transição inválida, conflito otimista ou integridade |
| `500 Internal Server Error` | Erro inesperado |

Erros internos não expõem stack trace ou detalhes sensíveis ao cliente.

---

# Decisões e limitações conhecidas

## Ausência de Transactional Outbox

O histórico do MongoDB é gravado após o commit da operação principal.

Essa abordagem evita registrar no histórico uma transação que sofreu rollback.

Entretanto, ainda existe uma janela entre o commit do PostgreSQL e a persistência no MongoDB.

Se o MongoDB estiver indisponível nesse instante, o dado operacional permanece correto, mas pode ocorrer uma lacuna no histórico.

Em um ambiente de produção, uma evolução seria utilizar **Transactional Outbox** com processamento assíncrono e retentativas.

---

## Evicção do cache

O Redis utiliza TTL de 60 segundos e existem mecanismos de evicção quando operações relevantes modificam o estado.

Algumas alterações relacionadas a agentes e anomalias dependem da estratégia de `@CacheEvict` e do próprio TTL para limitar uma eventual defasagem do valor armazenado.

---

## Identidade do agente na intervenção

A autorização por recurso verifica se o usuário autenticado com perfil `AGENTE` está associado a um agente participante da missão.

Entretanto, a implementação atual não compara o `agenteId` enviado no corpo da requisição com o agente associado ao usuário autenticado.

Essa verificação adicional seria necessária em uma evolução da aplicação.

---

## Missões que falharam

O índice de estabilidade penaliza todas as missões cujo status é `FALHOU`.

Atualmente a missão não armazena a data do encerramento.

Por esse motivo, não é aplicada uma janela temporal às falhas.

Uma evolução seria persistir o instante de encerramento para permitir considerar somente falhas recentes.

As intervenções severas, por outro lado, são consideradas somente dentro da janela dos últimos 30 dias.

---

## Ocorrências

Uma entidade separada para ocorrências não foi implementada no escopo atual.

O histórico temporal no MongoDB foi utilizado para atender à necessidade principal de rastrear a sequência de acontecimentos das missões.

---

# Validação a partir de um clone limpo

O projeto deve poder ser executado sem depender da IDE ou de configurações existentes na máquina do desenvolvedor.

Fluxo esperado em PowerShell:

```powershell
git clone https://github.com/fernandotelheiro/continuum-control.git
cd continuum-control

Copy-Item .env.example .env

docker compose up --build -d

docker compose ps
```

Verifique a saúde:

```powershell
curl.exe -s http://localhost:8080/actuator/health
```

Depois valide a autenticação utilizando a senha definida no `.env.example`:

```powershell
curl.exe -s -u supervisor:senha-usuarios-exemplo http://localhost:8080/missoes
```

A resposta deve incluir a missão do seed:

```text
MIS-DEV-001
```

Também é possível consultar os agentes:

```powershell
curl.exe -s -u supervisor:senha-usuarios-exemplo http://localhost:8080/agentes
```

O resultado deve conter:

```text
Agente Um
Agente Dois
Agente Três
```

A suíte de testes pode ser executada separadamente com:

```powershell
.\mvnw.cmd test
```

Os bancos utilizados pelos testes são iniciados automaticamente por Testcontainers.

---

# Apoio utilizado

Durante o desenvolvimento foram utilizados recursos de inteligência artificial como apoio para orientação, revisão de código, interpretação de erros e organização das etapas de implementação.

Também houve apoio de colega(s) na orientação e revisão do trabalho.

As decisões implementadas, o código produzido e a validação do funcionamento do sistema foram acompanhados e revisados durante o desenvolvimento.