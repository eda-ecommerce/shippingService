package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class ShipmentRequestedEvent extends DomainEvent<ShipmentDTO> {
    public ShipmentRequestedEvent(UUID eventKey, ShipmentDTO payload) {
        super(eventKey, payload);
    }
}
