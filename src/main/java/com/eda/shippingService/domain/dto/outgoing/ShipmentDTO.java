package com.eda.shippingService.domain.dto.outgoing;
import com.eda.shippingService.domain.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ShipmentDTO(
        UUID orderId,
        AddressDTO destination,
        AddressDTO origin,
        PackageDTO aPackage,
        List<OrderLineItemDTO> requestedProducts,
        ShipmentStatus status
) {
    public Shipment toEntity(){
        return new Shipment(orderId,
                destination.toEntity(),
                origin.toEntity(),
                aPackage != null ? aPackage.toEntity() : null,
                this.requestedProducts().stream()
                        .map(OrderLineItemDTO::toEntity)
                        .toList(),
                status
        );
   }

   public static ShipmentDTO fromEntity(Shipment shipment){
        return new ShipmentDTO(
                shipment.getOrderId(),
                AddressDTO.fromEntity(shipment.getDestination()),
                AddressDTO.fromEntity(shipment.getOrigin()),
                shipment.getAPackage() != null ?PackageDTO.fromEntity(shipment.getAPackage()) : null,
                shipment.getRequestedProducts().stream()
                        .map(OrderLineItemDTO::fromEntity).toList(),
                shipment.getStatus()
        );
   }
}