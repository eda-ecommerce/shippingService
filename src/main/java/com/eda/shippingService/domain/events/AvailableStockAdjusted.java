package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class AvailableStockAdjusted extends DomainEvent<AvailableStockAdjusted.StockAdjustedPayload> {
    public AvailableStockAdjusted(UUID eventKey, Product payload) {
        super(eventKey, new StockAdjustedPayload(
                payload.getId(),
                payload.getStock(),
                payload.getReservedStock(),
                payload.getStock().doubleValue() - payload.getReservedStock().doubleValue()
        ));
    }

    public record StockAdjustedPayload(
            UUID productId,
            Number actualStock,
            Number reservedStock,
            Number availableStock
    ) {}
}
