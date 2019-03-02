package com.ddingcham.event.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPaymentTimeout implements DomainEvent {

    public static final String TYPE = "item.payment.timeout";

    private UUID uuid;

    @Override
    public String type() {
        return null;
    }

    @Override
    public Instant when() {
        return null;
    }

    @Override
    public UUID aggregateUuid() {
        return null;
    }
}
