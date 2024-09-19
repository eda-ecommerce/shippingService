package com.eda.shippingService.model.dto;

import com.eda.shippingService.model.entity.Address;
import com.eda.shippingService.model.entity.Shipment;

//Necessary?
public record AddressDTO(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {
    public static AddressDTO fromEntity(Address address){
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}