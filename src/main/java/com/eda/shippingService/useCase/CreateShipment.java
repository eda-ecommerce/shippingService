package com.eda.shippingService.useCase;

import com.eda.shippingService.model.entity.Shipment;
import com.eda.shippingService.repo.ShipmentRepository;

public class CreateShipment {

    private ShipmentRepository shipmentRepository;

    public Object createShipment(Shipment shipment) {

        if (shipment.getId() != null && shipmentRepository.findById(shipment.getId()).isPresent()) {
            throw new IllegalArgumentException(String.format("Shipment with Shipment ID %s already exists.", shipment.getId()));
        }

        if (shipment.getOrderId() != null && shipmentRepository.findByOrderId(shipment.getOrderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipment.getOrderId()));
        }

        return shipmentRepository.save(shipment);
    }
}
