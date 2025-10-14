package org.art.vertex.application;

import org.art.vertex.application.directory.config.DirectoryApplicationConfig;
import org.art.vertex.application.tag.config.TagApplicationConfig;
import org.art.vertex.application.user.config.UserApplicationConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    UserApplicationConfig.class,
    TagApplicationConfig.class,
    DirectoryApplicationConfig.class
})
public class VertexApplicationAutoconfig {
}
