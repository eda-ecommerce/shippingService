package com.eda.shippingService.domain.events.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Getter
@JsonSerialize
@JsonIgnoreProperties
public abstract class Message<T> {

    private final UUID messageId;
    @Nullable
    private final UUID eventKey;
    private final long timestamp;
    private final T payload;

    public Message(@Nullable UUID eventKey, T payload) {
        this.eventKey = eventKey;
        this.timestamp = System.currentTimeMillis();
        this.messageId = UUID.randomUUID();
        this.payload = payload;
    }

    public Message(@Nullable UUID eventKey, UUID messageId, long timestamp, T payload) {
        this.eventKey = eventKey;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.payload = payload;
    }
}

