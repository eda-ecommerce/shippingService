package com.eda.shippingService.adapters.web;

import com.eda.shippingService.application.service.*;
import com.eda.shippingService.domain.dto.incoming.RequestShipmentDTO;
import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/shipment")
public class ShippingController {
    private final ShipmentService shipmentService;

    @Autowired
    public ShippingController(
            ShipmentService shipmentService
    ) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/")
    public void requestShipment(@RequestBody RequestShipmentDTO shipmentDTO) {
        shipmentService.requestShipment(shipmentDTO);
    }

    @PostMapping("/status")
    public void setStatus(@RequestBody UpdateShipmentStatusDTO shipmentDTO) {
        shipmentService.externalShipmentStatusUpdate(shipmentDTO);
    }
    /*
    Routes:
    POST /shipment
    GET /shipment/{orderId}
    GET /shipments
    DELETE /shipment/{orderId}

    Event triggers:
    - createShipment (Command)
    - cancelShipment (Command)
    - processProductDelivery (Command)
    - shipmentDelivered (Event)
    - orderCreated (Event) --> StockCheck --> Shipment Requested/Impossible
    - orderConfirmed (Event) --> SendShipment()
     */


}
