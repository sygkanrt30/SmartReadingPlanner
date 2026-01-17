package ru.yanin.practice.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.queue.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue.routing.key}")
    private String routingKey;

    @Value("${spring.rabbitmq.queue.message.ttl}")
    private int ttl;

    @Bean
    public Queue myQueue() {
        return QueueBuilder
                .durable(queueName)
                .ttl(ttl)
                .build();
    }

    @Bean
    public DirectExchange myExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(myQueue())
                .to(myExchange())
                .with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean()
    public RabbitTemplate rabbitTemplateForVerification(ConnectionFactory connectionFactory) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setDefaultReceiveQueue(queueName);
        template.setMessageConverter(jsonMessageConverter());
        template.setRoutingKey(routingKey);
        template.setExchange(exchangeName);
        return template;
    }
}
