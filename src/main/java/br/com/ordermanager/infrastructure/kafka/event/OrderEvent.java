package br.com.ordermanager.infrastructure.kafka.event;

import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.entities.OrderItem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record OrderEvent(
        UUID externalOrderId,
        List<OrderItemEvent> items
) {
    public List<OrderItem> toOrderItemEntities(Order order) {
        return items.stream()
                .map(itemDTO -> {
                    var item = itemDTO.toEntity();
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList());
    }

}
