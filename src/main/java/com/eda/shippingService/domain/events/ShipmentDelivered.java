package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ShipmentDelivered extends DomainEvent<ShipmentDTO> {
    public ShipmentDelivered(UUID eventKey, ShipmentDTO payload) {
        super(eventKey, payload);
    }

}