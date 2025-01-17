package mx.txalcala.spring_reactor_app.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceWebPropertiesConfig {

    // Agregamos la definición de recursos para la clase exception
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

}
