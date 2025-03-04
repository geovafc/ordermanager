package br.com.ordermanager.infrastructure.outbox;

import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.entities.enums.OrderStatus;
import br.com.ordermanager.domain.repositories.OrderRepository;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OutboxPublisher {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.order-processed}")
    private String topicOrderProcessed;

    @Scheduled(fixedRateString = "${outbox.scheduled.fixedRate}")
    @Transactional
    public void publishOutBoxEvents() {
        var outboxList = outboxRepository.findByProcessedFalse();

        outboxList.forEach(outbox -> {
            try {
                processOutboxRecord(outbox);

                updateOrderStatusToSend(outbox);

                log.info("m=publishOutBoxEvents, event published: {}", outbox.getId());
            } catch (Exception e) {
                log.error("m=publishOutBoxEvents, error publishing event: {}", outbox.getId(), e);
            }
        });
    }

    private void processOutboxRecord(Outbox outbox) {
        kafkaTemplate.send(this.topicOrderProcessed, outbox.getPayload());

        outbox.setProcessed(true);
        outboxRepository.save(outbox);
    }

    private void updateOrderStatusToSend(Outbox outbox) {
        var order = orderRepository.findByExternalOrderId(outbox.getAggregateId());
        order.setStatus(OrderStatus.SENT);
        orderRepository.save(order);
    }
}