package com.eda.shippingService.domain.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum ShipmentStatus {
    REQUESTED,
    PACKAGED,
    SHIPPED,
    IN_DELIVERY,
    DELIVERED,
    RETURNED
}
