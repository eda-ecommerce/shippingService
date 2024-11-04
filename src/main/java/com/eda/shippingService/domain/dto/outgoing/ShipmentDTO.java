package com.eda.shippingService.domain.dto.outgoing;
import com.eda.shippingService.domain.entity.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ShipmentDTO(
        @JsonProperty("orderId")
        UUID orderId,
        @JsonProperty("destination")
        AddressDTO destination,
        @JsonProperty("origin")
        AddressDTO origin,
        @JsonProperty("package")
        PackageDTO aPackage,
        @JsonProperty("requestedProducts")
        List<OrderLineItemDTO> requestedProducts,
        @JsonProperty("status")
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