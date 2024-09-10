package com.eda.shippingService.model.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Product extends AbstractEntity{
    private Number stock;
    private boolean retired;
    private String storageLocation;
    private Float weight;

    public Product(UUID id, Number stock) {
        this.setId(id);
        this.stock = stock;
        this.retired = false;
        this.storageLocation = RandomStringGenerator.generateRandomString(5);
    }

    public void reduceStock(Number amount) {
        this.stock = this.stock.doubleValue() - amount.doubleValue();
    }

    public void increaseStock(Number amount) {
        this.stock = this.stock.doubleValue() + amount.doubleValue();
    }

    public void retire() {
        this.retired = true;
    }

    private static class RandomStringGenerator {
        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final Random RANDOM = new Random();

        public static String generateRandomString(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(index));
            }
            return sb.toString();
        }
    }
}
