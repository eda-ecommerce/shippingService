package com.eda.shippingService.domain.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
//Mark as value object, overrides equals/hashcode could be record?
@Value
@Getter
@NoArgsConstructor(force = true)
public class Address {
    String street;
    String city;
    String state;
    String postalCode;
    String country;

    //You would use some Validation framework here
    public boolean validate(){
        return country != null && country.equals("DE");
    }

    @Override
    public String toString() {
        return street+" ,"+postalCode+" "+city+" \n"+state+" "+country;
    }
}
