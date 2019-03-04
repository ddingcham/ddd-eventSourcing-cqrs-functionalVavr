package com.ddingcham.event.eventstore;

import com.ddingcham.event.domain.ShopItem;
import com.ddingcham.event.domain.ShopItemRepository;
import com.ddingcham.event.domain.events.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Repository
public class EventSourcedShopItemRepository implements ShopItemRepository {

    private final EventStore eventStore;
    private final EventSerializer eventSerializer;

    @Autowired
    public EventSourcedShopItemRepository(EventStore eventStore, EventSerializer eventSerializer) {
        this.eventStore = eventStore;
        this.eventSerializer = eventSerializer;
    }

    @Override
    public ShopItem save(ShopItem aggregate) {
        final List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
        eventStore.saveEvents(
                aggregate.getUuid(),
                pendingEvents
                        .stream()
                        .map(eventSerializer::serialize)
                        .collect(toList()));
        return aggregate.markChangesAsCommitted();
    }

    @Override
    public ShopItem findByUUID(UUID uuid) {
        return ShopItem.rebuild(uuid, getRelatedEvents(uuid));
    }

    @Override
    public ShopItem findByUUIDat(UUID uuid, Instant at) {
        return ShopItem
                .rebuild(uuid,
                        getRelatedEvents(uuid)
                                .stream()
                                .filter(event -> !event.when().isAfter(at))
                                .collect(toList()));
    }

    private List<DomainEvent> getRelatedEvents(UUID uuid) {
        return eventStore.getEventsForAggregate(uuid)
                .stream()
                .map(eventSerializer::deserialize)
                .collect(toList());
    }
}
