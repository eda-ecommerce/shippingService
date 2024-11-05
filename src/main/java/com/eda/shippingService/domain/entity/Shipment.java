package com.eda.shippingService.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
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
    @NotNull
    private Address origin;

    //At the moment we assume one package per shipment, but this could easily be changed to a list of packages
    @OneToOne(cascade = CascadeType.REMOVE)
    private APackage aPackage;

    @ElementCollection
    private List<OrderLineItem> requestedProducts;

    private ShipmentStatus status;

    //I dislike that orderLineItem is not directly a HashMap, but I guess that's the "DDD/Architecture way?"
    //Need to revisit this from a database perspective probably
    public HashMap<UUID, Integer> getRequestedProductsAsHashMap(){
        HashMap<UUID, Integer> requestedProducts = new HashMap<>();
        for (OrderLineItem orderLineItem : this.requestedProducts){
            requestedProducts.put(orderLineItem.getProductId(), orderLineItem.getQuantity());
        }
        return requestedProducts;
    }

    //That's the reason I dislike the previous method
    //O(n) lookup time, imagine scaling this to 1000s of products every second
    public Integer getProductQuantity(UUID productId){
        OrderLineItem found = requestedProducts.stream().findAny().filter(orderLineItem -> orderLineItem.getProductId().equals(productId)).orElse(null);
        if (found != null){
            return found.getQuantity();
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

    //This is O(2n) btw
    public boolean checkContents(){
        HashMap<UUID, Integer> expected = getRequestedProductsAsHashMap();
        for (OrderLineItem orderLineItem: aPackage.getContents()){
            if (expected.get(orderLineItem.getProductId()) == null || expected.get(orderLineItem.getProductId()) > orderLineItem.getQuantity()){
                return false;
            }
        }
        return true;
    }

    public void approve(){
        if (this.status == ShipmentStatus.RESERVED || this.status == null){
            this.status = ShipmentStatus.APPROVED;
        } else {
            throw new IllegalStateException("Shipment is way past the point of no return");
        }
    }

    public void pack(){
        if (this.status != ShipmentStatus.APPROVED){
            throw new IllegalStateException("Shipment must be approved before it can be packed");
        }

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
