package com.eda.shippingService.domain.entity;

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
@SuppressWarnings("JpaDataSourceORMInspection")
public class Shipment{
    //We use the orderId as the primary key, as it is unique and lets us match it to order way quicker
    @Getter
    @Id
    private UUID orderId;

    //Due to the nature of embedding stuff in the same table, I had to rename the columns
    //Is there a better solution?
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

    //At the moment we assume one package per shipment, but this could easily be changed to a list of packages
    @OneToOne(cascade = CascadeType.DETACH)
    private APackage aPackage;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<OrderLineItem> requestedProducts;
    @Transient
    private HashMap<UUID, Integer> requestedHashMap = new HashMap<>();

    private ShipmentStatus status;

    public Shipment(UUID orderId, Address destination, Address origin, APackage aPackage, List<OrderLineItem> orderLineItems, ShipmentStatus status){
        this.orderId = orderId;
        this.destination = destination;
        this.origin = origin;
        this.aPackage = aPackage;
        this.requestedProducts = orderLineItems;
        this.status = status;
        for (OrderLineItem orderLineItem : orderLineItems){
            requestedHashMap.put(orderLineItem.productId(), orderLineItem.quantity());
        }
    }

    public HashMap<UUID, Integer> getRequestedProductsAsHashMap(){
        return requestedHashMap;
    }

    public Integer getProductQuantity(UUID productId){
        return requestedHashMap.getOrDefault(productId, 0);
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
        HashMap<UUID, Integer> expected = getRequestedProductsAsHashMap();
        for (OrderLineItem orderLineItem: aPackage.getContents()){
            if (expected.get(orderLineItem.productId()) == null || expected.get(orderLineItem.productId()) > orderLineItem.quantity()){
                return false;
            }
        }
        return true;
    }

    public void approve(){
        if (this.status == ShipmentStatus.RESERVED || this.status == null){
            this.status = ShipmentStatus.APPROVED;
        } else {
            throw new IllegalStateException("Shipment is already in state: "+this.status+" and cannot be approved.");
        }
    }

    public void packed(){
        if (this.status == ShipmentStatus.APPROVED){
            if(checkContents()) {
                this.status = ShipmentStatus.PACKAGED;
            }
            throw new IllegalStateException("Shipment incomplete, please check contents.");
        }
        throw new IllegalStateException("Shipment must be approved before it can be packed");

    }

    public void send(){
        if (this.status != ShipmentStatus.PACKAGED){
            throw new IllegalStateException("Shipment must be packaged before it can be sent");
        }
        assignTrackingNumber(UUID.randomUUID());
        this.status = ShipmentStatus.SHIPPED;
    }

    public void inDelivery(){
        if (this.status != ShipmentStatus.SHIPPED){
            throw new IllegalStateException("That cant be");
        }
        this.status = ShipmentStatus.IN_DELIVERY;
    }

    public void delivered(){
        if (this.status != ShipmentStatus.SHIPPED){
            throw new IllegalStateException("Shipment must be shipped before it can be delivered");
        }
        this.status = ShipmentStatus.DELIVERED;
    }

    public void reserved(){
        if (this.status == null || this.status != ShipmentStatus.REQUESTED){
            throw new IllegalStateException("Shipment cannot be reserved again. Current status: " + this.status);
        }
        this.status = ShipmentStatus.RESERVED;
    }

}
