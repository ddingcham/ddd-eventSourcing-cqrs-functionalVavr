package com.ddingcham.event.eventstore;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Entity(name = "event_streams")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventStream {

    @Id
    @GeneratedValue(generator = "event_stream_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "event_stream_seq", sequenceName = "event_stream_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false, name = "aggregate_uuid", length = 36)
    @Getter
    private UUID aggregateUUID;

    /*
        @Version
        Specifies the version field or property of an entity class that
        serves as its optimistic lock value.
     */
    @Version
    @Column(nullable = false)
    private long version;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EventDescriptor> events = new ArrayList<>();

    EventStream(UUID aggregateUUID) {
        this.aggregateUUID = aggregateUUID;
    }

    void addEvents(List<EventDescriptor> events) {
        this.events.addAll(events);
    }

    List<EventDescriptor> getEvents() {
        return events
                .stream()
                .sorted(comparing(EventDescriptor::getOccurredAt))
                .collect(Collectors.toList());
    }

}
