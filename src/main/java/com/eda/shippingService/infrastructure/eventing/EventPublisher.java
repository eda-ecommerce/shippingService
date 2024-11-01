package com.eda.shippingService.infrastructure.eventing;

import com.eda.shippingService.domain.events.common.Message;

@SuppressWarnings("rawtypes")
public interface EventPublisher {
    void publish(Message event, String topic);
}
