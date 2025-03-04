package br.com.ordermanager.application.mappers;

import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.entities.OrderItem;
import br.com.ordermanager.infrastructure.kafka.events.OrderEvent;
import br.com.ordermanager.infrastructure.kafka.events.OrderItemEvent;
import br.com.ordermanager.infrastructure.kafka.events.OrderItemProcessedEvent;
import br.com.ordermanager.infrastructure.kafka.events.OrderProcessedEvent;

import java.util.List;
import java.util.stream.Collectors;


public class OrderMapper {

    public static Order eventToEntity(OrderEvent orderEvent) {

        var order = new Order();
        order.setExternalOrderId(orderEvent.externalOrderId());

        var orderItems = toOrderItemEntitieList(orderEvent, order);

        order.setItems(orderItems);

        return order;
    }

    private static List<OrderItem> toOrderItemEntitieList(OrderEvent event, Order order) {

        return event.items().stream()
                .map(orderItemEvent -> {
                    var item = orderItemEventToEntity(orderItemEvent);
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList());
    }

    private static OrderItem orderItemEventToEntity(OrderItemEvent orderItemEvent) {

        return OrderItem.builder()
                .price(orderItemEvent.price())
                .productName(orderItemEvent.productName())
                .quantity(orderItemEvent.quantity())
                .build();
    }

    public static OrderProcessedEvent entityToOrderProcessedEvent(Order order) {
        var orderItems = order.getItems();

        var itemEvents = toOrderItemProcessedEventList(orderItems);

        return new OrderProcessedEvent(
                order.getId(),
                order.getExternalOrderId(),
                itemEvents,
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }

    private static List<OrderItemProcessedEvent> toOrderItemProcessedEventList(List<OrderItem> orderItems) {

        return orderItems.stream()
                .map(item -> new OrderItemProcessedEvent(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());
    }
}
