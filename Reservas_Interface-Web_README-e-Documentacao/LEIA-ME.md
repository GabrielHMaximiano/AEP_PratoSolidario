# AEP Prato Solidário — Reservas, interface web, README e documentação

Este pacote é o **terceiro e último**: envie só depois que os pacotes *Base do projeto* e *Lotes de doação* estiverem aceitos na `main`.

Esta pasta contém:
- `arquivos/` — os **24 arquivos** que você deve acrescentar ao repositório https://github.com/GabrielHMaximiano/AEP_PratoSolidario, já nas pastas certas;
- este `LEIA-ME.md`, com os comandos.

> Leia e entenda os arquivos da sua parte antes de enviar: o professor pode pedir que você explique o código que está no seu nome.

## Seus arquivos

| Arquivo (caminho dentro do repositório) |
|---|
| `README.md` |
| `api-exemplos.http` |
| `database/99_reset.sql` |
| `docker-compose.yml` |
| `docs/Prato_Solidario_AEP_1a_entrega_revisada.docx` |
| `docs/Prato_Solidario_AEP_1a_entrega_revisada.pdf` |
| `docs/Prato_Solidario_AEP_2a_entrega.docx` |
| `docs/Prato_Solidario_AEP_2a_entrega.pdf` |
| `docs/diagramas/diagrama-camadas.dot` |
| `docs/diagramas/diagrama-camadas.png` |
| `docs/diagramas/diagrama-classes.dot` |
| `docs/diagramas/diagrama-classes.png` |
| `docs/diagramas/diagrama-der.dot` |
| `docs/diagramas/diagrama-der.png` |
| `src/main/java/br/com/pratosolidario/controller/ReservaController.java` |
| `src/main/java/br/com/pratosolidario/dto/ConclusaoReservaRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/ReservaRequest.java` |
| `src/main/java/br/com/pratosolidario/dto/ReservaResponse.java` |
| `src/main/java/br/com/pratosolidario/model/Reserva.java` |
| `src/main/java/br/com/pratosolidario/model/StatusReserva.java` |
| `src/main/java/br/com/pratosolidario/repository/ReservaRepository.java` |
| `src/main/java/br/com/pratosolidario/service/ReservaService.java` |
| `src/main/resources/static/index.html` |
| `src/test/java/br/com/pratosolidario/model/ImpactoSocialPolimorfismoTest.java` |

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
git checkout -b sprint-3-reservas

# 2) copiar os seus arquivos (ajuste o caminho até a pasta do pacote)
cp -r /caminho/da/pasta/arquivos/. .

# 2b) os PDFs de diagramas da 1ª entrega vão para /docs (mantém o histórico)
mkdir -p docs/diagramas/1a-entrega
git mv "Diagrama Prato Solidario.pdf" docs/diagramas/1a-entrega/
git mv "Diagrama Classes Prato Solidario.pdf" docs/diagramas/1a-entrega/
git commit -m "Organiza os diagramas da 1ª entrega na pasta docs"

# 3) um commit por parte do trabalho
git add src/main/java/br/com/pratosolidario/model/Reserva.java \
    src/main/java/br/com/pratosolidario/model/StatusReserva.java \
    src/main/java/br/com/pratosolidario/repository/ReservaRepository.java \
    src/main/java/br/com/pratosolidario/service/ReservaService.java \
    src/main/java/br/com/pratosolidario/controller/ReservaController.java \
    src/main/java/br/com/pratosolidario/dto/ReservaRequest.java \
    src/main/java/br/com/pratosolidario/dto/ReservaResponse.java \
    src/main/java/br/com/pratosolidario/dto/ConclusaoReservaRequest.java
git commit -m "Reservas de lotes: reservar, concluir retirada e cancelar (RF02)"

git add src/main/resources/static/index.html
git commit -m "Interface web de demonstração"

git add docker-compose.yml \
    README.md \
    api-exemplos.http \
    database/99_reset.sql
git commit -m "Ambiente: docker-compose, README com instruções Windows/Linux e exemplos da API"

git add src/test/java/br/com/pratosolidario/model/ImpactoSocialPolimorfismoTest.java
git commit -m "Teste do cálculo polimórfico de impacto"

git add docs/
git commit -m "Documentação: diagramas e documentos da 1ª e da 2ª entrega"

# 3b) conferir: nada pode ter ficado de fora
git status

# 4) enviar a branch
git push -u origin sprint-3-reservas
```

5. No GitHub aparece o botão **Compare & pull request** → crie o pull request para a `main`.
6. **Outro integrante** abre o pull request, revisa (*Files changed*) e clica em **Merge pull request**. Só depois disso o próximo integrante começa.


## Depois que as três partes estiverem na main — conferir tudo junto

```bash
git checkout main && git pull
docker compose up -d          # ou PostgreSQL instalado (veja o README)
mvn test                      # 12 testes devem passar
mvn spring-boot:run           # abrir http://localhost:8080
```

No GitHub, confira em *Insights → Contributors* que **os três nomes** aparecem. Depois disso, sigam o roteiro de verificação da Seção 9 do documento da 2ª entrega para confirmar que tudo funciona antes de entregar.

## Se algo der errado

| Problema | Solução |
|---|---|
| `Permission denied` / `403` no `git push` | O convite de colaborador ainda não foi aceito, ou o Git está usando outra conta. No Windows: *Gerenciador de Credenciais → Credenciais do Windows* → remova `git:https://github.com` e faça o push de novo. |
| Commit aparece com outro nome/foto | O `user.email` não é o da sua conta do GitHub. Corrija com `git config --global user.email ...` antes dos próximos commits. |
| `git mv`: arquivo não encontrado | Os PDFs já foram movidos ou renomeados — pule o passo 2b. |
| Conflito no pull request | Alguém enviou antes da hora. Rode `git checkout main && git pull`, depois `git checkout sua-branch && git merge main`, resolva e envie de novo. |

## Resumo

1. Clonar o repositório e criar a branch.
2. Copiar **o conteúdo** da pasta `arquivos/` para dentro da pasta do repositório (aceitar mesclar as pastas).
3. Fazer os commits, na ordem, com os comandos acima.
4. `git push` e abrir o pull request; **outro integrante** revisa e faz o merge.
