package br.com.ordermanager.application.services;

import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import br.com.ordermanager.infrastructure.kafka.event.OrderEvent;
import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

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

            saveOutbox(orderEvent);


        }
    }

    private Order buildOrder(OrderEvent orderEvent) {
        var totalPrice = orderEvent.items().stream()
                .map(orderItem -> orderItem.price().multiply(BigDecimal.valueOf(orderItem.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var order = Order.builder()
                .externalOrderId(orderEvent.externalOrderId())
                .totalPrice(totalPrice)
                .build();

        var items = orderEvent.toOrderItemEntities(order);
        order.setItems(items);

        return order;
    }

    private void saveOutbox(OrderEvent orderEvent) {
        try {

            var outbox = buildOutbox(orderEvent);

            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error("m=processOrder, exception= {}", e.getMessage());

            throw new RuntimeException("Error saving to outbox", e);
        }
    }

    private static Outbox buildOutbox(OrderEvent orderEvent) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var payload = objectMapper.writeValueAsString(orderEvent);

        return Outbox.builder()
                .aggregateId(orderEvent.externalOrderId())
                .payload(payload)
                .build();
    }
}
