package com.eda.shippingService.application;

import com.eda.shippingService.domain.events.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
