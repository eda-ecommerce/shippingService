package com.eda.shippingService.useCase;

import com.eda.shippingService.repo.ShipmentRepository;

public class ListAllShipments {

    private ShipmentRepository shipmentRepository;

    public Object listAllShipments() {
        return shipmentRepository.findAll();
    }
}