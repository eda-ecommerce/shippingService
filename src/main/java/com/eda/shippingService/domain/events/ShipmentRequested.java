package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class ShipmentRequested extends DomainEvent<ShipmentDTO> {
    public ShipmentRequested(ShipmentDTO payload) {
        super(null, payload);
    }
}
