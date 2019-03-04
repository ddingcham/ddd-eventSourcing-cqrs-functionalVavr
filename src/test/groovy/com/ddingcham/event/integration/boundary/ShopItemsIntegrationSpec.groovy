package com.ddingcham.event.integration.boundary

import com.ddingcham.event.integration.IntegrationSpec

class ShopItemsIntegrationSpec extends IntegrationSpec {

    def "item should wait for payment when create ordered item command comes and no item yet"() {

    }

    def "item should be paid when paying for ordered item"() {

    }

    def "cannot pay for not ordered item"() {

    }

    def "item should be marked as payment timeout when payment did not come"() {

    }

    def "cannot mark payment missing when item already paid"() {

    }

    def "cannot mark payment as missing when no item at all"() {

    }

    def "item should be paid when receiving missed payment"() {

    }

    def "ordering an item should be idempotent"() {

    }

    def "marking payment as missing should be idempotent"() {

    }

    def "paying should be idempotent"() {

    }

}
