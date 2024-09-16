package com.eda.shippingService.model.entity.dto;
import com.eda.shippingService.model.entity.APackage;
import com.eda.shippingService.model.entity.Address;
import com.eda.shippingService.model.entity.OrderLineItem;

import java.util.List;
import java.util.UUID;

public record ShipmentDTO(
        UUID id,
        AddressDTO destination,
        AddressDTO origin,
        UUID orderId, APackage aPackage,
        List<OrderLineItem> requestedProducts
) {}