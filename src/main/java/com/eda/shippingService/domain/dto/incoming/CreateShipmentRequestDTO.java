package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.OrderLineItemDTO;
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
        List<OrderLineItemDTO> requestedProducts
) {
    //This includes "logic" as in there should not be a package yet and the status is Requested, idk if thats good practice
    public Shipment toEntity(){
        return new Shipment(
                orderId,
                destination.toEntity(),
                origin.toEntity(),
                null,
                requestedProducts.stream().map(OrderLineItemDTO::toEntity).toList(),
                ShipmentStatus.RESERVED
        );
    }
}
