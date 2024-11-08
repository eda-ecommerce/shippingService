package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class StockCriticalEvent extends DomainEvent<StockCriticalEvent.StockCriticalPayload> {
    public StockCriticalEvent(UUID eventKey, Product payload) {
        super(eventKey, new StockCriticalPayload(
                payload.getId(),
                payload.getStock(),
                payload.getReservedStock(),
                payload.getAvailableStock()
        ));
    }
    public record StockCriticalPayload(
            UUID productId,
            Number actualStock,
            Number reservedStock,
            Number availableStock
    ){}
}
