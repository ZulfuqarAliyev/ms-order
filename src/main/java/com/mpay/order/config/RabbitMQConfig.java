package com.mpay.order.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbit.queue.order.q}")
    private String orderQ;

    @Value("${rabbit.queue.order.dlq}")
    private String orderDlq;

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderQ + "_Exchange");
    }

    @Bean
    public DirectExchange orderDlqExchange() {
        return new DirectExchange(orderDlq + "_Exchange");
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(orderQ)
                .withArgument("x-dead-letter-exchange", orderDlq + "_Exchange")
                .withArgument("x-dead-letter-routing-key", orderDlq + "_Key")
                .build();
    }

    @Bean
    public Queue orderDlqQueue() {
        return QueueBuilder.durable(orderDlq).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(orderQ + "_Key");
    }

    @Bean
    public Binding orderDlqBinding() {
        return BindingBuilder.bind(orderDlqQueue())
                .to(orderDlqExchange())
                .with(orderDlq + "_Key");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true);
        return template;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner runner(AmqpAdmin amqpAdmin) {
        return args -> amqpAdmin.initialize();
    }
}