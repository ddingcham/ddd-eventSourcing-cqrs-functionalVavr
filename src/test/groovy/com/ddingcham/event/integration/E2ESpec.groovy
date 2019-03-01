package com.ddingcham.event.integration

import com.ddingcham.event.domain.events.ItemOrdered
import com.ddingcham.event.eventstore.publisher.EventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage
import spock.util.concurrent.PollingConditions

import java.util.concurrent.BlockingQueue

import static java.util.UUID.randomUUID

class E2ESpec extends IntegrationSpec {

    private ANY_UUID = randomUUID()

    @Autowired Source source
    @Autowired Sink commands
    @Autowired MessageCollector eventsCollector
    @Autowired EventPublisher eventPublisher

    BlockingQueue<Message<?>> events
    PollingConditions conditions = new PollingConditions(timeout: 12, initialDelay: 0, factor: 1)

    def setup() {
        events = eventsCollector.forChannel(source.output())
    }

    def 'received order command should result in emitted item ordered event'() {
        when:
            commands.input().send(new GenericMessage<>(sampleOrderInJson(ANY_UUID)))

        then:
            conditions.eventually {
                expectedMessageThatContains(ItemOrdered.TYPE)
            }
    }


    void expectedMessageThatContains(String text) {
        Message<String> msg = events.poll()
        println msg
        assert msg != null && msg.getPayload().contains(text)
        println "GOT IT: contains " + msg.getPayload().contains(text)
        assert msg.getPayload().contains(text)
    }

    private static String sampleOrderInJson(UUID uuid) {
        return "{\"type\":\"item.order\",\"uuid\":\"$uuid\",\"when\":\"2019-02-28T10:28:23.956Z\"}"
    }

}
