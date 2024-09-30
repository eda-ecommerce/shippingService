package com.eda.shippingService.domain.dto.outgoing;
import com.eda.shippingService.domain.entity.*;

import java.util.List;
import java.util.UUID;

public record ShipmentDTO(
        UUID id,
        Address destination,
        Address origin,
        UUID orderId, APackage aPackage,
        List<OrderLineItem> requestedProducts,
        ShipmentStatus status
) {
    public Shipment toEntity(){
        return new Shipment(
                destination,origin,orderId,aPackage,requestedProducts, status
        );
   }

   public static ShipmentDTO fromEntity(Shipment shipment){
        return new ShipmentDTO(
                shipment.getId(),
                shipment.getDestination(),
                shipment.getOrigin(),
                shipment.getOrderId(),
                shipment.getAPackage(),
                shipment.getRequestedProducts(),
                shipment.getStatus()
        );
   }
}