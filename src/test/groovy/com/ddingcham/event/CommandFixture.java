package com.ddingcham.event;

import com.ddingcham.event.domain.commands.Order;
import com.ddingcham.event.domain.commands.Pay;

import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static java.time.Instant.now;

public class CommandFixture {

    public static Order orderItemCommand(UUID uuid) {
        return new Order(uuid, ZERO, now());
    }

    public static Pay payItemCommand(UUID uuid) {
        return new Pay(uuid, now());
    }
}
