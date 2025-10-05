package org.art.vertex.application;

import org.art.vertex.application.user.config.UserApplicationConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    UserApplicationConfig.class
})
public class VertexApplicationAutoconfig {
}
