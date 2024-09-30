package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.entity.APackage;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.eda.shippingService.domain.events.common.Message;

import java.util.UUID;

public class PackageSentEvent extends DomainEvent<PackageDTO> {
    public PackageSentEvent(APackage aPackage) {
        super(null, new PackageDTO(aPackage.getId(), aPackage.getTrackingNumber(), aPackage.getContents()));
    }
}
