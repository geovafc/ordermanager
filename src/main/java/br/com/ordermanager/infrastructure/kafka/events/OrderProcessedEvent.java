package br.com.ordermanager.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderProcessedEvent(
        UUID orderId,
        UUID externalOrderId,
        List<OrderItemProcessedEvent> items,
        String status,
        BigDecimal totalPrice,
        LocalDateTime createdAt
)  {
}
