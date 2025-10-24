# SESP-CT

Este módulo OpenMRS estende a plataforma SESP (Sistema Electrónico de Seguimento de Pacientes) para suportar a interoperabilidade com o Sistema do Comité Terapêutico Nacional (SCT) para a gestão de casos suspeitos de Falência Terapêutica (FT) ao HIV em Moçambique. Desenvolvido pela C‑Saúde em colaboração com o MISAU e a ITECH, o módulo permite a integração automática de submissões de casos de FT e recomendações terapêuticas no SESP, melhorando o seguimento clínico, minimizando tarefas manuais e optimizando a monitorização nacional dos cuidados de HIV.

---

## Requisitos Prévios

Antes de instalar este módulo, certifique‑se de que o seu ambiente cumpre os seguintes requisitos:

- **Plataforma OpenMRS:** Versão 2.3.3 (referida como SESP).
- **Java:** Versão 8 (JDK 8 / JRE 8).
- **Servidor de Aplicação:** Apache Tomcat 8 ou 9.
- **Base de Dados:** MySQL 5.6 ou superior.
- **Ferramentas:** Acesso à linha de comandos com `openssl` (normalmente disponível em Linux/WSL/Git Bash for Windows).

---

## Guia de Instalação e Configuração

Siga estes passos para instalar e configurar o módulo SESP‑CT.

### Passo 1 — Instalar o Módulo

1. Aceda à sua instância SESP (OpenMRS) como Administrador.
2. Navegue até `Administração` > `Módulos` > `Gerir Módulos`.
3. Clique em **"Carregar Módulo"** e selecione o ficheiro `.omod` do SESP‑CT que descarregou.
4. Após o carregamento, o módulo será listado.

### Passo 2 — Gerar Chaves Criptográficas

O módulo necessita de um par de chaves (privada e pública) para assinar digitalmente os pedidos e garantir a autenticação com o middleware. Estas chaves devem estar em **PKCS#8**.

Execute estes comandos num terminal (Linux, WSL, ou Git Bash):

```bash
# 1. Gerar uma chave privada RSA de 4096 bits
openssl genrsa -out client_private.pem 4096

# 2. Converter para PKCS#8 (formato requerido pelo módulo)
openssl pkcs8 -topk8 -inform PEM -outform PEM -in client_private.pem -out client_private_pkcs8.pem -nocrypt

# 3. Extrair a chave pública correspondente
openssl rsa -in client_private_pkcs8.pem -pubout -out client_public.pem
```

Irá necessitar do conteúdo destes ficheiros no próximo passo.

### Passo 3 — Gerar um Salt

Para o registo inicial, é necessário um valor "salt" aleatório.

```bash
# Gere uma string aleatória segura de 32 bytes
openssl rand -base64 32
```

Copie o valor gerado (ex.: `aBcDeF...xYz/123=`) para usar na configuração.

### Passo 4 — Configurar as Propriedades Globais

Navegue até Administração > Configurações > e procure pela secção `sespct`. Configure as seguintes propriedades:

| Propriedade                  | Valor Exemplo                                 | Descrição                                                                 |
|-----------------------------|-----------------------------------------------|---------------------------------------------------------------------------|
| `sespct.api.baseUrl`        | `https://sctdev.csaude.org.mz/api`            | URL base da API do middleware SESP‑CT(HTTPS).                             |
| `sespct.api.usCode`         | `1040110`                                     | Código SISMA da Unidade Sanitária (US).                                   |
| `sespct.api.clientId`       | `1040110`                                     | `clientId` / username para autenticação (recomendado igual ao usCode).    |
| `sespct.api.clientSecret`   | `senha_forte_123!`                            | `clientSecret` / password para autenticação na API.                       |
| `sespct.api.salt`          | `(valor do Passo 3)`                           | Valor `salt` aleatório gerado no Passo 3.                                 |
| `sespct.api.privateKey`     | `-----BEGIN PRIVATE KEY...`                   | Conteúdo completo de `client_private_pkcs8.pem` (inclua BEGIN/END).       |
| `sespct.api.publicKey`      | `-----BEGIN PUBLIC KEY...`                    | Conteúdo completo de `client_public.pem` (inclua BEGIN/END).              |
| `sespct.api.serverPublicKey`| *(deixar em branco inicialmente)*              | Chave pública do servidor middleware (obtida automaticamente após registo).|
| `sespct.api.clientRegistered`| `false`                                      | Será actualizado para `true` após registo bem‑sucedido.                   |

> **Nota Importante:** A `privateKey` é extremamente sensível. Trate‑a como uma senha mestra e nunca a partilhe publicamente.

### Passo 5 — Reiniciar o Servidor Tomcat

Para que o módulo carregue as novas configurações e inicie corretamente, reinicie o Tomcat.

Se estiver a usar Docker, por exemplo:

```bash
docker restart tomcat
```

(Substitua `tomcat` pelo nome do seu contentor Tomcat.)

Após o reinício, verifique em **Administração de Módulo** se o módulo SESP‑CT iniciou com sucesso. O módulo tentará registar‑se automaticamente no middleware usando as chaves e credenciais fornecidas.

### Passo 6 — Configurar a Tarefa Agendada (Scheduler)

Esta tarefa sincroniza periodicamente os dados (novos pedidos e recomendações) com o middleware.

1. Navegue até `Administração` > `Agenda` > `Administração de Agenda`.
2. Clique em **"Agendar nova tarefa"**.

Preencha os campos:

- **Nome:** `Sesp CT Scheduler Task` (ou outro nome descritivo)
- **Classe de Agendamento:** `org.openmrs.module.sespct.api.sync.SespctSchedulerTask`
- **Descrição:** `Tarefa para sincronizar pedidos e recomendações do SESP‑CT.`

Configuração recomendada para produção:

| Campo                  | Valor       |
|------------------------|-------------|
| Iniciar no arranque    | Selecionado |
| Intervalo de repetição | `1800`      |
| Unidade de intervalo   | Segundos    |

(Isto configura a tarefa para executar a cada 30 minutos.)

Clique em **Gravar**.

### Passo 7 — Configurar Funções de Utilizador (Roles)

Para permitir o acesso à interface do módulo, atribua as roles adequadas aos utilizadores:

1. Navegue até `Administração` > `Utilizadores` > `Gerir Utilizadores`.
2. Edite o utilizador pretendido.
3. Na secção **Funções**, atribua uma das seguintes roles:

- **Gestor de CT Interoperabilidade:** Acesso total.

4. Clique em **Gravar Utilizador**.

---

## Suporte

Este módulo foi desenvolvido e é mantido pela C‑Saúde. Para suporte ou reporte de bugs, contacte a equipa de desenvolvimento.

---

## Segurança e Boas Práticas

- Nunca guarde a `privateKey` em repositórios públicos.

---

