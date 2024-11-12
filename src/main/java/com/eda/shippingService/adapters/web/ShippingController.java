package com.eda.shippingService.adapters.web;

import com.eda.shippingService.application.service.*;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/shipment")
public class ShippingController {
    private final ShipmentService shipmentService;

    @Autowired
    public ShippingController(
            ShipmentService shipmentService
    ) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<ShipmentDTO> requestShipment(@PathVariable UUID orderId, @RequestBody ShipmentContentsDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.requestShipment(orderId, shipmentDTO));
    }

    @PostMapping("/status")
    public ResponseEntity<ShipmentDTO> setStatus(@RequestBody UpdateShipmentStatusDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.externalShipmentStatusUpdate(shipmentDTO));
    }

    @PostMapping("/{orderId}/package")
    public ResponseEntity<ShipmentDTO> boxShipment(@RequestBody IncomingPackageDTO packageDTO, @PathVariable UUID orderId){
        var dto = shipmentService.boxShipment(orderId, packageDTO);
        return ResponseEntity.ok(dto);
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
