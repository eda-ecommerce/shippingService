package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateShipment {

    private final ShipmentRepository shipmentRepository;
    private final ReserveStock reserveStock;

    @Autowired
    public CreateShipment(ShipmentRepository shipmentRepository, ReserveStock reserveStock) {
        this.shipmentRepository = shipmentRepository;
        this.reserveStock = reserveStock;
    }

    public Shipment handle(CreateShipmentRequestDTO shipment) {
        if (shipment.orderId() != null && shipmentRepository.findByOrderId(shipment.orderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipment.orderId()));
        }

        Shipment shipmentEntity = shipment.toEntity();

        if (shipmentEntity.getStatus() == ShipmentStatus.REQUESTED) {
            // instead of directly decreasing the stock here, we are just reserving the necessary stock amount
            // the stock will be decreased only when the shipment is boxed.
            reserveStock.handle(shipmentEntity);
        }

        return shipmentRepository.save(shipmentEntity);
    }
}
