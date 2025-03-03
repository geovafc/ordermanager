# ordermanager
Micro serviço responsável pelo gerenciamento de pedidos


Entrar no terminal do container kafka
docker exec -it docker-kafka-1 bash

Criacao do topico kafka
kafka-topics --create --topic=order-created --bootstrap-server=localhost:9092 --partitions 3

Consumir do topico
kafka-console-consumer --bootstrap-server localhost:9092 --topic order-created-dlt --from-beginning

