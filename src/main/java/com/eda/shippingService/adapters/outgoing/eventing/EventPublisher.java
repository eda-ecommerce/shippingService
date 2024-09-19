package com.eda.shippingService.adapters.outgoing.eventing;

import com.eda.shippingService.adapters.incoming.eventing.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
