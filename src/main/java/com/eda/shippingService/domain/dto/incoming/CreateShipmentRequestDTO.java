package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;

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
