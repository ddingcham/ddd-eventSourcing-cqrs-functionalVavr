package com.ddingcham.event;

import com.ddingcham.event.domain.ShopItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;

public class ShopItemFixture {

    public static final Instant ANY_TIME = now();
    public static final int ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT = 48;
    public static final BigDecimal ANY_PRICE = BigDecimal.TEN;


    public static ShopItem initialized() {
        return null;
    }

    public static ShopItem ordered(UUID uuid) {
        return null;
    }

    public static ShopItem paid(UUID uuid) {
        return null;
    }

    public static ShopItem withTimeout(UUID uuid) {
        return null;
    }

    public static ShopItem withTimeoutAndPaid(UUID uuid) {
        return null;
    }
}
