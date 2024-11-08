package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.dto.incoming.RequestShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.ShipmentRequested;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;
    private final StockService stockService;

    public ShipmentService(ShipmentRepository shipmentRepository, EventPublisher eventPublisher, StockService stockService) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
        this.stockService = stockService;
    }

    public Shipment requestShipment(RequestShipmentDTO shipmentDTO){
        if (shipmentDTO.orderId() != null && shipmentRepository.findByOrderId(shipmentDTO.orderId()) != null) {
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipmentDTO.orderId()));
        }
        Shipment shipmentEntity = shipmentDTO.toEntity();
        for (var item: shipmentEntity.getRequestedProducts()) {
            stockService.reserveStock(item.productId, item.quantity);
        }
        eventPublisher.publish(new ShipmentRequested());
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
