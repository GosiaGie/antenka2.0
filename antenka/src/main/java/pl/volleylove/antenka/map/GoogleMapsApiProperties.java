package pl.volleylove.antenka.map;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@NoArgsConstructor
@ConfigurationProperties("google.maps")
public class GoogleMapsApiProperties {

    private String apiKey;
    private String apiPath;

}
