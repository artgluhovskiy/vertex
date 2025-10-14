package org.art.vertex.domain.shared.time;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public class Clock {

    private final java.time.Clock nativeClock;

    public LocalDateTime now() {
        return nativeClock.instant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }
}
