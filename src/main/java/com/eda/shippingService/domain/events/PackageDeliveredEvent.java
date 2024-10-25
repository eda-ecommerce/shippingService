package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PackageDeliveredEvent extends DomainEvent<PackageDeliveredEvent.PackageDeliveredPayload> {
    public PackageDeliveredEvent(UUID eventKey, Shipment payload) {
        super(eventKey, new PackageDeliveredPayload(
                payload.getAPackage().getId(),
                payload.getId(),
                payload.getOrderId(),
                AddressDTO.fromEntity(payload.getDestination()),
                AddressDTO.fromEntity(payload.getOrigin())
        ));
    }

    public record PackageDeliveredPayload (
            UUID packageId,
            UUID shipmentId,
            UUID orderId,
            AddressDTO dest,
            AddressDTO origin
    ){}
}