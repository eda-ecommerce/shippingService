package com.eda.shippingService.adapters.repo;

import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.UUID;

public class MessageCompositeKey implements Serializable {
    private UUID messageId;
    private String handlerName;
}
