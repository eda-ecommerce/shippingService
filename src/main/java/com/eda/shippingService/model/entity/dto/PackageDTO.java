package com.eda.shippingService.model.entity.dto;

import com.eda.shippingService.model.entity.OrderLineItem;

import java.util.List;
import java.util.UUID;

public record PackageDTO(
        UUID id,
        UUID trackingNumber,
        List<OrderLineItem> contents
) {}
