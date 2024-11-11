package com.eda.shippingService.application.service;

import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.dto.outgoing.StockDTO;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.AvailableStockAdjusted;
import com.eda.shippingService.domain.events.OutOfStock;
import com.eda.shippingService.domain.events.StockCritical;
import com.eda.shippingService.infrastructure.repo.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
public class StockServiceImpl implements StockService {
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    @Value("kafka.topic.stock")
    private String stockTopic;

    @Autowired
    public StockServiceImpl(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Try reserving stock for a specific product
     * @param productID the product id that is affected
     * @param quantity the number of units to be reserved (positive values)
     * @throws NotEnoughStockException if there is not enough stock to fulfill the request. Maybe call someone at Purchasing
     */
    public void reserveStock(UUID productID, int quantity) throws NotEnoughStockException {
        if (quantity<=0) return;
        var product = productRepository.findById(productID).orElseThrow(() -> new NoSuchElementException("No product exists with id: "+productID));
            try {
                product.reserveStock(quantity);
                eventPublisher.publish(new AvailableStockAdjusted(StockDTO.fromProduct(product)), stockTopic);
                productRepository.save(product);
                if (product.isCritical()) publishStockCritical(product);
            } catch (NotEnoughStockException e){
                eventPublisher.publish(new OutOfStock(product), stockTopic);
                log.error("Not enough stock of product {} to fulfill request for {} units. Current available stock: {}", productID, quantity, product.getAvailableStock());
                throw e;
            }
    }

    /**
     * Releases reserved stock for a specific product
     * @param productID the product id that is affected
     * @param quantity the number of units to be freed up (positive values)
     */
    public void releaseStock(UUID productID, int quantity) {
        if (quantity<=0) return;
        var product = productRepository.findById(productID).orElseThrow(() -> new NoSuchElementException("No product exists with id: "+productID));
        product.releaseStock(quantity);
        eventPublisher.publish(new AvailableStockAdjusted(StockDTO.fromProduct(product)), stockTopic);
        productRepository.save(product);
    }

    /**
     * Adjust the value for physical stock in the warehouse
     * @param productID the product id that is affected
     * @param quantity takes both, negative and positive values
     */
    public void adjustStock(UUID productID, int quantity) {
        var product = productRepository.findById(productID).orElseThrow(() -> new NoSuchElementException("No product exists with id: "+productID));
        product.adjustStock(quantity);
        eventPublisher.publish(new AvailableStockAdjusted(StockDTO.fromProduct(product)), stockTopic);
        productRepository.save(product);
        if (product.isCritical()) publishStockCritical(product);
    }

    /**
     * Method for manually correcting stock info
     * @param productID id of affected Product
     * @param physicalStock the counted stock in the warehouse
     * @param reservedStock reserved stock if applicable
     */
    public void setStock(UUID productID, int physicalStock, int reservedStock) {
        var product = productRepository.findById(productID).orElseThrow(() -> new NoSuchElementException("No product exists with id: "+productID));
        product.setPhysicalStock(physicalStock);
        product.setReservedStock(reservedStock);
        eventPublisher.publish(new AvailableStockAdjusted(StockDTO.fromProduct(product)), stockTopic);
        productRepository.save(product);
    }

    /**
     * Adjust stock for multiple products at once
     * @param map contains mappings of UUIDS to physical stock changes as Integers. Takes negative and positive values
     */
    public void batchAdjustStock(Map<UUID, Integer> map) {
        for(UUID id: map.keySet()){
            adjustStock(id, map.get(id));
        }
    }

    private void publishStockCritical(Product product){
        eventPublisher.publish(new StockCritical(StockDTO.fromProduct(product)), stockTopic);
    }

}