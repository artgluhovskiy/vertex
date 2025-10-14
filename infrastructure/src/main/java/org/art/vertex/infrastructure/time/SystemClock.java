package org.art.vertex.infrastructure.time;

import org.art.vertex.domain.shared.time.Clock;

import java.time.LocalDateTime;

public class SystemClock implements Clock {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
