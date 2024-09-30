package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderConfirmedEvent extends DomainEvent<OrderConfirmedEvent.OrderConfirmedPayload> {

    public OrderConfirmedEvent(OrderConfirmedPayload payload) {
        super(null, payload);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderConfirmedPayload(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("totalPrice") Integer totalPrice,
            @JsonProperty("products") OrderLineItem[] requestedProducts){
    }
}
