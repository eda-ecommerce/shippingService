package com.eda.shippingService.domain.events.common;

import java.util.UUID;

public abstract class Command<T> extends Message<T> {
    public Command(UUID eventKey, T payload) {
        super(eventKey, payload);
    }
}
