package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderConfirmed;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedEventHandler implements EventHandler<OrderConfirmed> {

    private final ShipmentService shipmentService;
    private final IdempotentConsumerRepository idempotentConsumerRepository;

    @Autowired
    public OrderConfirmedEventHandler(ShipmentService shipmentService, IdempotentConsumerRepository idempotentConsumerRepository) {
        this.shipmentService = shipmentService;
        this.idempotentConsumerRepository = idempotentConsumerRepository;
    }

    @Override
    @Transactional
    public void handle(OrderConfirmed event) {
        var found = idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName());
        if (found.isPresent()) {
            return;
        }
        var payload = event.getPayload();
        shipmentService.approveShipment(payload.orderId());
        idempotentConsumerRepository.save(new ProcessedMessage(event.getMessageId(), this.getClass().getSimpleName()));
    }
}
