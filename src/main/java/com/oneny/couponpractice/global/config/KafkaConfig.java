package com.oneny.couponpractice.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oneny.couponpractice.global.constants.Topic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableKafka
public class KafkaConfig {

    private static final String DLT_ERROR_HANDLER = "dtl_error_handler";
    private static final String CONTAINER_STOP_ERROR_HANDLER = "container_stop_error_handler";

    @Bean
    @Primary
    @ConfigurationProperties("spring.kafka")
    public KafkaProperties kafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 멱등한 프로듀서를 만들기 위한 설정
        // 단, 애플리케이션 단에서 장애가 발생하고 acks 받지 못한 경우 다시 처리했을 때
        // 같은 serialNumber 생성하지 못하므로 중복 메시지 발행할 수도 있음
        props.put(ProducerConfig.ACKS_CONFIG, "-1");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ?> kafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(producerFactory(kafkaProperties));
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            @Qualifier(DLT_ERROR_HANDLER) CommonErrorHandler commonErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    @Bean
    @Qualifier(DLT_ERROR_HANDLER)
    public CommonErrorHandler errorDltHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // 실패했을 때 produce 할 kafkaTemplate 전달
//        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), generateBackOff());

        // 직접 전송할 수도 있음
        return new DefaultErrorHandler((record, exception) -> {
            kafkaTemplate.send(Topic.COUPON_ISSUE_REQUEST_DLT, (String) record.key(), record.value());
        }, generateBackOff());
    }

    // retry 후에도 실패한 경우 메시지를 넘기지 않고 해당 컨슈머 스레드를 멈춤
    @Bean
    @Qualifier(CONTAINER_STOP_ERROR_HANDLER)
    public CommonErrorHandler errorHandler() {
        CommonContainerStoppingErrorHandler stoppingErrorHandler = new CommonContainerStoppingErrorHandler();
        AtomicReference<Consumer<?, ?>> consumer2 = new AtomicReference<>();
        AtomicReference<MessageListenerContainer> container2 = new AtomicReference<>();

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((rec, ex) -> {
            // ConsumerRecordRecoverer
            // container stopping error handler 통해서 해당 컨테이너(컨슈머)를 중지시킨다.
            stoppingErrorHandler.handleRemaining(ex, Collections.singletonList(rec), consumer2.get(), container2.get());
        }, generateBackOff()) { // backoff

            @Override
            public void handleRemaining(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
                consumer2.set(consumer);
                container2.set(container);
                super.handleRemaining(thrownException, records, consumer, container);
            }
        };
        // 재시도 불가능한 예외 설정
        errorHandler.addNotRetryableExceptions(JsonProcessingException.class);
        return errorHandler;
    }

    // Retry Policy - Exponential(1 -> 2 -> 4 -> 8 -> 포기)
    private BackOff generateBackOff() {
        ExponentialBackOff backOff = new ExponentialBackOff(1000, 2);
        backOff.setMaxAttempts(3);
        return backOff;
    }
}
