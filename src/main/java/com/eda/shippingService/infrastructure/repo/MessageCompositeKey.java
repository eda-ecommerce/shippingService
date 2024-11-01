package com.eda.shippingService.infrastructure.repo;

import java.io.Serializable;
import java.util.UUID;

public class MessageCompositeKey implements Serializable {
    private UUID messageId;
    private String handlerName;
}
