package com.eda.shippingService.domain.dto.outgoing;

import com.eda.shippingService.domain.entity.OrderLineItem;

import java.util.UUID;

public record OrderLineItemDTO(
        UUID productId,
        int quantity
) {
    public OrderLineItem toEntity(){
        return new OrderLineItem(
                this.productId(),
                this.quantity()
        );
    }
    public static OrderLineItemDTO fromEntity(OrderLineItem orderLineItem){
        return new OrderLineItemDTO(
                orderLineItem.getProductId(),
                orderLineItem.getQuantity()
        );
    }
}
