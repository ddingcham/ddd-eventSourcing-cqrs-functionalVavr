package com.ddingcham.event.eventstore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity(name = "event_descriptors")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventDescriptor {

    public enum Status {
        PENDING, SENT
    }

    @Id
    @GeneratedValue(generator = "event_descriptors_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "event_descriptors_seq", sequenceName = "event_descriptors_seq", allocationSize = 1)
    @Getter
    private Long id;


    @Column(nullable = false, length = 600)
    @Getter
    private String body;

    @Column(nullable = false, name = "occurred_at")
    @Getter
    private Instant occurredAt;

    @Column(nullable = false, length = 60)
    @Getter
    private String type;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private Status status = Status.PENDING;


    @Column(nullable = false, name = "aggregate_uuid", length = 36)
    @Getter
    private UUID aggregateUUID;

    @Builder
    EventDescriptor(String body, Instant occurredAt, String type, UUID aggregateUUID) {
        this.body = body;
        this.occurredAt = occurredAt;
        this.type = type;
        this.aggregateUUID = aggregateUUID;
    }

    public EventDescriptor sent() {
        this.status = Status.SENT;
        return this;
    }
}
