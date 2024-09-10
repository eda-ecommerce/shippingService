package com.eda.shippingService.model;

import com.eda.shippingService.model.entity.APackage;
import com.eda.shippingService.model.entity.OrderLineItem;
import com.eda.shippingService.model.entity.Product;
import com.eda.shippingService.model.entity.Shipment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ShipmentTest {
    private static final UUID productId = UUID.fromString("1a0000-0000-0000-0000-000000000000");
    private static final UUID shipmentid = UUID.fromString("1b0000-0000-0000-0000-000000000000");
    @Test
    public void shouldValidateContents() {
        //given
        Product product = new Product(productId,30);

        OrderLineItem requested = new OrderLineItem(productId, 10);
        OrderLineItem contents = new OrderLineItem(productId, 10);
        APackage aPackage = new APackage(10f, 10f, 10f, 100f, List.of(contents));
        Shipment shipment = new Shipment(
                null,
                shipmentid,
                aPackage,
                List.of(requested)
        );
        Assertions.assertTrue(shipment.checkContents());
    }
    @Test
    public void shouldInvalidateContents() {
        //given
        Product product = new Product(productId, 30);

        OrderLineItem requested = new OrderLineItem(productId, 10);
        OrderLineItem contents = new OrderLineItem(productId, 5); // Mismatched quantity
        APackage aPackage = new APackage(10f, 10f, 10f, 100f, List.of(contents));
        Shipment shipment = new Shipment(
                null,
                shipmentid,
                aPackage,
                List.of(requested)
        );
        Assertions.assertFalse(shipment.checkContents());
    }
}
