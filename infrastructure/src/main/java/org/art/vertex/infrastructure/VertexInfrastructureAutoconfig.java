package org.art.vertex.infrastructure;

import org.art.vertex.infrastructure.user.config.UserInfrastructureConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    UserInfrastructureConfig.class
})
public class VertexInfrastructureAutoconfig {
}
