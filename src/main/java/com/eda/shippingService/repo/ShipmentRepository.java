package com.eda.shippingService.repo;

import com.eda.shippingService.model.entity.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment, UUID> {
    Shipment findByOrderId(UUID orderId);
}
