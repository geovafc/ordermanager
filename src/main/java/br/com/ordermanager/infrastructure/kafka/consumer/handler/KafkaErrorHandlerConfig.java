package br.com.ordermanager.infrastructure.kafka.consumer.handler;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;


@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(template
        );

        return new DefaultErrorHandler(recover, new FixedBackOff(1000L, 3L));
    }
}