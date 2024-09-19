package com.eda.shippingService.useCase.commands;

import com.eda.shippingService.adapters.incoming.web.CreateShipmentRequestDTO;
import com.eda.shippingService.model.entity.Shipment;
import com.eda.shippingService.adapters.outgoing.repo.ShipmentRepository;
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
