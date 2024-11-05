package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderConfirmedPayload;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class OrderConfirmedEvent extends DomainEvent<OrderConfirmedPayload> {
    public OrderConfirmedEvent(UUID eventKey, UUID messageId, long timestamp, OrderConfirmedPayload payload) {
        super(eventKey, messageId, timestamp, payload);
    }
}
