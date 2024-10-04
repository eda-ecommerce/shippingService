package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

// let's fire this event even if there's no listener
public class StockUnreservedEvent extends DomainEvent<StockUnreservedEvent.StockUnreservedPayload> {
    public StockUnreservedEvent(UUID eventKey, Product payload) {
        super(eventKey, new StockUnreservedPayload(
                payload.getId(),
                payload.getStock(),
                payload.getReservedStock()
        ));
    }

    public record StockUnreservedPayload(
            UUID productId,
            Number newStock,
            Number newReservedStock
    ) {}
}
