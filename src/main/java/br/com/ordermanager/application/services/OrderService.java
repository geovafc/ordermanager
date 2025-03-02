package br.com.ordermanager.application.services;

import br.com.ordermanager.infrastructure.kafka.event.OrderEvent;
import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void processOrder(OrderEvent orderEvent) {
        var orderNotExist= !orderRepository.existsByExternalOrderId(orderEvent.externalOrderId());

        if (orderNotExist) {

            var order = buildOrder(orderEvent);

            orderRepository.save(order);

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
}
