package com.eda.shippingService.domain.events.common;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Getter
public abstract class Message<T> {
   @Nullable
   private final UUID messageId;
   private final UUID eventKey;
   private final long timestamp;

   private final T payload;

   public Message(@Nullable UUID eventKey, T payload) {
      this.eventKey = eventKey;
      this.timestamp = System.currentTimeMillis();
      this.messageId = UUID.randomUUID();
      this.payload = payload;
   }
}

