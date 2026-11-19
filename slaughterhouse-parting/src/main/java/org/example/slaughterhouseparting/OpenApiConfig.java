package org.example.slaughterhouseparting;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration public class OpenApiConfig
{
  @Bean public OpenAPI partingOpenApi()
  {
    return new OpenAPI().info(new Info().title("Slaughterhouse Parting API").version("1.0")
            .description("REST API for registering trays with animal parts. Messages are published to RabbitMQ."))
        .servers(List.of(new Server().url("http://localhost:9092").description("Local")));
  }
}
