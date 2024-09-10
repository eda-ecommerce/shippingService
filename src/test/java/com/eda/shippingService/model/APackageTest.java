package com.eda.shippingService.model;

import com.eda.shippingService.model.entity.APackage;
import com.eda.shippingService.model.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class APackageTest {
    private static final UUID productId = UUID.fromString("1a0000-0000-0000-0000-000000000000");
    private static final UUID shipmentid = UUID.fromString("1b0000-0000-0000-0000-000000000000");
    //@Test
    public void testBoxProduct() {
        //given
        //Product product = new Product(productId,30);
        //APackage aPackage = new APackage(10f,10f,10f,shipmentid);

        //aPackage.boxProduct(product, 10);
        //Assertions.assertEquals(10, aPackage.getContents().get(product));
    }
}
