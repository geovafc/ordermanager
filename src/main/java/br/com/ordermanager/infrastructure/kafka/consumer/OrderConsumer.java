package br.com.ordermanager.infrastructure.kafka.consumer;


import br.com.ordermanager.application.services.OrderService;
import br.com.ordermanager.infrastructure.kafka.events.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {
    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "${kafka.consumer.group-id.order-group}")
    public void consumeOrder(OrderEvent orderEvent, Acknowledgment acknowledgment) {

        log.info("m=consumeOrder, orderEvent= {}", orderEvent);

        orderService.processOrder(orderEvent);
        acknowledgment.acknowledge();

    }
}
