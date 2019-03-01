package com.ddingcham.event.eventstore.publisher;

import com.ddingcham.event.eventstore.EventDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PendingEventFetcher extends JpaRepository<EventDescriptor, Long> {

    List<EventDescriptor> findTop100ByStatusOrderByOccurredAtAsc(EventDescriptor.Status status);

    default List<EventDescriptor> listPending() {
        return findTop100ByStatusOrderByOccurredAtAsc(EventDescriptor.Status.PENDING);
    }
}
