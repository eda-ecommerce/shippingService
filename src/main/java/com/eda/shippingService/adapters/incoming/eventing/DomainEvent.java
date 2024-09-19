package com.eda.shippingService.adapters.incoming.eventing;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Getter
public class DomainEvent {
   @Nullable
   private final UUID eventKey;
   private final long timestamp;
   private final String source;

   private DomainEventPayload payload;

   public DomainEvent(@Nullable UUID eventKey, DomainEventPayload payload) {
      this.eventKey = eventKey;
      this.timestamp = System.currentTimeMillis();
      this.source = "Shipping";
      this.payload = payload;
   }

}
