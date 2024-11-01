package com.eda.shippingService.domain.events.common;

import java.util.UUID;

public abstract class DomainEvent<T> extends Message<T>{
    public DomainEvent(UUID eventKey, T payload) {
        super(eventKey, payload);
    }
    public DomainEvent(UUID eventKey, UUID messageId, long timestamp, T payload) {
        super(eventKey, messageId, timestamp, payload);
    }
}
