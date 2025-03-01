package br.com.ordermanager.services;

import br.com.ordermanager.dtos.requests.OrderRequestDTO;
import br.com.ordermanager.entities.Order;
import br.com.ordermanager.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void processOrder(OrderRequestDTO orderRequestDTO) {
        var orderNotExist= !orderRepository.existsByExternalOrderId(orderRequestDTO.externalOrderId());

        if (orderNotExist) {

            var order = buildOrder(orderRequestDTO);

            orderRepository.save(order);

        }
    }

    private Order buildOrder(OrderRequestDTO orderRequestDTO) {
        var totalPrice = orderRequestDTO.items().stream()
                .map(orderItem -> orderItem.price().multiply(BigDecimal.valueOf(orderItem.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var order = Order.builder()
                .externalOrderId(orderRequestDTO.externalOrderId())
                .totalPrice(totalPrice)
                .build();

        var items = orderRequestDTO.toOrderItemEntities(order);
        order.setItems(items);

        return order;
    }
}
