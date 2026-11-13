# AEP Prato Solidário — Base do projeto, cadastro e login, impacto social

Este pacote é o **primeiro** a ser enviado, porque é a base do projeto. Quando o pull request dele for aceito, avise quem vai enviar o pacote *Lotes de doação, notificações e relatórios*.

Esta pasta contém:
- `arquivos/` — os **39 arquivos** que você deve acrescentar ao repositório https://github.com/GabrielHMaximiano/AEP_PratoSolidario, já nas pastas certas;
- este `LEIA-ME.md`, com os comandos.

> Leia e entenda os arquivos da sua parte antes de enviar: o professor pode pedir que você explique o código que está no seu nome.

## Seus arquivos

| Arquivo (caminho dentro do repositório) |
|---|
| `.gitignore` |
| `database/00_create_database.sql` |
| `database/01_schema.sql` |
| `pom.xml` |
| `src/main/java/br/com/pratosolidario/PratoSolidarioApplication.java` |
| `src/main/java/br/com/pratosolidario/controller/AuthController.java` |
| `src/main/java/br/com/pratosolidario/controller/DoadorController.java` |
| `src/main/java/br/com/pratosolidario/controller/ImpactoController.java` |
| `src/main/java/br/com/pratosolidario/controller/InstituicaoController.java` |
| `src/main/java/br/com/pratosolidario/dto/DoadorRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/DoadorResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/ErroResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/ImpactoResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/InstituicaoRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/InstituicaoResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/LoginRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/UsuarioLogadoResponse.java` |
| `src/main/java/br/com/pratosolidario/exception/AutenticacaoException.java` |
| `src/main/java/br/com/pratosolidario/exception/ConflitoException.java` |
| `src/main/java/br/com/pratosolidario/exception/GlobalExceptionHandler.java` |
| `src/main/java/br/com/pratosolidario/exception/RecursoNaoEncontradoException.java` |
| `src/main/java/br/com/pratosolidario/exception/RegraNegocioException.java` |
| `src/main/java/br/com/pratosolidario/model/Doador.java` |
| `src/main/java/br/com/pratosolidario/model/ImpactoSocial.java` |
| `src/main/java/br/com/pratosolidario/model/Instituicao.java` |
| `src/main/java/br/com/pratosolidario/model/TipoUsuario.java` |
| `src/main/java/br/com/pratosolidario/model/Usuario.java` |
| `src/main/java/br/com/pratosolidario/repository/DoadorRepository.java` |
| `src/main/java/br/com/pratosolidario/repository/InstituicaoRepository.java` |
| `src/main/java/br/com/pratosolidario/repository/RepositorioCrud.java` |
| `src/main/java/br/com/pratosolidario/repository/UsuarioJdbcRepository.java` |
| `src/main/java/br/com/pratosolidario/service/AuthService.java` |
| `src/main/java/br/com/pratosolidario/service/DoadorService.java` |
| `src/main/java/br/com/pratosolidario/service/ImpactoService.java` |
| `src/main/java/br/com/pratosolidario/service/InstituicaoService.java` |
| `src/main/java/br/com/pratosolidario/service/UsuarioService.java` |
| `src/main/java/br/com/pratosolidario/util/SenhaUtil.java` |
| `src/main/resources/application.properties` |
| `src/test/java/br/com/pratosolidario/model/UsuarioValidacaoTest.java` |

## Antes de começar (uma vez só)

1. O **dono do repositório** adiciona os outros integrantes: no GitHub, abra o repositório → *Settings* → *Collaborators* → *Add people* → convide a conta de cada um. Cada um precisa **aceitar o convite** (chega por e-mail).
2. **Cada integrante**, no próprio computador, instala o Git (https://git-scm.com) e configura o nome e **o mesmo e-mail da sua conta do GitHub** — sem isso o commit não aparece no seu perfil:
   ```bash
   git config --global user.name "Seu Nome Completo"
   git config --global user.email "email-da-sua-conta@github.com"
   ```
3. A ordem de envio é **Base do projeto → Lotes de doação → Reservas**. O projeto só compila depois que as três partes estiverem na `main` (as classes de um dependem das dos outros) — isso é normal.

Os comandos abaixo funcionam no **Git Bash (Windows)** e no **Terminal (Linux/macOS)**. Para copiar os arquivos, você também pode usar o Explorador de Arquivos: copie **o conteúdo** da pasta `arquivos/` para dentro da pasta do repositório clonado, aceitando mesclar as pastas.


## Como enviar a sua parte

```bash
# 1) baixar a versão mais recente
git clone https://github.com/GabrielHMaximiano/AEP_PratoSolidario.git
cd AEP_PratoSolidario
git checkout -b sprint-1-cadastro

# 2) copiar os seus arquivos (ajuste o caminho até a pasta do pacote)
cp -r /caminho/da/pasta/arquivos/. .

# 3) um commit por parte do trabalho
git add pom.xml \
    .gitignore \
    src/main/resources/application.properties \
    src/main/java/br/com/pratosolidario/PratoSolidarioApplication.java
git commit -m "Estrutura do projeto Maven e configuração da aplicação"

git add database/00_create_database.sql \
    database/01_schema.sql
git commit -m "Banco de dados: script de criação do banco e das tabelas"

git add src/main/java/br/com/pratosolidario/model/Usuario.java \
    src/main/java/br/com/pratosolidario/model/Doador.java \
    src/main/java/br/com/pratosolidario/model/Instituicao.java \
    src/main/java/br/com/pratosolidario/model/TipoUsuario.java \
    src/main/java/br/com/pratosolidario/model/ImpactoSocial.java \
    src/main/java/br/com/pratosolidario/util/SenhaUtil.java \
    src/main/java/br/com/pratosolidario/exception/
git commit -m "Modelo de usuários: classe abstrata Usuario, Doador, Instituicao e interface ImpactoSocial"

git add src/main/java/br/com/pratosolidario/repository/RepositorioCrud.java \
    src/main/java/br/com/pratosolidario/repository/UsuarioJdbcRepository.java \
    src/main/java/br/com/pratosolidario/repository/DoadorRepository.java \
    src/main/java/br/com/pratosolidario/repository/InstituicaoRepository.java \
    src/main/java/br/com/pratosolidario/service/UsuarioService.java \
    src/main/java/br/com/pratosolidario/service/DoadorService.java \
    src/main/java/br/com/pratosolidario/service/InstituicaoService.java \
    src/main/java/br/com/pratosolidario/service/AuthService.java
git commit -m "Repositórios e serviços de usuários (RepositorioCrud, UsuarioJdbcRepository, UsuarioService)"

git add src/main/java/br/com/pratosolidario/service/ImpactoService.java \
    src/main/java/br/com/pratosolidario/controller/AuthController.java \
    src/main/java/br/com/pratosolidario/controller/DoadorController.java \
    src/main/java/br/com/pratosolidario/controller/InstituicaoController.java \
    src/main/java/br/com/pratosolidario/controller/ImpactoController.java \
    src/main/java/br/com/pratosolidario/dto/DoadorRequest.java \
    src/main/java/br/com/pratosolidario/dto/DoadorResponse.java \
    src/main/java/br/com/pratosolidario/dto/InstituicaoRequest.java \
    src/main/java/br/com/pratosolidario/dto/InstituicaoResponse.java \
    src/main/java/br/com/pratosolidario/dto/LoginRequest.java \
    src/main/java/br/com/pratosolidario/dto/UsuarioLogadoResponse.java \
    src/main/java/br/com/pratosolidario/dto/ImpactoResponse.java \
    src/main/java/br/com/pratosolidario/dto/ErroResponse.java
git commit -m "API de cadastro, login e impacto social (RF07 e RF04)"

git add src/test/java/br/com/pratosolidario/model/UsuarioValidacaoTest.java
git commit -m "Testes de validação, senha e polimorfismo de validar()"

# 4) enviar a branch
git push -u origin sprint-1-cadastro
```

5. No GitHub aparece o botão **Compare & pull request** → crie o pull request para a `main`.
6. **Outro integrante** abre o pull request, revisa (*Files changed*) e clica em **Merge pull request**. Só depois disso o próximo integrante começa.


## Resumo

1. Clonar o repositório e criar a branch.
2. Copiar **o conteúdo** da pasta `arquivos/` para dentro da pasta do repositório (aceitar mesclar as pastas).
3. Fazer os commits, na ordem, com os comandos acima.
4. `git push` e abrir o pull request; **outro integrante** revisa e faz o merge.
