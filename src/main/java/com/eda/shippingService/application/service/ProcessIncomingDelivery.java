package com.eda.shippingService.application.service;

import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
@Component
public class ProcessIncomingDelivery {

    private ModifyStockLevel modifyStockLevel;

    public void handle(IncomingDeliveryDTO incomingDelivery) {

        HashMap<UUID, Integer> expected = new HashMap<>();
        for (OrderLineItem orderLineItem : incomingDelivery.contents()){
            expected.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
        }

        modifyStockLevel.increaseStockLevel(expected);

    }
}
