package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AvailableStockAdjusted extends DomainEvent<AvailableStockAdjusted.StockAdjustedPayload> {
    public AvailableStockAdjusted(UUID eventKey, StockAdjustedPayload payload) {
        super(eventKey, payload);
    }
    @JsonSerialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StockAdjustedPayload(
            @NotNull
            UUID productId,
            @NotNull
            Number actualStock,
            @NotNull
            Number reservedStock,
            @NotNull
            Number availableStock
    ) {}
}
