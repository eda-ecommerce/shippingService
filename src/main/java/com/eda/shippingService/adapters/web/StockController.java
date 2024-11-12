package com.eda.shippingService.adapters.web;

import com.eda.shippingService.application.service.StockService;
import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/stock")
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/incomingDelivery")
    public void processIncomingDelivery(@RequestBody IncomingDeliveryDTO incomingDeliveryDTO) {

    }
}
