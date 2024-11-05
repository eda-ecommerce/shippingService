package com.eda.shippingService.adapters;

import com.eda.shippingService.application.commandHandlers.*;
import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingController {
    private final CreateShipment createShipment;
    private final DeleteShipment deleteShipment;
    private final BoxShipment boxShipment;
    private final UpdateShipmentStatus updateShipmentStatus;
    private final ProcessIncomingDelivery processIncomingDelivery;
    private final SendShipment sendShipment;

    @Autowired
    public ShippingController(
            CreateShipment createShipment,
            DeleteShipment deleteShipment,
            BoxShipment boxShipment,
            UpdateShipmentStatus updateShipmentStatus,
            ProcessIncomingDelivery processIncomingDelivery,
            SendShipment sendShipment
    ) {
        this.createShipment = createShipment;
        this.deleteShipment = deleteShipment;
        this.boxShipment = boxShipment;
        this.updateShipmentStatus = updateShipmentStatus;
        this.processIncomingDelivery = processIncomingDelivery;
        this.sendShipment = sendShipment;
    }

    @PostMapping("/shipment")
    public void createShipment(@RequestBody CreateShipmentRequestDTO shipmentDTO) {
        createShipment.handle(shipmentDTO);
    }

    @PostMapping("/shipment/status")
    public void setStatus(@RequestBody UpdateShipmentStatusRequestDTO shipmentDTO) {
        updateShipmentStatus.handle(shipmentDTO);
    }
    /*
    Routes:
    POST /shipment
    GET /shipment/{shipmentId}
    GET /shipments
    DELETE /shipment/{shipmentId}

    Event triggers:
    - createShipment (Command)
    - cancelShipment (Command)
    - processProductDelivery (Command)
    - shipmentDelivered (Event)
    - orderCreated (Event) --> StockCheck --> Shipment Requested/Impossible
    - orderConfirmed (Event) --> SendShipment()
     */

    @PostMapping("/shipment/incomingDelivery")
    public void processIncomingDelivery(@RequestBody IncomingDeliveryDTO incomingDeliveryDTO) {
        processIncomingDelivery.handle(incomingDeliveryDTO);
    }


}
