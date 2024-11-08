package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.entity.APackage;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentBoxedEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

//@NoArgsConstructor(force = true)
@Component
public class BoxShipment {

    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public BoxShipment(ShipmentRepository shipmentRepository, EventPublisher eventPublisher) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(Shipment incomingShipment, APackage packageDetails) {

        Optional<Shipment> found = shipmentRepository.findById(incomingShipment.getOrderId());

        if (packageDetails != null) {
            if (found.isPresent()) {
                Shipment shipment = found.get();
                if (shipment.getStatus() == null) {
                    throw new IllegalArgumentException("Shipment has not been reserved yet");
                }
                else if (shipment.getStatus() == ShipmentStatus.RESERVED) {
                    shipment.addPackage(packageDetails);
                    eventPublisher.publish(new ShipmentBoxedEvent(UUID.randomUUID(), shipment), "shipment");
                    shipmentRepository.save(shipment);
                }
                else {
                    throw new IllegalArgumentException("Shipment has either been boxed, or sent for delivery.");
                }
            }
            else {
                throw new IllegalArgumentException("Shipment not found");
            }
        }
        else {
            throw new IllegalArgumentException("Package details not found");
        }
    }
}
