package com.eda.shippingService.domain.events;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Getter
public abstract class DomainEvent {
   @Nullable
   private final UUID eventKey;
   private final long timestamp;
   private final UUID messageId;

   private DomainEventPayload payload;

   public DomainEvent(@Nullable UUID eventKey, DomainEventPayload payload) {
      this.eventKey = eventKey;
      this.timestamp = System.currentTimeMillis();
      this.messageId = UUID.randomUUID();
      this.payload = payload;
   }

}

