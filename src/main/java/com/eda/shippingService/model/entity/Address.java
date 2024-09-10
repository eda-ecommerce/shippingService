package com.eda.shippingService.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public boolean validate(){
        return country.equals("DE");
    }

    @Override
    public String toString() {
        return street+" ,"+postalCode+" "+city+" \n"+state+" "+country;
    }
}
