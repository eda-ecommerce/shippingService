package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(CreateShipmentRequestDTO shipmentDTO){
        if (shipmentDTO.orderId() != null && shipmentRepository.findByOrderId(shipmentDTO.orderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipmentDTO.orderId()));
        }
        Shipment shipmentEntity = shipmentDTO.toEntity();
        return shipmentRepository.save(shipmentEntity);
    }

    public void deleteShipment(){

    }

    public void boxShipment(){

    }

    public void sendShipment(){

    }

    public void externalShipmentStatusUpdate(){

    }
}
