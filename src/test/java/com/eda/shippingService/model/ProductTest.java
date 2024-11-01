package com.eda.shippingService.model;

import com.eda.shippingService.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private Product p1;

    // TODO: make sure we dont need Kafka to run this test

    @BeforeEach
    void init(){
        p1 = new Product(
                10,false, "ABC", 10f, 0
        );
    }

    @Test
    void reduceStock() {
        p1.reduceStock(5);
        assertEquals(5.0, p1.getStock().floatValue());
    }

    @Test
    void increaseStock() {
        p1.increaseStock(5);
        assertEquals(15.0,p1.getStock().floatValue());
    }

    @Test
    void retire() {
        p1.retire();
        assertTrue(p1.isRetired());
    }
}
