package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.application.commandHandlers.CreateShipment;
import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.dto.outgoing.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.ShipmentRequestedEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component
@Slf4j
public class OrderConfirmedEventHandler implements EventHandler<OrderConfirmedEvent> {
    private final IdempotentConsumerRepository idempotentConsumerRepository;
    private final CreateShipment createShipmentHandler;
    private final EventPublisher eventPublisher;

    @Autowired
    public OrderConfirmedEventHandler(IdempotentConsumerRepository idempotentConsumerRepository, CreateShipment createShipmentHandler, EventPublisher eventPublisher) {
        this.idempotentConsumerRepository = idempotentConsumerRepository;
        this.createShipmentHandler = createShipmentHandler;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(OrderConfirmedEvent event) {
        log.info("Handling OrderConfirmedEvent with ID: {}", event.getMessageId());
        if (idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName()).isPresent()) {
            log.info("OrderConfirmedEvent with ID: {} already processed", event.getMessageId());
            return;
        }
        // Transforming this from event to DTO seems counter intuitive, should the handler support taking events directly?
        Shipment created = createShipmentHandler.handle(new CreateShipmentRequestDTO(
                event.getPayload().orderId(),
                event.getPayload().customerId(),
                null,null,
                event.getPayload().products().stream().map(product -> new OrderLineItemDTO(product.productId(), product.quantity())).toList()));
        idempotentConsumerRepository.save(new ProcessedMessage(event.getMessageId(), this.getClass().getSimpleName()));

        ShipmentDTO shipmentDTO = ShipmentDTO.fromEntity(created);
        eventPublisher.publish(new ShipmentRequestedEvent (null, shipmentDTO), "shipment");
    }
}
