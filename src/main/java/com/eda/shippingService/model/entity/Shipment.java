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
    //Due to embedded, this is a bit more complex
    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "street", column = @Column(name = "destination_street")),
                    @AttributeOverride(name = "city", column = @Column(name = "destination_city")),
                    @AttributeOverride(name = "state", column = @Column(name = "destination_state")),
                    @AttributeOverride(name = "postalCode", column = @Column(name = "destination_postalCode")),
                    @AttributeOverride(name = "country", column = @Column(name = "destination_country"))
            }
    )
    private Address destination;
    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "street", column = @Column(name = "origin_street")),
                    @AttributeOverride(name = "city", column = @Column(name = "origin_city")),
                    @AttributeOverride(name = "state", column = @Column(name = "origin_state")),
                    @AttributeOverride(name = "postalCode", column = @Column(name = "origin_postalCode")),
                    @AttributeOverride(name = "country", column = @Column(name = "origin_country"))
            }
    )
    private Address origin;
    @Getter
    private UUID orderId;
    @OneToOne(cascade = CascadeType.REMOVE)
    private APackage aPackage;
    @ElementCollection
    private List<OrderLineItem> requestedProducts;

    private ShipmentStatus status;

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
        this.status = ShipmentStatus.PACKAGED;
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.aPackage.assignTrackingNumber(trackingNumber);
    }

    public boolean validateAddresses()
    {
        return destination.validate() && origin.validate();
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

    public void send(){
        if (this.status != ShipmentStatus.PACKAGED){
            throw new IllegalStateException("Shipment must be packaged before it can be sent");
        }
        this.status = ShipmentStatus.SHIPPED;
    }

    public void delivered(){
        if (this.status != ShipmentStatus.SHIPPED){
            throw new IllegalStateException("Shipment must be shipped before it can be delivered");
        }
        this.status = ShipmentStatus.DELIVERED;
    }

}
