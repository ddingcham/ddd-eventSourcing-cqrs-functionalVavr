package com.ddingcham.event.domain;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ShopItemRepository {

    ShopItem save(ShopItem aggregate);
    ShopItem findByUUID(UUID uuid);
    ShopItem findByUUIDat(UUID uuid, Instant at);
}
