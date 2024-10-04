package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class StockIncreasedEvent extends DomainEvent<StockIncreasedEvent.StockIncreasedPayload> {
    public StockIncreasedEvent(UUID eventKey, Product payload) {
        super(eventKey, new StockIncreasedPayload(
                payload.getId(),
                payload.getStock()
        ));
    }
    
    public record StockIncreasedPayload(
            UUID productId,
            Number newStock
    ) {}
}
