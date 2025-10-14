package org.art.vertex.domain.shared.time;

import java.time.LocalDateTime;

public interface Clock {

    LocalDateTime now();
}
