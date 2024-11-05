package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

//Since events are using the payload class, there is direct coupling to a DTO. Bad?
@Getter
public class OrderRequestedEvent extends DomainEvent<OrderRequestedDTO>{
    public OrderRequestedEvent(UUID eventKey, UUID messageId, long timestamp, OrderRequestedDTO payload) {
        super(eventKey,messageId,timestamp, payload);
    }
}
