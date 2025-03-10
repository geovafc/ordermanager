package br.com.ordermanager.application.services;

import br.com.ordermanager.application.mappers.OrderMapper;
import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import br.com.ordermanager.infrastructure.kafka.events.OrderEvent;
import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;


    @Transactional
    public void processOrder(OrderEvent orderEvent) {
        var orderNotExist= !orderRepository.existsByExternalOrderId(orderEvent.externalOrderId());

        if (orderNotExist) {

            var order = buildOrder(orderEvent);

            orderRepository.save(order);

            saveOutbox(order);

        }
    }

    private Order buildOrder(OrderEvent orderEvent) {


        var totalPrice = orderEvent.items().stream()
                .map(orderItem -> orderItem
                        .price()
                        .multiply(BigDecimal.valueOf(orderItem.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

       var order = OrderMapper.eventToEntity(orderEvent);
       order.setTotalPrice(totalPrice);

        return order;
    }

    private void saveOutbox(Order order) {
        try {

            var outbox = buildOutbox(order);

            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error("m=processOrder, exception= {}", e.getMessage());

            throw new RuntimeException("Error saving to outbox", e);
        }
    }

    private static Outbox buildOutbox(Order order) throws JsonProcessingException {
         var orderProcessedEvent = OrderMapper.entityToOrderProcessedEvent(order);

        var objectMapper = new ObjectMapper();
        var payload = objectMapper.writeValueAsString(orderProcessedEvent);

        return Outbox.builder()
                .aggregateId(order.getExternalOrderId())
                .payload(payload)
                .build();
    }
}
