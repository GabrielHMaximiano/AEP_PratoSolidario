# Prato Solidário 🍞 — ODS 2 Fome Zero

Plataforma que conecta **estabelecimentos com excedente de alimentos** (doadores) a **instituições assistenciais** (receptoras), priorizando os lotes que vencem primeiro, para reduzir o desperdício e ampliar o acesso a alimentos de famílias em vulnerabilidade.

Projeto da AEP (Atividade de Estudo Programado) — Engenharia de Software, UniCesumar, 4º semestre (2026).

---

## 1. Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 (funciona também com 21) |
| Framework | Spring Boot 3.3 (API REST + JDBC) |
| Banco de dados | PostgreSQL 14 ou superior |
| Build / dependências | Maven |
| Testes | JUnit 5 |
| Interface de demonstração | HTML + JavaScript (sem frameworks), servida pelo próprio Spring |

## 2. Estrutura do repositório

```
AEP_PratoSolidario/
├── database/                  ← scripts SQL (executados automaticamente pela aplicação)
│   ├── 00_create_database.sql   cria o banco "prato_solidario" (só na instalação manual)
│   ├── 01_schema.sql            DDL: tabelas, chaves e restrições
│   ├── 02_seed.sql              DML: dados de demonstração
│   └── 99_reset.sql             apaga as tabelas (para recomeçar a demonstração)
├── docs/                      ← documentos das entregas e diagramas
├── src/main/java/br/com/pratosolidario/
│   ├── model/                 ← domínio: Usuario (abstrata), Doador, Instituicao, ImpactoSocial (interface), LoteDoacao, JanelaRetirada, Reserva, Notificacao
│   ├── repository/            ← acesso ao banco (JDBC): RepositorioCrud<T> (interface) e implementações
│   ├── service/               ← regras de negócio
│   ├── controller/            ← API REST
│   ├── dto/                   ← objetos de entrada/saída da API (records)
│   ├── exception/             ← exceções e tratamento global de erros
│   └── util/                  ← SenhaUtil (hash de senha)
├── src/main/resources/
│   ├── application.properties ← configuração (conexão com o banco)
│   └── static/index.html      ← interface de demonstração
├── src/test/java/             ← testes unitários (JUnit 5)
├── api-exemplos.http          ← chamadas prontas da API (IntelliJ / VS Code)
├── docker-compose.yml         ← PostgreSQL em contêiner (opcional)
└── pom.xml
```

## 3. Requisitos funcionais × implementação

| RF | Descrição (1ª entrega revisada) | Onde está no código | Endpoint |
|---|---|---|---|
| RF01 | Doador cadastra lote (tipo, kg, validade, janela de retirada) | `Doador.publicarLote()`, `LoteDoacao.validar()`, `LoteDoacaoService.publicar()` | `POST /api/lotes` |
| RF02 | Instituição vê lotes ordenados pela validade e reserva | `LoteDoacaoRepository.listarDisponiveis()`, `Instituicao.reservarLote()`, `ReservaService.reservar()` | `GET /api/lotes/disponiveis`, `POST /api/reservas` |
| RF03 | Nível de urgência automático (crítico / atenção / normal) | `LoteDoacao.calcularUrgencia()`, `NivelUrgencia` | campo `urgencia` em todo lote |
| RF04 | Impacto social diferente por perfil (kg × famílias) | `ImpactoSocial.calcularImpacto()` sobrescrito em `Doador` e `Instituicao` | `GET /api/impacto`, `GET /api/impacto/{id}` |
| RF05 | Relatórios filtráveis por período, doador e instituição, com exportação | `RelatorioRepository`, `RelatorioService.exportarCsv()` | `GET /api/relatorios/resgates`, `.../resgates/csv` |
| RF06 | Notificar instituições da região quando lote crítico é publicado | `NotificacaoService.notificarInstituicoesDaRegiao()`, `Notificacao.paraLoteUrgente()` | `GET /api/instituicoes/{id}/notificacoes` |
| RF07 | Cadastro de doadores/instituições e login com senha em hash | `UsuarioService<T>`, `AuthService`, `Usuario.autenticar()`, `SenhaUtil` | `POST /api/doadores`, `POST /api/instituicoes`, `POST /api/auth/login` |

**CRUD da entidade principal (LoteDoacao):** `POST /api/lotes` (Create) · `GET /api/lotes`, `GET /api/lotes/{id}` (Read) · `PUT /api/lotes/{id}` (Update) · `DELETE /api/lotes/{id}` (Delete).
Doadores (`/api/doadores`) e instituições (`/api/instituicoes`) também têm CRUD completo.

**Regras de urgência (RF03):** vencido = validade já passou · crítico = vence hoje ou amanhã · atenção = vence em 2 ou 3 dias · normal = mais de 3 dias. Lotes críticos disparam o RF06.

## 4. Pilares de POO no código

| Pilar | Onde |
|---|---|
| **Classe abstrata** | `model/Usuario` (não pode ser instanciada), `repository/UsuarioJdbcRepository<T>`, `service/UsuarioService<T>` |
| **Interface** | `model/ImpactoSocial`, `repository/RepositorioCrud<T>` (implementada por todos os repositórios) |
| **Herança** | `Doador extends Usuario`, `Instituicao extends Usuario`; `DoadorRepository extends UsuarioJdbcRepository<Doador>`; `DoadorService extends UsuarioService<Doador>`; `ConflitoException extends RegraNegocioException` |
| **Polimorfismo (@Override)** | `calcularImpacto()`, `getUnidadeImpacto()`, `getTipoUsuario()` e `validar()` (com `super.validar()`) em `Doador`/`Instituicao`; `ImpactoService.calcularTodos()` chama `calcularImpacto()` numa lista de `Usuario` com objetos dos dois tipos |
| **Encapsulamento** | atributos `private` com getters/setters; mudanças de estado só por métodos com regra (`reservar()`, `concluir()`, `cancelar()`); senha guardada apenas como hash |
| **Composição** | `LoteDoacao` é composto por uma `JanelaRetirada` (parte imutável, criada e substituída só pelo lote); `Doador` possui a lista de `LoteDoacao` que publicou |

## 5. Como executar

### 5.1 Pré-requisitos

- **JDK 17 ou superior** — confira com `java -version`.
- **Maven 3.9+** — ou use o **IntelliJ IDEA**, que já traz o Maven embutido.
- **PostgreSQL 14+** instalado **ou** **Docker Desktop** (escolha uma das opções do passo 5.2).

### 5.2 Passo 1 — preparar o banco de dados

**Opção A — Docker (mais simples, igual em Windows e Linux)**

```bash
docker compose up -d
```
Pronto: o banco `prato_solidario` fica disponível em `localhost:5432` com usuário `postgres` e senha `postgres`.

**Opção B — PostgreSQL instalado na máquina**

1. Crie o banco (só uma vez), pelo pgAdmin (botão direito em *Databases → Create → Database…*, nome `prato_solidario`) ou pelo terminal:
   ```bash
   psql -U postgres -f database/00_create_database.sql
   ```
2. Se a senha do seu usuário `postgres` **não** for `postgres`, informe-a pela variável `DB_PASSWORD` no passo 5.3.

> As tabelas e os dados de exemplo **não** precisam ser criados à mão: ao iniciar, a aplicação executa `database/01_schema.sql` e `database/02_seed.sql` automaticamente. Os scripts podem ser rodados várias vezes sem erro.

### 5.3 Passo 2 — iniciar a aplicação

**No IntelliJ IDEA (Windows ou Linux)**
1. *File → Open…* e selecione a pasta do projeto (onde está o `pom.xml`); aguarde o Maven baixar as dependências.
2. Abra `src/main/java/br/com/pratosolidario/PratoSolidarioApplication.java` e clique em ▶ **Run**.
3. Se a senha do banco for diferente de `postgres`: *Run → Edit Configurations… → Environment variables* → `DB_PASSWORD=sua_senha`.

**Pelo terminal — Linux / macOS**
```bash
mvn spring-boot:run
# senha diferente:
DB_PASSWORD=sua_senha mvn spring-boot:run
```

**Pelo terminal — Windows (PowerShell)**
```powershell
mvn spring-boot:run
# senha diferente:
$env:DB_PASSWORD="sua_senha"; mvn spring-boot:run
```

**Pelo terminal — Windows (Prompt de Comando / CMD)**
```bat
set DB_PASSWORD=sua_senha
mvn spring-boot:run
```

A aplicação está no ar quando aparecer no console a linha `Started PratoSolidarioApplication`.

### 5.4 Passo 3 — usar

- **Interface de demonstração:** http://localhost:8080
- **API REST:** http://localhost:8080/api/... (exemplos prontos em `api-exemplos.http`)

Contas de demonstração (senha **123456** para todas):

| Perfil | E-mail | Região |
|---|---|---|
| Doador — Padaria da Dona Marta | `marta@padaria.com` | Zona Norte |
| Doador — Hortifruti Bom Preço | `contato@bompreco.com` | Centro |
| Instituição — ONG Mesa Solidária | `contato@mesasolidaria.org` | Zona Norte |
| Instituição — Lar São Vicente | `lar@saovicente.org` | Centro |

Roteiro sugerido para a demonstração:
1. Entre como **Padaria** e publique um lote com validade **hoje** → o sistema avisa que ele é *crítico* e que as instituições da Zona Norte foram notificadas.
2. Edite e exclua um lote disponível (CRUD completo).
3. Saia e entre como **ONG Mesa Solidária** → veja a notificação, os lotes ordenados por urgência, reserve um e conclua a retirada informando as famílias atendidas.
4. Abra **Relatórios**, filtre e exporte o CSV; abra **Impacto geral** para ver o polimorfismo (kg para doadores, famílias para instituições).

### 5.5 Testes automatizados

```bash
mvn test
```
Os testes unitários (pasta `src/test/java`) validam urgência, reserva dupla, validações e o cálculo polimórfico de impacto. Eles **não** precisam do banco ligado.

### 5.6 Gerar o executável (.jar)

```bash
mvn clean package
java -jar target/prato-solidario-1.0.0.jar
```

## 6. Configuração por variáveis de ambiente

| Variável | Padrão | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/prato_solidario` | endereço do banco |
| `DB_USER` | `postgres` | usuário do banco |
| `DB_PASSWORD` | `postgres` | senha do banco |
| `PORT` | `8080` | porta da aplicação |

## 7. Compatibilidade entre sistemas operacionais

- O código Java roda na JVM e não usa nenhum recurso específico de Windows ou Linux (caminhos de arquivo, comandos do sistema etc.).
- Os scripts SQL ficam dentro do classpath (o `pom.xml` copia a pasta `/database`), então a aplicação os encontra em qualquer SO e em qualquer pasta de execução.
- Toda a configuração sensível ao ambiente (endereço, usuário e senha do banco, porta) é feita por **variáveis de ambiente**, com a sintaxe de cada SO mostrada no passo 5.3.
- O `docker-compose.yml` oferece o mesmo banco, na mesma versão, em qualquer SO.
- Arquivos em UTF-8; o CSV exportado inclui BOM para o Excel exibir os acentos corretamente.

## 8. Problemas comuns

| Sintoma | Causa provável e solução |
|---|---|
| `Connection to localhost:5432 refused` | O PostgreSQL não está rodando. Inicie o serviço (Windows: *Serviços → postgresql*; Linux: `sudo systemctl start postgresql`) ou rode `docker compose up -d`. |
| `password authentication failed for user "postgres"` | A senha do seu PostgreSQL não é `postgres`. Defina `DB_PASSWORD` (passo 5.3). |
| `database "prato_solidario" does not exist` | Faltou criar o banco (passo 5.2, opção B). |
| `Port 8080 was already in use` | Outra aplicação usa a porta 8080. Rode com `PORT=8081` (Linux) ou `$env:PORT="8081"` (PowerShell). |
| Docker: `port is already allocated` | Já existe um PostgreSQL local na porta 5432. Use a opção B, ou troque a porta no `docker-compose.yml` para `"5433:5432"` e rode com `DB_URL=jdbc:postgresql://localhost:5433/prato_solidario`. |
| `invalid target release: 17` | O JDK instalado é anterior ao 17. Instale o JDK 17 ou 21 (ex.: Eclipse Temurin). |
| Quero recomeçar com os dados de exemplo | Rode `database/99_reset.sql` no banco e reinicie a aplicação. |
