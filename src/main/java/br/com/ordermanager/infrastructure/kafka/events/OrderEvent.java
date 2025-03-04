package br.com.ordermanager.infrastructure.kafka.events;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record OrderEvent(
        UUID externalOrderId,
        List<OrderItemEvent> items
) implements Serializable {


}
