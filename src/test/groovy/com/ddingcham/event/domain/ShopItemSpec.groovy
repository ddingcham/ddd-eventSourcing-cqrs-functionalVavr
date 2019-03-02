package com.ddingcham.event.domain

import com.ddingcham.event.domain.commands.OrderWithTimeout
import com.ddingcham.event.domain.events.ItemOrdered
import io.vavr.control.Try
import spock.lang.Specification
import spock.lang.Unroll

import static com.ddingcham.event.ShopItemFixture.initialized
import static java.time.Instant.now

/*
 * @Unroll
 * Indicates that iterations of a data-driven feature should be made visible
 * as separate features to the outside world (IDEs, reports, etc.).
 * By default,
 * the name of an iteration is the feature's name followed by a consecutive number.
 * This can be changed by providing a naming pattern after @Unroll.
 */
@Unroll
class ShopItemSpec extends Specification {

    private static final int PAYMENT_DEADLINE_IN_HOURS = 48
    private static final BigDecimal ANY_PRICE = BigDecimal.TEN
    private final UUID uuid = UUID.randomUUID()


    def 'should emit item ordered event when ordering initialized item'() {
        given:
            ShopItem initialized = initialized()
        when:
            Try<ShopItem> tryOrder =
                    initialized.order(new OrderWithTimeout(uuid, ANY_PRICE, now(), PAYMENT_DEADLINE_IN_HOURS))
        then:
            tryOrder.isSuccess()
            tryOrder.get().getUncommittedChanges().size() == 1
            tryOrder.get().getUncommittedChanges().head().type() == ItemOrdered.TYPE

    }

    def 'should calculate #deadline when ordering at #orderingAt and expiration in days #expiresIn'() {

    }

    def 'Payment expiration date cannot be in the past'() {

    }

    def 'ordering an item should be idempotent'() {

    }

    def 'cannot pay for just initialized item'() {

    }

    def 'cannot mark payment timeout when item just initialized'() {

    }

    def 'should emit item paid event when paying for ordered item'() {

    }

    def 'paying for an item should be idempotent'() {

    }

    def 'should emit payment timeout event when marking items as payment missing'() {

    }

    def 'marking payment timeout should be idempotent'() {

    }

    def 'cannot mark payment missing when item already paid'() {

    }

    def 'should emit item paid event when receiving missed payment'() {

    }

    def 'receiving payment after timeout should be idempotent'() {

    }
}
