# TCC Pagamento Service

O **tcc-pagamento-service** é um microsserviço responsável pelo processamento de pagamentos.
Ele realiza a integração com o **Mercado Pago**, cria e gerencia cobranças, recebe notificações de pagamento e atualiza o status dos pedidos em outros serviços da solução.

## Funcionalidades

- Criação de pagamentos para pedidos existentes
- Integração com **Mercado Pago** (QR Code / pagamento online)
- Consulta de status de pagamento
- Recebimento de notificações/retornos de pagamento (webhook)
- Atualização de status do pagamento do pedido
- API para consulta de transações

> Observação: o cadastro de clientes, produtos, categorias e pedidos é responsabilidade de outros serviços do domínio (por exemplo, o serviço de pedidos FastFood).

## Arquitetura do Projeto

### Tecnologias Utilizadas

- Kotlin
- Spring Boot
- Gradle (Kotlin DSL)
- JUnit 5 e MockK (testes)
- Swagger / OpenAPI (documentação da API)
- Mercado Pago (integração de pagamentos)
- Banco relacional (PostgreSQL)
- Docker e Docker Compose
- Kubernetes (manifests em `k8s/`)

### Estrutura do Projeto

Este repositório é um **microsserviço isolado**, estruturado em um único módulo Gradle, seguindo boas práticas de separação por camadas dentro do próprio projeto:

- **Camada de domínio / aplicação**: classes Kotlin com as regras de negócio de pagamento (entidades, services, use cases).
- **Camada de infraestrutura**: controllers REST, repositórios, clients de integração (ex.: Mercado Pago), configuração de persistência e segurança.
- **Recursos**: arquivos de configuração e migrations:
  - `src/main/resources/application.properties`
  - `src/main/resources/application-local.properties`
  - `src/main/resources/db/migration` (scripts de migração, ex.: Flyway)

Há também manifestos para deploy em Kubernetes no diretório `k8s/` (deployment, service, hpa etc).

## Como executar a aplicação via Docker

### Requisitos

- Docker instalado
- Docker Compose instalado

### Passo a passo

1. Acesse o diretório `local` do projeto, onde há um `docker-compose.yml` preparado para este serviço:
   ```sh
   cd local
   ```

2. Crie um arquivo `secrets.env` no mesmo diretório do `docker-compose.yml` com as variáveis de ambiente necessárias (tokens do Mercado Pago, URL de banco de dados, etc.).

3. Execute o comando abaixo para subir o banco de dados e a aplicação:

   ```sh
   docker-compose --env-file secrets.env up -d
   ```

A aplicação, por padrão, sobe na porta `8080` (a menos que seja configurada outra porta no `application.properties`).

## Como executar localmente

### Requisitos

- Java 21 instalado
- Docker instalado
- Docker Compose instalado
- Gradle Wrapper (já incluso no projeto: `./gradlew` / `gradlew.bat`)

### Passo a passo

1. Clone o repositório:

   ```sh
   git clone https://github.com/felixgilioli/tcc-pagamento-service.git
   ```

2. Suba o banco de dados com Docker Compose:

   ```sh
   cd tcc-pagamento-service/local
   docker-compose up -d
   ```

3. Volte para o diretório raiz do projeto:

   ```sh
   cd ..
   ```

4. Execute o projeto informando as variáveis de ambiente necessárias (especialmente o token do Mercado Pago) e ativando o profile `local`:

   #### Linux / macOS

   ```bash
   MERCADO_PAGO_ACCESS_TOKEN=TEST-xxx \
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

   #### Windows (PowerShell)

   ```powershell
   $env:MERCADO_PAGO_ACCESS_TOKEN="TEST-xxx"
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

> Ajuste também outras variáveis de ambiente necessárias (URL do banco, credenciais, etc.), caso estejam parametrizadas no `application-local.properties`.

## Como testar a aplicação

- Execute a suíte de testes automatizados:

  ```sh
  ./gradlew test
  ```

- Importe, se disponível, a coleção do Postman relativa a este serviço (por exemplo, `local/postman/tcc-pagamento-service.postman_collection.json`) para testar os endpoints de criação de pagamento, consulta e callbacks.

- Alternativamente, acesse a documentação via Swagger / OpenAPI (URL padrão):

  ```
  http://localhost:8080/swagger-ui/index.html
  ```

> Os endpoints permitem criar um pagamento para um pedido, consultar o status da transação e simular callbacks do Mercado Pago.

## Infraestrutura

### Kubernetes

O diretório `k8s/` contém os manifestos necessários para deploy do serviço:

- `deployment.yaml` – definição do Deployment (imagem Docker, replicas, recursos)
- `service.yaml` – Service para expor o microsserviço dentro do cluster
- `hpa.yaml` – Horizontal Pod Autoscaler (se configurado)
- `namespace.yaml` – namespace utilizado
- `secrets.yaml` – definição de Secrets (tokens, credenciais, etc.)
- `kustomization.yaml` – composição de recursos via Kustomize

### CI/CD e Cloud

Este serviço pode ser integrado a um pipeline de CI/CD (por exemplo, GitHub Actions) para:

- Build e testes da aplicação
- Build e push da imagem Docker para um registry (Docker Hub / ECR)
- Deploy/rollout no cluster Kubernetes (EKS, GKE, etc.)

A infraestrutura de cloud (cluster, banco de dados, rede, etc.) pode ser compartilhada com os demais serviços do TCC, preferencialmente provisionada via Terraform, em um repositório de infraestrutura separado (por exemplo: `tcc-infrastructure-tf`).

