package com.eda.shippingService.adapters.incoming.web;

import com.eda.shippingService.model.entity.ShipmentStatus;

import java.util.UUID;

public record UpdateShipmentStatusRequestDTO(
        UUID shipmentId,
        ShipmentStatus shipmentStatus
) {
}
