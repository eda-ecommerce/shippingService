package com.eda.shippingService.adapters.web;

import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/stock")
public class StockController {
    @PostMapping("/incomingDelivery")
    public void processIncomingDelivery(@RequestBody IncomingDeliveryDTO incomingDeliveryDTO) {

    }
}
