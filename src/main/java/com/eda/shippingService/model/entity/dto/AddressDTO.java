package com.eda.shippingService.model.entity.dto;

public record AddressDTO(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {}