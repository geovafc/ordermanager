package br.com.ordermanager.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemProcessedEvent(
        UUID id,
        String productName,
        Integer quantity,
        BigDecimal price
        ){
}
