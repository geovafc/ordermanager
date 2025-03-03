package br.com.ordermanager.infrastructure.kafka.event;

import br.com.ordermanager.domain.entities.OrderItem;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderItemEvent(
    String productName,
    Integer quantity,
    BigDecimal price
) implements Serializable {
    public OrderItem toEntity() {
        return OrderItem.builder()
                .price(this.price)
                .productName(this.productName)
                .quantity(this.quantity)
                .build();
    }

}
