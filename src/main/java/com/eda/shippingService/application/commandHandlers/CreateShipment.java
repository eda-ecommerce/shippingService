package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateShipment {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public CreateShipment(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment handle(CreateShipmentRequestDTO shipment) {
        if (shipment.orderId() != null && shipmentRepository.findByOrderId(shipment.orderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipment.orderId()));
        }

        return shipmentRepository.save(shipment.toEntity());
    }
}
