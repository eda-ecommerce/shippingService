package com.eda.shippingService.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
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

    public void addPackage(APackage aPackage){
        this.aPackage = aPackage;
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.aPackage.assignTrackingNumber(trackingNumber);
    }

    public void validate()
    {
        if (!destination.validate())
        {
            throw new IllegalArgumentException("Destination is not in Germany");
        }
    }

    public boolean checkContents(){
        HashMap<UUID, Integer> expected = new HashMap<>();
        for (OrderLineItem orderLineItem : requestedProducts){
            expected.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
        }
        for (OrderLineItem orderLineItem: aPackage.getContents()){
            if (expected.get(orderLineItem.getProductId()) == null || expected.get(orderLineItem.getProductId()) > orderLineItem.getQuantity()){
                return false;
            }
        }
        return true;
    }

    //TODO check if the contents of the Package match the requested products
}
