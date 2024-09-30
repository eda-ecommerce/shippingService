package com.eda.shippingService.domain.dto.outgoing;

import com.eda.shippingService.domain.entity.OrderLineItem;

import java.util.List;
import java.util.UUID;

public record PackageDTO(
        UUID id,
        UUID trackingNumber,
        List<OrderLineItem> contents
) {}
