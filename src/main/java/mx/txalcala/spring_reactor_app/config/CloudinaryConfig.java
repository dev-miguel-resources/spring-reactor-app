package mx.txalcala.spring_reactor_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cloudinary.utils.ObjectUtils;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "escalab-academy",
                "api_key", "397328258457897",
                "api_secret", "8bcc-8EAJ36pLCOoXG8efDlyhqo"));
    }

}
