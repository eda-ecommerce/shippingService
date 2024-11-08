package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//The Problem with this Handler / Use case pattern:
//Every time I call this from a different location, this has to be adjusted to process different ways of calling handle.
//OR: I conform to the CreateShipmentRequestDTO and adjust the calling code to fit this pattern.
public class CreateShipment {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public CreateShipment(ShipmentRepository shipmentRepository, ReserveStock reserveStock) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment handle(CreateShipmentRequestDTO shipmentDTO) {
        if (shipmentDTO.orderId() != null && shipmentRepository.findByOrderId(shipmentDTO.orderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipmentDTO.orderId()));
        }
        Shipment shipmentEntity = shipmentDTO.toEntity();
        return shipmentRepository.save(shipmentEntity);
    }
}
