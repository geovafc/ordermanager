package br.com.ordermanager.dtos.requests;

import br.com.ordermanager.entities.OrderItem;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
    String productName,
    Integer quantity,
    BigDecimal price
) {
    public OrderItem toEntity() {
        return OrderItem.builder()
                .price(this.price)
                .productName(this.productName)
                .quantity(this.quantity)
                .build();
    }

}
