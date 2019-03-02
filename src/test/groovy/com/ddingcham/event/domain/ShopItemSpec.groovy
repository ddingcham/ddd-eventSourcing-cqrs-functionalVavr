package com.ddingcham.event.domain

import spock.lang.Specification
import spock.lang.Unroll

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

    def 'should emit item ordered event when ordering initialized item'() {

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
