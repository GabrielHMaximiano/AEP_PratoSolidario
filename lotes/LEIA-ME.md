# AEP Prato Solidário — Lotes de doação, notificações e relatórios

Este pacote é o **segundo**: envie só depois que o pacote *Base do projeto* estiver aceito na `main`. Quando o seu for aceito, avise quem vai enviar o pacote *Reservas, interface web, README e documentação*.

Esta pasta contém:
- `arquivos/` — os **23 arquivos** que você deve acrescentar ao repositório https://github.com/GabrielHMaximiano/AEP_PratoSolidario, já nas pastas certas;
- este `LEIA-ME.md`, com os comandos.

> Leia e entenda os arquivos da sua parte antes de enviar: o professor pode pedir que você explique o código que está no seu nome.

## Seus arquivos

| Arquivo (caminho dentro do repositório) |
|---|
| `database/02_seed.sql` |
| `src/main/java/br/com/pratosolidario/controller/LoteDoacaoController.java` |
| `src/main/java/br/com/pratosolidario/controller/NotificacaoController.java` |
| `src/main/java/br/com/pratosolidario/controller/RelatorioController.java` |
| `src/main/java/br/com/pratosolidario/dto/FiltroRelatorio.java` |
| `src/main/java/br/com/pratosolidario/dto/LotePublicadoResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/LoteRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/LoteResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/NotificacaoResponse.java` |
| `src/main/java/br/com/pratosolidario/dto/RelatorioLinha.java` |
| `src/main/java/br/com/pratosolidario/dto/RelatorioResponse.java` |
| `src/main/java/br/com/pratosolidario/model/JanelaRetirada.java` |
| `src/main/java/br/com/pratosolidario/model/LoteDoacao.java` |
| `src/main/java/br/com/pratosolidario/model/NivelUrgencia.java` |
| `src/main/java/br/com/pratosolidario/model/Notificacao.java` |
| `src/main/java/br/com/pratosolidario/model/StatusLote.java` |
| `src/main/java/br/com/pratosolidario/repository/LoteDoacaoRepository.java` |
| `src/main/java/br/com/pratosolidario/repository/NotificacaoRepository.java` |
| `src/main/java/br/com/pratosolidario/repository/RelatorioRepository.java` |
| `src/main/java/br/com/pratosolidario/service/LoteDoacaoService.java` |
| `src/main/java/br/com/pratosolidario/service/NotificacaoService.java` |
| `src/main/java/br/com/pratosolidario/service/RelatorioService.java` |
| `src/test/java/br/com/pratosolidario/model/LoteDoacaoTest.java` |

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
git clone https://github.com/GabrielHMaximiano/AEP_PratoSolidario.git   # (ou, se já tiver clonado: git checkout main && git pull)
cd AEP_PratoSolidario
git checkout -b sprint-2-lotes

# 2) copiar os seus arquivos (ajuste o caminho até a pasta do pacote)
cp -r /caminho/da/pasta/arquivos/. .

# 3) um commit por parte do trabalho
git add src/main/java/br/com/pratosolidario/model/LoteDoacao.java \
    src/main/java/br/com/pratosolidario/model/JanelaRetirada.java \
    src/main/java/br/com/pratosolidario/model/NivelUrgencia.java \
    src/main/java/br/com/pratosolidario/model/StatusLote.java \
    src/main/java/br/com/pratosolidario/model/Notificacao.java
git commit -m "Modelo de lotes: LoteDoacao composto por JanelaRetirada, urgência e notificação"

git add src/main/java/br/com/pratosolidario/repository/LoteDoacaoRepository.java \
    src/main/java/br/com/pratosolidario/repository/NotificacaoRepository.java \
    src/main/java/br/com/pratosolidario/service/LoteDoacaoService.java \
    src/main/java/br/com/pratosolidario/service/NotificacaoService.java \
    src/main/java/br/com/pratosolidario/controller/LoteDoacaoController.java \
    src/main/java/br/com/pratosolidario/controller/NotificacaoController.java \
    src/main/java/br/com/pratosolidario/dto/LoteRequest.java \
    src/main/java/br/com/pratosolidario/dto/LoteResponse.java \
    src/main/java/br/com/pratosolidario/dto/LotePublicadoResponse.java \
    src/main/java/br/com/pratosolidario/dto/NotificacaoResponse.java
git commit -m "CRUD de lotes e notificação por região (RF01, RF03, RF06)"

git add src/main/java/br/com/pratosolidario/repository/RelatorioRepository.java \
    src/main/java/br/com/pratosolidario/service/RelatorioService.java \
    src/main/java/br/com/pratosolidario/controller/RelatorioController.java \
    src/main/java/br/com/pratosolidario/dto/FiltroRelatorio.java \
    src/main/java/br/com/pratosolidario/dto/RelatorioLinha.java \
    src/main/java/br/com/pratosolidario/dto/RelatorioResponse.java
git commit -m "Relatórios de resgate com filtros e exportação CSV (RF05)"

git add database/02_seed.sql
git commit -m "Banco de dados: dados de demonstração"

git add src/test/java/br/com/pratosolidario/model/LoteDoacaoTest.java
git commit -m "Testes de urgência, reserva dupla e composição do lote"

# 4) enviar a branch
git push -u origin sprint-2-lotes
```

5. No GitHub aparece o botão **Compare & pull request** → crie o pull request para a `main`.
6. **Outro integrante** abre o pull request, revisa (*Files changed*) e clica em **Merge pull request**. Só depois disso o próximo integrante começa.


## Resumo

1. Clonar o repositório e criar a branch.
2. Copiar **o conteúdo** da pasta `arquivos/` para dentro da pasta do repositório (aceitar mesclar as pastas).
3. Fazer os commits, na ordem, com os comandos acima.
4. `git push` e abrir o pull request; **outro integrante** revisa e faz o merge.
