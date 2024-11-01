package com.eda.shippingService.domain.dto.outgoing;

import com.eda.shippingService.domain.entity.APackage;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.PackageDimensions;

import java.util.List;
import java.util.UUID;

public record PackageDTO(
        UUID id,
        UUID trackingNumber,
        PackageDimensions dimensions,
        Float weight,
        List<OrderLineItemDTO> contents
) {
    private record PackageDimensions(Float height, Float width, Float depth, Float volume) {}

    public static PackageDTO fromEntity(APackage aPackage){
        return new PackageDTO(
                aPackage.getId(),
                aPackage.getTrackingNumber() != null ? aPackage.getTrackingNumber() : null,
                new PackageDimensions(
                        aPackage.getDimensions().height(),
                        aPackage.getDimensions().width(),
                        aPackage.getDimensions().depth(),
                        aPackage.getDimensions().volume()
                ),
                aPackage.getWeight(),
                aPackage.getContents().stream()
                        .map(OrderLineItemDTO::fromEntity)
                        .toList()
        );
    }

    public APackage toEntity(){
        List<OrderLineItem> contentsE = this.contents().stream()
                .map(OrderLineItemDTO::toEntity)
                .toList();

        return new APackage(
                trackingNumber,
                new com.eda.shippingService.domain.entity.PackageDimensions(
                        dimensions().height(),
                        dimensions().width(),
                        dimensions().depth(),
                        dimensions().volume()),
                weight,
                contentsE
                );
    }
}
