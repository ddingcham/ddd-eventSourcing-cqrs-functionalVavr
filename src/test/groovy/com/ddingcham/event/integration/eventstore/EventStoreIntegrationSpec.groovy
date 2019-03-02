package com.ddingcham.event.integration.eventstore

import com.ddingcham.event.boundary.ShopItems
import com.ddingcham.event.domain.events.ItemOrdered
import com.ddingcham.event.eventstore.EventStore
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

class EventStoreIntegrationSpec extends IntegrationSpec {

    private final UUID uuid = UUID.randomUUID()

    @Subject @Autowired ShopItems shopItems

    @Autowired EventStore eventStore

    def 'should store item ordered event when create bought item command comes and no item yet'() {
        when:
            shopItems.order(orderItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE, ItemPaid.TYPE]
    }

    def 'should store item paid event when paying for existing item'() {

    }

    def 'should store item paid event when receiving missed payment'() {

    }

    def 'ordering an item should be idempotent - should store only 1 event'() {

    }

    def 'marking payment as missing should be idempotent - should store only 1 event'() {

    }

    def 'paying should be idempotent - should store only 1 event'() {

    }
}
