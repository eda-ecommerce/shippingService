package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.eda.shippingService.domain.events.common.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import elemental.json.JsonString;
import elemental.json.impl.JreJsonString;
import lombok.Getter;
import org.springframework.lang.Nullable;

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