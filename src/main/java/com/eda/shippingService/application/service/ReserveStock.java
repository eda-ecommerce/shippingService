package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.AvailableStockAdjusted;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReserveStock {
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public ReserveStock(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(HashMap<UUID, Integer> expected) {
        for (UUID productId : expected.keySet()) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                Product found = product.get();
                Integer expectedAmount = expected.get(productId);
                if (found.isQuantityAvailable(expectedAmount)) {
                    found.reserveStock(expectedAmount);
                    productRepository.save(found);
                    eventPublisher.publish(new AvailableStockAdjusted(UUID.randomUUID(), new AvailableStockAdjusted.StockAdjustedPayload(
                            found.getId(), found.getStock(), found.getReservedStock(), found.getAvailableStock()
                    )), "stock");
                } else {
                    throw new IllegalArgumentException(String.format("The Product's stock is %s. Cannot reserve %s.", found.getStock(), expectedAmount));
                }
            } else {
                throw new IllegalArgumentException(String.format("Product with ID %s not found.", productId));
            }
        }
    }
}
