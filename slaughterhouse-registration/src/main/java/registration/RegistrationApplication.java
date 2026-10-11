package registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("domain")
public class RegistrationApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(RegistrationApplication.class, args);
  }
}
