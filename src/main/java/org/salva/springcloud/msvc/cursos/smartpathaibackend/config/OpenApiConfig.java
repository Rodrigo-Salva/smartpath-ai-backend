package org.salva.springcloud.msvc.cursos.smartpathaibackend.config;

// backend/src/main/java/com/smartpath/config/OpenApiConfig.java

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartPathOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartPath AI API")
                        .description("API REST para plataforma de mentoría laboral con IA")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Tu Nombre")
                                .email("tu@email.com")));
    }
}

