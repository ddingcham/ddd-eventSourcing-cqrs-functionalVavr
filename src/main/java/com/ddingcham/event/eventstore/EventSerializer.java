package com.ddingcham.event.eventstore;

import com.ddingcham.event.domain.events.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EventSerializer {

    private final ObjectMapper objectMapper;

    EventSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    DomainEvent deserialize(EventDescriptor eventDescriptor) {
        try {
            return objectMapper.readValue(eventDescriptor.getBody(), DomainEvent.class);
        } catch (IOException e) {
            throw new UnSupportedSerializableEventException(e);
        }
    }

    EventDescriptor serialize(DomainEvent event) {
        try {
            return EventDescriptor
                    .builder()
                    .body(objectMapper.writeValueAsString(event))
                    .occurredAt(event.when())
                    .type(event.type())
                    .aggregateUUID(event.aggregateUuid())
                    .build();
        } catch (JsonProcessingException e) {
            throw new UnSupportedSerializableEventException(e);
        }
    }

    static class UnSupportedSerializableEventException extends RuntimeException {
        public UnSupportedSerializableEventException(Throwable cause) {
            super(cause);
        }
    }
}
