package org.art.vertex.domain.shared.event;

public interface EventPublisher {

    void publish(DomainEvent event);

    void publishAsync(DomainEvent event);
}