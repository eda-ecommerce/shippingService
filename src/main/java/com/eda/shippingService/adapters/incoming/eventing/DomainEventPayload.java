package com.eda.shippingService.adapters.incoming.eventing;

import elemental.json.JsonString;

public interface DomainEventPayload {
    JsonString toJsonString();
}
