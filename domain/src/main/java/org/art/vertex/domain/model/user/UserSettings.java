package org.art.vertex.domain.model.user;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class UserSettings {

    @Builder.Default
    Map<String, Object> preferences = new HashMap<>();

    public static UserSettings defaultSettings() {
        return UserSettings.builder().build();
    }
}