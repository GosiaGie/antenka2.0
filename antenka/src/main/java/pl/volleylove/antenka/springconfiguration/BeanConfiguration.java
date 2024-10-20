package pl.volleylove.antenka.springconfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.volleylove.antenka.map.GoogleMapsApiHelper;
import pl.volleylove.antenka.map.GoogleMapsApiProperties;
import pl.volleylove.antenka.security.*;

@Configuration
public class BeanConfiguration {

    @Bean
    public JwtIssuer jwtIssuer() {
        return new JwtIssuer(new JwtProperties());
    }

    @Bean
    public JwtDecoder jwtDecoder(){return new JwtDecoder(new JwtProperties());};

    @Bean
    public JwtToPrincipalConverter jwtToPrincipalConverter(){return new JwtToPrincipalConverter();}

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtDecoder(), jwtToPrincipalConverter());}

    @Bean
    public PasswordEncoder passwordEncoderForService(){return new BCryptPasswordEncoder();}

    @Bean
    public GoogleMapsApiHelper googleMapsHelper(){return new GoogleMapsApiHelper(new GoogleMapsApiProperties());}








}
