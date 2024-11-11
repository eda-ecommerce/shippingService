package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.StockDTO;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AvailableStockAdjusted extends DomainEvent<StockDTO> {
    public AvailableStockAdjusted( StockDTO payload) {
        super(null, payload);
    }
}
