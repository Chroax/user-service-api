package com.binar.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("User Service Api")
                        .description("User Service API for Ticketing")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .name("Cahyadi Surya Nugraha")
                                        .email("cahyadisn6@gmail.com")
                                        .url("github.com/Chroax")
                        )
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearer-jwt", Arrays.asList("read", "write"))
                                .addList("bearer-key", Collections.emptyList())
                )
                .servers(servers());
    }

    private List<Server> servers() {
        List<Server> servers = new ArrayList<>();

        Server serverDev = new Server();
        serverDev.setUrl("http://localhost:8075/");
        serverDev.setDescription("Main server for Dev");

        Server serverProd = new Server();
        serverProd.setUrl("https://api-mticketing.up.railway.app/");
        serverProd.setDescription("Main server for Production");

        servers.add(serverDev);
        servers.add(serverProd);
        return servers;
    }
}