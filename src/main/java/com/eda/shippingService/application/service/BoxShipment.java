package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.entity.APackage;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentBoxed;
import com.eda.shippingService.domain.entity.*;
import com.eda.shippingService.domain.events.AvailableStockAdjusted;
import com.eda.shippingService.domain.events.StockCriticalEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import com.eda.shippingService.infrastructure.repo.ProductRepository;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

//@NoArgsConstructor(force = true)
@Component
public class BoxShipment {

    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;
    private final ProductRepository productRepository;

    @Autowired
    public BoxShipment(
            ShipmentRepository shipmentRepository,
            EventPublisher eventPublisher,
            ProductRepository productRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
        this.productRepository = productRepository;
    }

    public void handle(Shipment incomingShipment, APackage packageDetails) {

        if (packageDetails == null) {
            throw new IllegalArgumentException("Package details not found.");
        }
        else {
            Optional<Shipment> found = shipmentRepository.findById(incomingShipment.getOrderId());
            if (found.isPresent()) {
                Shipment shipment = found.get();
                ShipmentStatus shipmentStatus = shipment.getStatus();
                if (shipmentStatus != ShipmentStatus.RESERVED) {
                    throw new IllegalArgumentException("Shipment status is " + shipmentStatus + " and not RESERVED.");
                }
                else {
                    HashMap<UUID, Integer> expected = shipment.getRequestedProductsAsHashMap();
                    for (UUID productId : expected.keySet()){
                        Optional<Product> product = productRepository.findById(productId);
                        if (product.isPresent()){
                            Product foundProduct = product.get();
                            double reservedStock = foundProduct.getReservedStock().doubleValue();
                            if (reservedStock > 0){ // maybe unnecessary check? remove if not needed.
                                foundProduct.unreserveStock(reservedStock);
                                foundProduct.reduceStock(reservedStock);
                                eventPublisher.publish(new AvailableStockAdjusted(UUID.randomUUID(),
                                        new AvailableStockAdjusted.StockAdjustedPayload(
                                                foundProduct.getId(),
                                                foundProduct.getStock(),
                                                foundProduct.getReservedStock(),
                                                foundProduct.getAvailableStock())),"stock");
                                productRepository.save(foundProduct);
                                if (foundProduct.isCritical()){
                                    eventPublisher.publish(new StockCriticalEvent(UUID.randomUUID(), foundProduct),"stock");
                                }
                            }
                            else {
                                throw new IllegalArgumentException(String.format("The reserved stock level is %s, so it is not possible to un-reserve it.", reservedStock));
                            }
                        }
                        else {
                            throw new IllegalArgumentException(String.format("Product with ID %s not found.", productId));
                        }
                    }
                    shipment.addPackage(packageDetails);
                    eventPublisher.publish(new ShipmentBoxed(UUID.randomUUID(), shipment), "shipment");
                    shipmentRepository.save(shipment);
                }
            }
            else {
                throw new IllegalArgumentException("Shipment not found.");
            }
        }
    }
}
