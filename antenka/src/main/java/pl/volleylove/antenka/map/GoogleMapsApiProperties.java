package pl.volleylove.antenka.map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("google.maps")
public class GoogleMapsApiProperties {

    private String apiKey;
    private String apiPath;

}
