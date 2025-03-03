package br.com.ordermanager.infrastructure.outbox;

import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.entities.enums.OrderStatus;
import br.com.ordermanager.domain.repositories.OrderRepository;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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

    @Scheduled(fixedRate = 5000) // Executa a cada 5 segundos
    @Transactional
    public void publishOutBoxEvents() {
        List<Outbox> outboxList = outboxRepository.findByProcessedFalse();
        outboxList.forEach(outbox -> {
            try {
                //adicionar topico nos properties
                kafkaTemplate.send("order-processed", outbox.getPayload());
                outbox.setProcessed(true);
                outboxRepository.save(outbox);

                var order = orderRepository.findByExternalOrderId(outbox.getAggregateId());
                order.setStatus(OrderStatus.SENT);
                orderRepository.save(order);

                log.info("m=publishOutBoxEvents, event published: {}", outbox.getId());
            } catch (Exception e) {
                log.error("m=publishOutBoxEvents, error publishing event: {}", outbox.getId(), e);
            }
        });
    }
}