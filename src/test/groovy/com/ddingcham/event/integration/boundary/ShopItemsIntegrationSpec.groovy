package com.ddingcham.event.integration.boundary

import com.ddingcham.event.boundary.ShopItems
import com.ddingcham.event.domain.ShopItem
import com.ddingcham.event.domain.ShopItemStatus
import com.ddingcham.event.eventstore.EventStore
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static com.ddingcham.event.CommandFixture.markPaymentTimeoutCommand
import static com.ddingcham.event.CommandFixture.orderItemCommand
import static com.ddingcham.event.CommandFixture.payItemCommand

class ShopItemsIntegrationSpec extends IntegrationSpec {

    private final UUID uuid = UUID.randomUUID()

    @Subject @Autowired ShopItems shopItems

    @Autowired EventStore eventStore

    def "item should wait for payment when create ordered item command comes and no item yet"() {
        when:
            shopItems.order(orderItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.ORDERED
    }

    def "item should be paid when paying for ordered item"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.PAID

    }

    def "cannot pay for not ordered item"() {
        when:
            shopItems.pay(payItemCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Cannot pay")

    }

    def "item should be marked as payment timeout when payment did not come"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.PAYMENT_MISSING

    }

    def "cannot mark payment missing when item already paid"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Item already paid")

    }

    def "cannot mark payment as missing when no item at all"() {
        when:
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Payment is not missing yet")

    }

    def "item should be paid when receiving missed payment"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.PAID

    }

    def "ordering an item should be idempotent"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.order(orderItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.ORDERED

    }

    def "marking payment as missing should be idempotent"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.PAYMENT_MISSING

    }

    def "paying should be idempotent"() {
        when:
            shopItems.order(orderItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.status == ShopItemStatus.PAID

    }

}
