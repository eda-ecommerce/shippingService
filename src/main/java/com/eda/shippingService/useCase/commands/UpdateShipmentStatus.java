package com.eda.shippingService.useCase.commands;

import com.eda.shippingService.adapters.incoming.web.UpdateShipmentStatusRequestDTO;
import com.eda.shippingService.adapters.outgoing.eventing.EventPublisher;
import com.eda.shippingService.adapters.outgoing.eventing.PackageDeliveredEvent;
import com.eda.shippingService.adapters.outgoing.repo.ShipmentRepository;
import com.eda.shippingService.model.dto.ShipmentDTO;
import com.eda.shippingService.model.entity.Shipment;
import com.eda.shippingService.model.entity.ShipmentStatus;
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
                    //TODO eventPublisher.publish();
                    shipmentRepository.save(shipment);
                }
                case DELIVERED -> {
                    shipment.delivered();
                    eventPublisher.publish(new PackageDeliveredEvent(UUID.randomUUID(), shipment));
                    shipmentRepository.save(shipment);
                }
                case RETURNED -> {
                    //TODO
                }
                default -> {
                    throw new IllegalArgumentException("Invalid shipment status");
                }
            }
            shipmentRepository.save(shipment);
        }
        return ShipmentDTO.fromEntity(found.get());
    }

}
