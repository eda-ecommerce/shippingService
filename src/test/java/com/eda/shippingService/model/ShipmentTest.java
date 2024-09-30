package com.eda.shippingService.model;

import com.eda.shippingService.domain.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ShipmentTest {
    private final UUID productId = UUID.fromString("1a0000-0000-0000-0000-000000000000");
    private final UUID shipmentId = UUID.fromString("1b0000-0000-0000-0000-000000000000");
    private final OrderLineItem requested = new OrderLineItem(productId, 10);
    private final Address dest = new Address("Hammerhausen 24", "Valhalla", "IL", "00001", "DE");
    private final Address origin = new Address("123 Burgstraße", "Kassel", "IL", "51428", "DE");
    Shipment emptyShipment;
    @BeforeEach
    public void setup() {
        emptyShipment = new Shipment(
                dest,
                origin,
                shipmentId,
                null,
                List.of(requested),
                ShipmentStatus.REQUESTED
        );
    }

    @Test
    public void shouldValidateContents() {
        //given
        OrderLineItem contents = new OrderLineItem(productId, 10);
        APackage aPackage = new APackage(10f, 10f, 10f, 100f, List.of(contents));
        Shipment shipment = new Shipment(
                dest,
                origin,
                shipmentId,
                aPackage,
                List.of(requested),
                ShipmentStatus.REQUESTED
        );
        Assertions.assertTrue(shipment.checkContents());
    }
    @Test
    public void shouldInvalidateContents() {
        //given
        OrderLineItem contents = new OrderLineItem(productId, 5); // Mismatched quantity
        APackage aPackage = new APackage(10f, 10f, 10f, 100f, List.of(contents));
        Shipment shipment = new Shipment(
                dest,
                origin,
                shipmentId,
                aPackage,
                List.of(requested),
                ShipmentStatus.REQUESTED
        );
        Assertions.assertFalse(shipment.checkContents());
    }

    @Test
    public void shouldAddPackage(){
        emptyShipment.addPackage(
                new APackage(10f, 10f, 10f, 100f, List.of(requested))
        );
        Assertions.assertNotNull(emptyShipment.getAPackage());
    }


    @Test
    void shouldAssignTrackingNumber() {
        OrderLineItem contents = new OrderLineItem(productId, 10);
        APackage aPackage = new APackage(10f, 10f, 10f, 100f, List.of(contents));
        Shipment shipment = new Shipment(
                dest,
                origin,
                shipmentId,
                aPackage,
                List.of(requested),
                ShipmentStatus.REQUESTED
        );
        shipment.assignTrackingNumber(UUID.fromString("1f0000-0000-0000-0000-000000000000"));
        Assertions.assertNotNull(shipment.getAPackage().getTrackingNumber());
    }

    @Test
    void shouldValidateAddresses() {
        Assertions.assertTrue(emptyShipment.validateAddresses());
    }
}
