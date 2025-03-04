# Gerenciador de Pedidos
Projeto resposável gerenciamento e cálculo do valor total pedidos. 
Ele ecebe eventos de pedidos de um sistema externo via Kafka, processa esses pedidos e os envia para outro sistema externo, também via Kafka.

---

##  Funcionalidades

-  **Consumo de eventos de pedidos** do tópico `order-created`.
-  **Cálculo do valor total do pedido**.
-  **Verificação de duplicidade** de pedidos.
-  **Persistência do pedido no banco de dados**.
-  **Envio de eventos de pedidos processados** para o tópico `order-processed`.
-  **Utilização de um ErrorHandler personalizado** para lidar com falhas no consumo de mensagens e envio de mensagens para o tópico de DLT `order-created-dlt`.
-  **Implementação do Outbox Pattern** para garantir consistência e confiabilidade na entrega de mensagens.
-  **Publicação assíncrona de eventos da tabela `outbox` para o Kafka**.

---

## Arquitetura
O serviço utiliza o Spring Boot, Kafka e um banco de dados relacional. A comunicação entre o serviço e os sistemas externos é feita através de tópicos Kafka. O Outbox Pattern é implementado para garantir a consistência dos dados e a confiabilidade da entrega de mensagens.

### Componentes
-  **OrderConsumer: Consome eventos de pedidos do tópico Kafka `order-created`**.
-  **OrderService: Processa os pedidos, calcula o valor total e persiste no banco de dados**.
-  **KafkaErrorHandlerConfig: Configura o ErrorHandler personalizado responsável por realizar novas tentativas de consumo das mensagens do tópico `order-created`, após algumas tentativas caso ocorra falha a mensagem é enviado para DLT `order-created-dlt`**.
-  **OutboxPublisher: Agendador de tarefas que roda periódicamente para verificar na tabela "outbox" quais registros precisam ser publicados para no tópico `order-processed`**.



### Tecnologias Utilizadas

- **Java 17 + Spring Boot**
-  **Apache Kafka (Mensageria)**
-  **H2 para facilitar os testes em desenvolvimento (Banco de Dados Relacional)**
-  **JPA/Hibernate (ORM)**
-  **Outbox Pattern (Consistência entre banco e eventos)**
-  **Lombok (Redução de boilerplate code)**
-  **Scheduled Tasks (Publicação assíncrona de eventos)**
-  **Spring Transactional (Garantia de consistência)**

---

## Estrutura do Projeto

```bash
/order-manager
│── src/main/java/br/com/ordermanager
│   │── application/
│   │   │── mappers/               # Conversores de entidades para eventos e vice-versa
│   │   │── services/               # Regras de negócio e processamento de pedidos 
│   │── domain/
│   │   │── entities/               # Entidades de domínio
│   │   │── repositories/           # Interfaces de persistência (JPA)
│   │── infrastructure/
│   │   │── kafka/                  # Integração com Kafka
│   │   │   │── config/             # Configurações do Kafka
│   │   │   │── consumer/           # Consumo de eventos Kafka
│   │   │   │   │── handler/          # Tratamento de erros do consumidor
│   │   │   │── events/             # Eventos Kafka
│   │   │── outbox/                  # Publicação de eventos via Outbox Pattern
```

---

## Configuração

### Pré-requisitos

-  **Java 17** instalado.
-  **Docker** instalado.
-  **Apache Kafka** e **Zookeeper**  rodando localmente.

---

## Como Rodar o Projeto

### Subir o Kafka Localmente

Entrar no diretório ``docker`` do projeto usar o Docker Compose:

```bash
docker-compose up -d
```

### Rodar a Aplicação
Na pasta raiz do projeto rodar os comandos gradle
```bash
./gradlew clean build 
```

Em seguida, rodar o comando a baixo e aguardar a aplicação ser executada para realizar a criação do tópico

```bash
./gradlew bootRun
```

Usar o seguinte json a baixo para enviar como mensagem para o tópico `order-created`
```json
{
  "externalOrderId":"550e8400-e29b-41d4-a716-446655440000",
  "items":[
    {
      "productName":"Laptop",
      "quantity":1,
      "price":2500.00
    },
    {
      "productName":"Mouse",
      "quantity":2,
      "price":50.00
    }
  ]
}
```

---

