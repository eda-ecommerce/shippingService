package com.eda.shippingService.model.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class APackage extends AbstractEntity{
    private UUID trackingNumber;
    //TODO: everything
}
