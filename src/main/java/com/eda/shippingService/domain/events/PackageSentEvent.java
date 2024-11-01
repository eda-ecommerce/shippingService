package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.entity.APackage;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class PackageSentEvent extends DomainEvent<PackageDTO> {
    public PackageSentEvent(APackage aPackage) {
        super(null, PackageDTO.fromEntity(aPackage));
    }
}
