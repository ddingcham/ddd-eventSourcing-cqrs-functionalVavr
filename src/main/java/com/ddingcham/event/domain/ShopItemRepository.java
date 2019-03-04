package com.ddingcham.event.domain;

import java.time.Instant;
import java.util.UUID;

public interface ShopItemRepository {

    ShopItem save(ShopItem aggregate);
    ShopItem findByUUID(UUID uuid);
    ShopItem findByUUIDat(UUID uuid, Instant at);
}
