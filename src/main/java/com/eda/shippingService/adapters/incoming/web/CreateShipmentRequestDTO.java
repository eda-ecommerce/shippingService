package com.eda.shippingService.adapters.incoming.web;

import com.eda.shippingService.model.dto.AddressDTO;
import com.eda.shippingService.model.entity.OrderLineItem;
import com.eda.shippingService.model.entity.Shipment;
import com.eda.shippingService.model.entity.ShipmentStatus;

import java.util.List;
import java.util.UUID;

//Should be like OrderCreatedEvent
public record CreateShipmentRequestDTO(
        UUID orderId,
        UUID customerId,
        AddressDTO destination,
        AddressDTO origin,
        List<OrderLineItem> requestedProducts
) {
    public Shipment toEntity(){
        return new Shipment(
                this.destination().toEntity(),
                this.origin().toEntity(),
                this.orderId(),
                null,
                this.requestedProducts(),
                ShipmentStatus.REQUESTED
        );
    }
}
