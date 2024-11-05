package com.eda.shippingService.adapters;

import com.eda.shippingService.application.eventHandlers.EventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.domain.events.OrderRequestedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class KafkaOrderListener {

    private final EventHandler<OrderRequestedEvent> orderRequestedEventHandler;
    private final EventHandler<OrderConfirmedEvent> orderConfimedEventEventHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KafkaOrderListener(EventHandler<OrderRequestedEvent> orderRequestedEventHandler, EventHandler<OrderConfirmedEvent> orderConfimedEventEventHandler) {
        this.orderRequestedEventHandler = orderRequestedEventHandler;
        this.orderConfimedEventEventHandler = orderConfimedEventEventHandler;
    }

    //This should probably more fine-grained
    @KafkaListener(topics = "order", groupId = "shipping-service")
    public void listen(ConsumerRecord<String, String> record) {
        var headers = record.headers().toArray();
        var operation = Arrays.stream(headers)
                .filter(header -> header.key().equals("operation"))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElseThrow();
        var messageId = UUID.fromString(Arrays.stream(headers)
                .filter(header -> header.key().equals("messageId"))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElseThrow());
        try {
            switch (operation) {
                case "OrderRequested":
                    orderRequestedEventHandler.handle(
                            new OrderRequestedEvent(null, messageId,record.timestamp(), objectMapper.readValue(record.value(), OrderRequestedDTO.class)));
                    break;
                case "OrderConfirmed":
                    orderConfimedEventEventHandler.handle(
                            new OrderConfirmedEvent(null, messageId, record.timestamp(), objectMapper.readValue(record.value(), OrderConfirmedDTO.class)));
                    break;
                default:
                    log.error("Unsupported operation: {}", operation);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing message with id: {} value: {}", messageId, record.value());
            log.error(e.getMessage());
        }
    }
}