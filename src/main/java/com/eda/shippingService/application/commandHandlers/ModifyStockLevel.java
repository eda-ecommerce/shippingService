package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.StockDecreasedEvent;
import com.eda.shippingService.domain.events.StockIncreasedEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class ModifyStockLevel {
    private EventPublisher eventPublisher;
    private ProductRepository productRepository;

    public void increaseStockLevel(HashMap<UUID, Integer> expected) {
        for (UUID productId : expected.keySet()){
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()){
                Product found = product.get();
                found.increaseStock(expected.get(productId));
                eventPublisher.publish(new StockIncreasedEvent(UUID.randomUUID(), found), "stock");
                productRepository.save(found);
            }
        }
    }

    public void decreaseStockLevel(Shipment shipment) {
        // unnecessary check IMO, remove if not needed
        if (shipment.checkContents()) {
            HashMap<UUID, Integer> expected = new HashMap<>();
            for (OrderLineItem orderLineItem : shipment.getRequestedProducts()){
                expected.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
            }

            for (UUID productId : expected.keySet()){
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()){
                    Product found = product.get();
                    found.reduceStock(expected.get(productId));
                    eventPublisher.publish(new StockDecreasedEvent(UUID.randomUUID(), found), "stock");
                    productRepository.save(found);
                }
            }
        }
    }

    // WIP
    // TODO: maybe replace the redundant increaseStockLevel and decreaseStockLevel methods with this?
    public void modifyStockLevel(HashMap<UUID, Integer> expected, Boolean increaseStock) {
        for (UUID productId : expected.keySet()){
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()){
                Product found = product.get();
                if (increaseStock) {
                    found.increaseStock(expected.get(productId));
                    eventPublisher.publish(new StockIncreasedEvent(UUID.randomUUID(), found), "stock");
                } else {
                    found.reduceStock(expected.get(productId));
                    eventPublisher.publish(new StockDecreasedEvent(UUID.randomUUID(), found), "stock");
                }
                productRepository.save(found);
            }
        }
    }

}
