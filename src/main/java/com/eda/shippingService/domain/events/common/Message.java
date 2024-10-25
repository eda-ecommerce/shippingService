package com.eda.shippingService.domain.events.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.lang.Nullable;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

@Getter
public abstract class Message<T> {

    private final UUID messageId;
    @Nullable
    private final UUID eventKey;
    private final long timestamp;
    private final T payload;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Message(@Nullable UUID eventKey, T payload) {
        this.eventKey = eventKey;
        this.timestamp = System.currentTimeMillis();
        this.messageId = UUID.randomUUID();
        this.payload = payload;
    }
}

