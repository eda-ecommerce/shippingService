package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class StockDecreasedEvent extends DomainEvent<StockDecreasedEvent.StockDecreasedPayload> {
    public StockDecreasedEvent(UUID eventKey, Product payload) {
        super(eventKey, new StockDecreasedPayload(
                payload.getId(),
                payload.getStock()
        ));
    }

    public record StockDecreasedPayload(
            UUID productId,
            Number newStock
    ) {}
}
