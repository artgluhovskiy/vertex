package org.art.vertex.domain.user.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class UserSettings {

    @Builder.Default
    Map<String, Object> preferences = Map.of();

    public static UserSettings defaultSettings() {
        return UserSettings.builder().build();
    }
}