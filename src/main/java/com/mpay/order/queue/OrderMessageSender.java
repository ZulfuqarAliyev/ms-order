package com.mpay.order.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.queue.order.q}")
    private String orderQ;

    public void sendOrderMessage(Object message, CorrelationData correlationData) {
        String exchange = orderQ + "_Exchange";
        String routingKey = orderQ + "_Key";
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                message,
                correlationData
        );
        System.out.println("Message sent to RabbitMQ: " + message);
    }
}

