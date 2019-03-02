package com.ddingcham.event.integration.eventstore.publisher

import com.ddingcham.event.eventstore.EventDescriptor
import com.ddingcham.event.eventstore.publisher.PendingEventFetcher
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.Instant

import static com.ddingcham.event.eventstore.EventDescriptor.Status.PENDING

class PendingEventFetcherSpec extends IntegrationSpec {

    @Subject @Autowired PendingEventFetcher pendingEventFetcher

    def 'should fetch only pending events'() {
        given:
            10.times { pendingEvent() }
            3.times { sentEvent() }
        when:
            List<EventDescriptor> events = pendingEventFetcher.listPending()
        then:
            events.every {it.status == PENDING}
    }

    def 'should fetch events in correct order'() {
        given:
            3.times { pendingEvent() }
        when:
            List<EventDescriptor> pendingEvents = pendingEventFetcher.listPending()
        then:
            pendingEvents.head().occurredAt.isBefore(pendingEvents.last().occurredAt)
            pendingEvents.head().occurredAt.isBefore(pendingEvents.get(1).occurredAt)
            pendingEvents.get(1).occurredAt.isBefore(pendingEvents.last().occurredAt)

    }


    EventDescriptor pendingEvent() {
        EventDescriptor event = new EventDescriptor("body", Instant.now(), "type", UUID.randomUUID())
        pendingEventFetcher.save(event)
        return event
    }

    EventDescriptor sentEvent() {
        EventDescriptor event = new EventDescriptor("body", Instant.now(), "type", UUID.randomUUID())
        event.sent()
        pendingEventFetcher.save(event)
        return event
    }

}
