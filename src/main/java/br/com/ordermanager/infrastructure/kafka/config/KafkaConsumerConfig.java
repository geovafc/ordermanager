package br.com.ordermanager.infrastructure.kafka.config;

import br.com.ordermanager.infrastructure.kafka.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Autowired
    CommonErrorHandler commonErrorHandler;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(commonErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${spring.kafka.consumer.bootstrap-servers}");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${kafka.consumer.group-id.order-group}");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
//        props.put(String.valueOf(StringDeserializer.class), StringDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvent.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "br.com.ordermanager.infrastructure.kafka.event"); // Adicione o pacote do seu OrderEvent

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OrderEvent.class));
    }


}