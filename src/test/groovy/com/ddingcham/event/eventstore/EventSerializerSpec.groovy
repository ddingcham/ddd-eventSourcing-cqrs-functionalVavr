package com.ddingcham.event.eventstore

import spock.lang.Specification
import spock.lang.Subject

class EventSerializerSpec extends Specification {

    @Subject EventSerializer eventSerializer = new EventSerializer()

    def "should parse ItemOrdered event"() {

    }

    def "should parse ItemPaid event"() {

    }

    def "should parse ItemPaymentTimeout event"() {

    }
}
