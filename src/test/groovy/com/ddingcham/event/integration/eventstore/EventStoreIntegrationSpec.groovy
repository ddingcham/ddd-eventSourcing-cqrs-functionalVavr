package com.ddingcham.event.integration.eventstore

import com.ddingcham.event.boundary.ShopItems
import com.ddingcham.event.domain.commands.MarkPaymentTimeout
import com.ddingcham.event.domain.commands.Order
import com.ddingcham.event.domain.commands.Pay
import com.ddingcham.event.domain.events.ItemOrdered
import com.ddingcham.event.domain.events.ItemPaid
import com.ddingcham.event.domain.events.ItemPaymentTimeout
import com.ddingcham.event.eventstore.EventStore
import com.ddingcham.event.eventstore.EventStream
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static com.ddingcham.event.CommandFixture.markPaymentTimeoutCommand
import static com.ddingcham.event.CommandFixture.orderItemCommand
import static com.ddingcham.event.CommandFixture.payItemCommand

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
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE]
    }

    def 'should store item paid event when paying for existing item'() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE, ItemPaid.TYPE]

    }

    def 'should store item paid event when receiving missed payment'() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE, ItemPaymentTimeout.TYPE, ItemPaid.TYPE]

    }

    def 'ordering an item should be idempotent - should store only 1 event'() {
        given:
            Order firstOrder = orderItemCommand(uuid)
        when:
            shopItems.order(firstOrder)
            shopItems.order(orderItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE]
            // 같은 aggregateId를 갖는 Order 이벤트에 대해서는 기록하지 않음
            eventStream.get().getEvents().get(0).getOccurredAt() == firstOrder.when
    }

    def 'marking payment as missing should be idempotent - should store only 1 event'() {
        given:
            MarkPaymentTimeout firstTimeOut = markPaymentTimeoutCommand(uuid)
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.markPaymentTimeout(firstTimeOut)
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE, ItemPaymentTimeout.TYPE]
            // 같은 aggregateId를 갖는 MarkPaymentTimeout 이벤트에 대해서는 기록하지 않음
            eventStream.get().getEvents().get(1).getOccurredAt() == firstTimeOut.when
    }

    def 'paying should be idempotent - should store only 1 event'() {
        given:
            Pay firstPay = payItemCommand(uuid)
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.pay(firstPay)
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemOrdered.TYPE, ItemPaid.TYPE]
            // 같은 aggregateId를 갖는 Pay 이벤트에 대해서는 기록하지 않음
            eventStream.get().getEvents().get(1).getOccurredAt() == firstPay.when

    }
}
