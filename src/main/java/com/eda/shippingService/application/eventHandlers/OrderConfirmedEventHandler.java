package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedEventHandler implements EventHandler<OrderConfirmedEvent> {

    private final ShipmentRepository shipmentRepository;
    private final IdempotentConsumerRepository idempotentConsumerRepository;

    @Autowired
    public OrderConfirmedEventHandler(ShipmentRepository shipmentRepository, IdempotentConsumerRepository idempotentConsumerRepository) {
        this.shipmentRepository = shipmentRepository;
        this.idempotentConsumerRepository = idempotentConsumerRepository;
    }

    @Override
    @Transactional
    public void handle(OrderConfirmedEvent event) {
        var found = idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName());
        if (found.isPresent()) {
            return;
        }
        var id = event.getPayload().orderId();
        var shipment = shipmentRepository.findById(id);
        if (shipment.isPresent()){
            shipment.get().approve();
            shipmentRepository.save(shipment.get());
        }
        else {
            //TODO: either handle like creation, or throw exception
            throw new RuntimeException("Shipment not found");
        }
        idempotentConsumerRepository.save(new ProcessedMessage(event.getMessageId(), this.getClass().getSimpleName()));
    }
}
