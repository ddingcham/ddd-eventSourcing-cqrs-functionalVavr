package com.ddingcham.event.integration.eventstore.publisher

import com.ddingcham.event.boundary.ShopItems
import com.ddingcham.event.eventstore.EventDescriptor
import com.ddingcham.event.eventstore.publisher.EventPublisher
import com.ddingcham.event.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Source
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.Message
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

import java.time.Instant
import java.util.concurrent.BlockingQueue

class EventPublisherSpec extends IntegrationSpec {

    @Subject
    @Autowired
    EventPublisher eventPublisher
    @Autowired
    ShopItems shopItems
    @Autowired
    Source source
    @Autowired
    MessageCollector messageCollector
    @Autowired
    PendingEventFetcher pendingEventFetcher

    PollingConditions conditions
    BlockingQueue<Message<?>> channel

    def setup() {
        conditions = new PollingConditions(timeout: 12, initialDelay: 0, factor: 1)
        channel = messageCollector.forChannel(source.output())
    }

    def 'should publish pending events'() {
        given:
            EventDescriptor pendingEvent = pendingEvent()
        when:
            eventPublisher.publishPending()
        then:
            conditions.eventually {
                Message<String> received = channel.poll()
                received != null
                received.getPayload() == pendingEvent.getBody()
            }
    }

    EventDescriptor pendingEvent() {
        EventDescriptor event = new EventDescriptor("body", Instant.now(), "type", UUID.randomUUID())
        pendingEventFetcher.save(event)
        return event
    }


}