package org.art.vertex.domain.event;

public interface EventPublisher {

    void publish(DomainEvent event);

    void publishAsync(DomainEvent event);
}