# ordermanager
Micro serviço responsável pelo gerenciamento de pedidos


Entrar no terminal do container kafka
docker exec -it docker-kafka-1 bash

Criacao do topico kafka
kafka-topics --create --topic=order-created --bootstrap-server=localhost:9092 --partitions 3

Consumir do topico
kafka-console-consumer --bootstrap-server localhost:9092 --topic order-created-dlt --from-beginning

json
{
"externalOrderId": "550e8400-e29b-41d4-a716-446655440000",
"items": [
{
"productName": "Laptop",
"quantity": 1,
"price": 2500.00
},
{
"productName": "Mouse",
"quantity": 2,
"price": 50.00
}
]
}

