package com.eda.shippingService.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
public record OrderLineItem(
        UUID productId,
        Integer quantity){}

