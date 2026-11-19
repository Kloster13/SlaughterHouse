package org.example.slaughterhousepackaging;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration public class OpenApiConfig
{
  @Bean public OpenAPI packagingOpenApi()
  {
    return new OpenAPI().info(new Info().title("Slaughterhouse Packaging API").version("1.0")
            .description("REST API for registering packaged products (packages and half animals) with references "
                + "to the trays their parts came from. Messages are published to RabbitMQ."))
        .servers(List.of(new Server().url("http://localhost:9093").description("Local")));
  }
}
