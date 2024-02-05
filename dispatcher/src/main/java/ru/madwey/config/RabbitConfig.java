package ru.madwey.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageUpdate() {
        return new Queue(textMessageUpdateQueue);
    }

    @Bean
    public Queue docMessageUpdate() {
        return new Queue(docMessageUpdateQueue);
    }

    @Bean
    public Queue photoMessageUpdate() {
        return new Queue(photoMessageUpdateQueue);
    }

    @Bean
    public Queue answerMessageUpdate() {
        return new Queue(answerMessageQueue);
    }

}
