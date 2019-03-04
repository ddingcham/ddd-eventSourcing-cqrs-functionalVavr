package com.ddingcham.event.eventstore

import com.ddingcham.event.domain.events.ItemOrdered
import spock.lang.Specification
import spock.lang.Subject

import static java.time.Instant.now

class EventSerializerSpec extends Specification {

    final String ANY_TYPE = "ANY_TYPE"
    final UUID ANY_UUID = UUID.fromString("9a94d251-5fdb-4f38-b308-9f72d2355467")

    @Subject EventSerializer eventSerializer = new EventSerializer()

    def "should parse ItemOrdered event"() {
        given:
            String body = """{
                "type": "$ItemOrdered.TYPE",
                "uuid": "${ANY_UUID.toString()}",
                "when": "2016-05-24T12:06:41.045Z",
                "price": "123.45"
            }"""
        when:
            ItemOrdered event = eventSerializer.deserialize(new EventDescriptor(body, now(), ANY_TYPE, ANY_UUID))
        then:
            event.uuid == ANY_UUID
            event.price == 123.45

    }

    def "should parse ItemPaid event"() {

    }

    def "should parse ItemPaymentTimeout event"() {

    }
}
