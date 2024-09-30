package com.eda.shippingService.domain.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class APackage extends AbstractEntity{
    @Getter
    private UUID trackingNumber;
    private Float height;
    private Float width;
    private Float depth;
    @Getter
    @Setter
    private Float weight;
    @ElementCollection
    @Getter
    private List<OrderLineItem> contents = List.of();

    public APackage(Float height, Float width, Float depth, Float weight, List<OrderLineItem> contents){
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.weight = weight;
        this.contents = contents;
    }

    public Float getVolume(){
        return height * width * depth;
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.trackingNumber = trackingNumber;
    }
}
