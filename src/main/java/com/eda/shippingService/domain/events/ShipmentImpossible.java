package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class ShipmentImpossible extends DomainEvent<ShipmentDTO> {
    public ShipmentImpossible(ShipmentDTO payload) {
        super(null, payload);
    }
}
