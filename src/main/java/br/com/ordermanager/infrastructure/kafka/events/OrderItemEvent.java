package br.com.ordermanager.infrastructure.kafka.events;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderItemEvent(
    String productName,
    Integer quantity,
    BigDecimal price
) implements Serializable {

}
