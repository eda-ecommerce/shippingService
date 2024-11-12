package com.eda.shippingService.application.service;

import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.entity.OrderLineItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StockService {
    void reserveStock(UUID productID, int quantity) throws NotEnoughStockException;
    void releaseStock(UUID productID, int quantity);
    void adjustStock(UUID productID, int quantity);
    void setStock(UUID productID, int actualStock, int reservedStock);
    void batchAdjustStock(Map<UUID, Integer> hashMap);
    void batchAdjustStock(List<OrderLineItem> orderLineItems);
}
