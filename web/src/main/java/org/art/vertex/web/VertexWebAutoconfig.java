package org.art.vertex.web;

import org.art.vertex.web.config.SecurityConfig;
import org.art.vertex.web.config.WebConfig;
import org.art.vertex.web.user.AuthController;
import org.art.vertex.web.user.config.UserWebConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan(basePackageClasses = {
    AuthController.class
})
@Import({
    SecurityConfig.class,
    WebConfig.class,
    UserWebConfig.class
})
public class VertexWebAutoconfig {
}
