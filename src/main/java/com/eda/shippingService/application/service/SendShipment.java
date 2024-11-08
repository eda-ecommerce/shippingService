package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentSent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

//@NoArgsConstructor(force = true)
@Component
public class SendShipment {

    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;

    public SendShipment(ShipmentRepository shipmentRepository, EventPublisher eventPublisher) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(Shipment incomingShipment) {

        Optional<Shipment> found = shipmentRepository.findById(incomingShipment.getOrderId());

        if (found.isPresent()) {
            Shipment shipment = found.get();
            ShipmentStatus shipmentStatus = shipment.getStatus();
            if (shipmentStatus == ShipmentStatus.PACKAGED) {
                shipment.send();
                eventPublisher.publish(new ShipmentSent(UUID.randomUUID(), shipment), "shipment");
                shipmentRepository.save(shipment);
            }
            else {
                throw new IllegalArgumentException("Shipment is not ready to be sent. The current shipment status is " + shipmentStatus);
            }
        }
        else {
            throw new IllegalArgumentException("Shipment not found");
        }
    }
}
