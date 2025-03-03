package br.com.ordermanager.infrastructure.kafka.consumer;


import br.com.ordermanager.application.services.OrderService;
import br.com.ordermanager.infrastructure.kafka.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    @Autowired
    private OrderService orderService;

//    @KafkaListener(topics = "order-topic", groupId = "order-group", containerFactory = "orderKafkaListenerContainerFactory")
    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "${kafka.consumer.group-id.order-group}")
    public void consumeOrder(OrderEvent orderEvent) {
//        commit offset manual
        orderService.processOrder(orderEvent);
    }
}
