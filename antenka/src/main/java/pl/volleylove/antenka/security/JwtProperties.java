package pl.volleylove.antenka.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("security.jwt") //configuration is in the application.properties file and starts with security.jwt
public class JwtProperties {

    private String secretKey;

}
