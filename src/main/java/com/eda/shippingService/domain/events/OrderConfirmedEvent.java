package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderConfirmedEvent extends DomainEvent{

    public OrderConfirmedEvent(UUID eventKey, OrderConfirmedPayload payload) {
        super(eventKey, payload);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderConfirmedPayload implements DomainEventPayload {
        @JsonProperty("orderId")
        private final UUID orderId;
        @JsonProperty("customerId")
        private final UUID customerId;
        @JsonProperty("totalPrice")
        private final Integer totalPrice;
        @JsonProperty("products")
        private final OrderLineItem[] requestedProducts;

        OrderConfirmedPayload(UUID orderId, UUID customerId, Integer totalPrice, OrderLineItem[] requestedProducts) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.totalPrice = totalPrice;
            this.requestedProducts = requestedProducts;
        }
    }


}
