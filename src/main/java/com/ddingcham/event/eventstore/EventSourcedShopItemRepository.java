package com.ddingcham.event.eventstore;

import com.ddingcham.event.domain.ShopItem;
import com.ddingcham.event.domain.ShopItemRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class EventSourcedShopItemRepository implements ShopItemRepository {

    @Override
    public ShopItem save(ShopItem aggregate) {
        return null;
    }

    @Override
    public ShopItem findByUUID(UUID uuid) {
        return null;
    }

    @Override
    public ShopItem findByUUIDat(UUID uuid, Instant at) {
        return null;
    }
}
