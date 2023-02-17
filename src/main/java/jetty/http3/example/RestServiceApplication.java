package jetty.http3.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * Class used for bean, configuration, and package scanning configuration.
 */
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class},
        scanBasePackages = RestServiceApplication.BASE_PACAKGE)
public class RestServiceApplication {

    public static final String BASE_PACAKGE = "jetty.http3.example";

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }

}
