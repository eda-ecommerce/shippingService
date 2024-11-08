package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusRequestDTO;
import com.eda.shippingService.domain.events.PackageSentEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.domain.events.ShipmentDelivered;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UpdateShipmentStatus {
    private ShipmentRepository shipmentRepository;
    private EventPublisher eventPublisher;

    //TODO
    public ShipmentDTO handle(UpdateShipmentStatusRequestDTO request) {
        Optional<Shipment> found = shipmentRepository.findById(request.shipmentId());
        if (found.isPresent()) {
            Shipment shipment = found.get();
            switch (request.shipmentStatus()){
                case IN_DELIVERY -> {
                    shipment.setStatus(ShipmentStatus.IN_DELIVERY);
                    eventPublisher.publish(new PackageSentEvent(shipment.getAPackage()), "package");
                    shipmentRepository.save(shipment);
                }
                case DELIVERED -> {
                    shipment.delivered();
                    eventPublisher.publish(new ShipmentDelivered(UUID.randomUUID(), shipment), "package");
                    shipmentRepository.save(shipment);
                }
                case RETURNED -> {
                    //TODO
                }
                default -> throw new IllegalArgumentException("Invalid shipment status");
            }
            shipmentRepository.save(shipment);
        }
        return ShipmentDTO.fromEntity(found.get());
    }

}
