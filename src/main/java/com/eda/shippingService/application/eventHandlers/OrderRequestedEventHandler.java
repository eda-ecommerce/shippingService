package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.application.service.StockServiceImpl;
import com.eda.shippingService.domain.dto.incoming.RequestShipmentDTO;
import com.eda.shippingService.domain.dto.outgoing.OrderLineItemDTO;
import com.eda.shippingService.domain.events.OrderRequested;
import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderRequestedEventHandler implements EventHandler<OrderRequested> {
    private final IdempotentConsumerRepository idempotentConsumerRepository;
    private final ShipmentService shipmentService;

    @Autowired
    public OrderRequestedEventHandler(IdempotentConsumerRepository idempotentConsumerRepository, ShipmentService shipmentService) {
        this.idempotentConsumerRepository = idempotentConsumerRepository;
        this.shipmentService = shipmentService;
    }

    @Transactional
    public void handle(OrderRequested event) {
        log.info("Handling OrderConfirmedEvent with ID: {}", event.getMessageId());
        if (idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName()).isPresent()) {
            log.info("OrderConfirmedEvent with ID: {} already processed", event.getMessageId());
            return;
        }
        // Transforming this from event to DTO seems counterintuitive,
        // should the handler support taking events directly?
        var created = shipmentService.requestShipment(new RequestShipmentDTO(
                event.getPayload().orderId(),
                event.getPayload().customerId(),
                //TODO Where do the addresses come from? --> PrepareShipment, but via controller?
                null,null,
                event.getPayload().products().stream().map(product -> new OrderLineItemDTO(product.productId(), product.quantity())).toList()));
        var requestedProducts = created.getRequestedProducts();

    }
}
