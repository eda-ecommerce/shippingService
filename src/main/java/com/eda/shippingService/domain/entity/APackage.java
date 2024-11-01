package com.eda.shippingService.domain.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class APackage extends AbstractEntity{
    @Nullable
    private UUID trackingNumber;
    @Embedded
    private PackageDimensions dimensions;
    @Setter
    private Float weight;
    @ElementCollection
    private List<OrderLineItem> contents = List.of();

    public APackage(Float height, Float width, Float depth, Float weight, List<OrderLineItem> contents){
        this.weight = weight;
        this.contents = contents;
        this.dimensions = new PackageDimensions(height, width, depth, height * width * depth);
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.trackingNumber = trackingNumber;
    }
}
