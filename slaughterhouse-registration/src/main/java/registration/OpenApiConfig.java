package registration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration public class OpenApiConfig
{
  @Bean public OpenAPI registrationOpenApi()
  {
    return new OpenAPI()
        .info(new Info()
            .title("Slaughterhouse Registration API")
            .version("1.0")
            .description("REST API for registering animals arriving at the slaughterhouse "
                + "and looking up registered animals by id, farm name and/or arrival date."))
        .servers(List.of(new Server().url("http://localhost:9091").description("Local")));
  }
}
