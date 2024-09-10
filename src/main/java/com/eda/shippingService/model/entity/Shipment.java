package com.eda.shippingService.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Shipment extends AbstractEntity {

    @Embedded
    private Address destination;
    //If we want destination, we need to embed this differently
    //@Embedded
    //private Address origin;
    private String trackingNumber;
    private UUID orderId;
    @OneToOne(cascade = CascadeType.REMOVE)
    private APackage aPackage;
    @ElementCollection
    private List<OrderLineItem> requestedProducts;

    public Integer getProductQuantity(UUID productId){
        for (OrderLineItem orderLineItem : requestedProducts){
            if (orderLineItem.getProductId().equals(productId)){
                return orderLineItem.getQuantity();
            }
        }
        return 0;
    }
    //TODO check if the contents of the Package match the requested products
}
