package com.mpay.order.service;

import com.mpay.order.dao.entity.OutboxEntity;
import com.mpay.order.dao.repository.OutboxRepository;
import com.mpay.order.model.enums.OutboxStatus;
import com.mpay.order.queue.OrderMessageSender;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OutboxPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;
    private final OrderMessageSender orderMessageSender;

    private final ConcurrentHashMap<String, Long> correlationMap = new ConcurrentHashMap<>();

    public OutboxPublisherService(RabbitTemplate rabbitTemplate, OutboxRepository outboxRepository, OrderMessageSender orderMessageSender) {
        this.rabbitTemplate = rabbitTemplate;
        this.outboxRepository = outboxRepository;
        this.orderMessageSender = orderMessageSender;
    }

    @PostConstruct
    public void init() {
        setupPublisherConfirmCallback();
    }

    public void setupPublisherConfirmCallback() {
        System.out.println("setupPublisherConfirmCallback");
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String correlationId = correlationData != null ? correlationData.getId() : null;

            if (correlationId == null) return;

            Long outboxId = correlationMap.get(correlationId);

            if (outboxId != null) {
                var entityOpt = outboxRepository.findById(outboxId);
                if (entityOpt.isPresent()) {
                    var entity = entityOpt.get();
                    if (ack) {
                        System.out.println("Delivery published for ID=" + correlationId);
                        entity.setStatus(OutboxStatus.PUBLISHED);
                    } else {
                        System.err.println("Delivery failed for ID=" + correlationId + " cause=" + cause);
                        entity.setStatus(OutboxStatus.FAILED);
                    }
                    outboxRepository.save(entity);
                }
            }

            correlationMap.remove(correlationId);
        });
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void publishPendingEvents() {
        System.out.println("Publishing pending events...");
        List<OutboxEntity> events = outboxRepository.findTop10ByStatusInOrderByIdAsc(Arrays.asList(OutboxStatus.PENDING, OutboxStatus.FAILED));

        for (OutboxEntity event : events) {
            try {
                String correlationId = String.valueOf(event.getId());
                CorrelationData correlationData = new CorrelationData(correlationId);
                correlationMap.put(correlationId, event.getId());
                orderMessageSender.sendOrderMessage(event.getPayload(), correlationData);
            } catch (Exception ex) {
                System.err.println("Error sending message: " + ex.getMessage());
            }
        }
        System.out.println("Finished publishing pending events.");
    }
}
