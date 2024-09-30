package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.entity.ShipmentStatus;

import java.util.UUID;

public record UpdateShipmentStatusRequestDTO(
        UUID shipmentId,
        ShipmentStatus shipmentStatus
) {
}
