package org.example.slaughterhousepackaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication @EnableScheduling public class SlaughterhousePackagingApplication
{

  public static void main(String[] args)
  {
    SpringApplication.run(SlaughterhousePackagingApplication.class, args);
  }

}
