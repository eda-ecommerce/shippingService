package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.StockReservedEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReserveStock {
    private ProductRepository productRepository;
    private EventPublisher eventPublisher;

    // returning Boolean just in case we need it in the future
    public Boolean handle(Shipment shipment) {

        if (shipment.checkContents()) {
            HashMap<UUID, Integer> expected = new HashMap<>();
            for (OrderLineItem orderLineItem : shipment.getRequestedProducts()){
                expected.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
            }

            for (UUID productId : expected.keySet()){
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()){
                    Product found = product.get();
                    found.reserveStock(expected.get(productId));
                    if (found.isProductInStock()) {
                        eventPublisher.publish(new StockReservedEvent(UUID.randomUUID(), found));
                        productRepository.save(found);
                        return true;
                    }
                    else {
                        found.unreserveStock(expected.get(productId));
                        throw new IllegalArgumentException(String.format("The Product's stock is %s. Cannot reserve %s.", found.getStock(), expected.get(productId)));

                    }
                }
                else {
                    throw new IllegalArgumentException(String.format("Product with ID %s not found.", productId));
                }
            }
        }
        else {
            throw new IllegalArgumentException("Shipment does not have any products.");
        }
        return false;
    }
}
