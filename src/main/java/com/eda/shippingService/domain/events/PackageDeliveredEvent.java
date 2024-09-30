package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import elemental.json.JsonString;
import elemental.json.impl.JreJsonString;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class PackageDeliveredEvent extends DomainEvent {
    public PackageDeliveredEvent(@Nullable UUID key, Shipment shipment) {
        super(key, new PackageDeliveredPayload(shipment));
    }

    static class PackageDeliveredPayload  implements DomainEventPayload {
        private final UUID packageId;
        private final UUID shipmentId;
        private final UUID orderId;
        private final AddressDTO dest;
        private final AddressDTO origin;
        public PackageDeliveredPayload(Shipment shipment) {
            this.packageId = shipment.getAPackage().getId();
            this.shipmentId = shipment.getId();
            this.orderId = shipment.getOrderId();
            this.dest = AddressDTO.fromEntity(shipment.getDestination());
            this.origin = AddressDTO.fromEntity(shipment.getOrigin());
        }

        @Override
        public JsonString toJsonString() {
            // Implement the logic to convert the payload to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonString = mapper.writeValueAsString(this);
                return new JreJsonString(jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new JreJsonString("{}");
            }
        }
    }
}