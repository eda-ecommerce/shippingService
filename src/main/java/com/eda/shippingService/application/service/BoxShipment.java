package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.entity.*;
import com.eda.shippingService.domain.events.AvailableStockAdjusted;
import com.eda.shippingService.domain.events.ShipmentBoxedEvent;
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
    public BoxShipment(ShipmentRepository shipmentRepository, EventPublisher eventPublisher, ProductRepository productRepository) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
        this.productRepository = productRepository;
    }

    public void handle(Shipment incomingShipment, APackage packageDetails) {

        Optional<Shipment> found = shipmentRepository.findById(incomingShipment.getOrderId());

        if (packageDetails != null) {
            if (found.isPresent()) {
                Shipment shipment = found.get();
                if (shipment.getStatus() == null) {
                    throw new IllegalArgumentException("Shipment has not been reserved yet");
                }
                else if (shipment.getStatus() == ShipmentStatus.RESERVED) {
                    // TODO: unreserve the stock and send out an event in case of critical stock levels
                    HashMap<UUID, Integer> expected = new HashMap<>();
                    for (OrderLineItem orderLineItem : shipment.getRequestedProducts()){
                        expected.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
                    }

                    // if this flag is true after un-reserving all the products, pack the shipment
                    boolean stockAdjusted = true;

                    for (UUID productId : expected.keySet()){
                        Optional<Product> product = productRepository.findById(productId);
                        if (product.isPresent()){
                            Product foundProduct = product.get();
                            double reservedStock = foundProduct.getReservedStock().doubleValue();
                            if (reservedStock > 0){
                                foundProduct.unreserveStock(reservedStock);
                                foundProduct.reduceStock(reservedStock);
                                eventPublisher.publish(new AvailableStockAdjusted(UUID.randomUUID(), foundProduct),"stock");
                                productRepository.save(foundProduct);
                            }
                            else {
                                stockAdjusted = false;
                                throw new IllegalArgumentException(String.format("The reserved stock level is %s, so it is not possible to un-reserve it.", reservedStock));
                            }
                        }
                        else {
                            throw new IllegalArgumentException(String.format("Product with ID %s not found.", productId));
                        }
                    }
                    shipment.addPackage(packageDetails);
                    eventPublisher.publish(new ShipmentBoxedEvent(UUID.randomUUID(), shipment), "shipment");
                    shipmentRepository.save(shipment);
                }
                else {
                    throw new IllegalArgumentException("Shipment has either been boxed, or sent for delivery.");
                }
            }
            else {
                throw new IllegalArgumentException("Shipment not found");
            }
        }
        else {
            throw new IllegalArgumentException("Package details not found");
        }
    }
}
