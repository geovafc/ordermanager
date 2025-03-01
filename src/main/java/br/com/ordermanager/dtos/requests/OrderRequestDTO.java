package br.com.ordermanager.dtos.requests;

import br.com.ordermanager.entities.Order;
import br.com.ordermanager.entities.OrderItem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record OrderRequestDTO(
        UUID externalOrderId,
        List<OrderItemRequestDTO> items
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
