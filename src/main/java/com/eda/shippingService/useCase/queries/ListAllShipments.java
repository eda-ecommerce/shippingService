package com.eda.shippingService.useCase.queries;

import com.eda.shippingService.adapters.outgoing.repo.ShipmentRepository;

public class ListAllShipments {

    private ShipmentRepository shipmentRepository;

    public Object listAllShipments() {
        return shipmentRepository.findAll();
    }
}