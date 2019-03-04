package com.ddingcham.event.integration.eventstore

import com.ddingcham.event.domain.ShopItem
import com.ddingcham.event.domain.ShopItemRepository
import com.ddingcham.event.domain.commands.OrderWithTimeout
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import static com.ddingcham.event.ShopItemFixture.initialized

class EventSourcedShopItemRepositoryIntegrationSpec extends IntegrationSpec {

    private static final UUID uuid = UUID.randomUUID()
    private static final int PAYMENT_DEADLINE_IN_HOURS = 48
    private static final Instant TODAY = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
    private static final Instant TOMORROW = TODAY.plus(1, java.time.temporal.ChronoUnit.DAYS)
    private static final Instant DAY_AFTER_TOMORROW = TOMORROW.plus(1, java.time.temporal.ChronoUnit.DAYS)
    private static final BigDecimal ANY_PRICE = BigDecimal.TEN

    @Subject
    @Autowired
    ShopItemRepository shopItemRepository

    /*
        given: prepare ShopItem to store with time-out-order (PAYMENT_DEADLINE_IN_HOURS)
        when: store ShopItem // Repository Spec
        and: load ShopItem // By UUID
        then: validate uuid & status // ShopItem
     */
    def 'should store and load item'() {
        given:
            ShopItem stored =
                    initialized().order(new OrderWithTimeout(uuid, ANY_PRICE, TODAY, PAYMENT_DEADLINE_IN_HOURS)).get()
        when:
            shopItemRepository.save(stored)
        and:
            ShopItem loaded = shopItemRepository.findByUUID(uuid)
        then:
            loaded.uuid == stored.uuid
            loaded.status == stored.status
    }

    /*
        given: prepare ShopItem to store with time-out-order with time-out-order (PAYMENT_DEADLINE_IN_HOURS)
                ShopItem is Paid at DAY_AFTER_TOMORROW
        when: store ShopItem // Repository Spec
        and: load events about ShopItem(uuid) : TOMORROW, DAY_AFTER_TOMORROW
        then: ShopItem events(uuid) forEach validate event.Status
     */
    def 'should reconstruct item at given moment'() {

    }

}
