package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.StockDTO;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class StockCritical extends DomainEvent<StockDTO> {
    public StockCritical(StockDTO payload) {
        super(null, payload);
    }
}
