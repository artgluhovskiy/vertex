package org.art.vertex.domain.shared.event;

public interface EventHandler<T extends DomainEvent> {

    void handle(T event);

    Class<T> getEventType();
}