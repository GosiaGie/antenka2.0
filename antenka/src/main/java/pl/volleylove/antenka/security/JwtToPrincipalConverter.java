package pl.volleylove.antenka.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT decodedJWT){
        return new UserPrincipal.UserPrincipalBuilder()
                .userID(Long.parseLong(decodedJWT.getSubject()))
                .email(decodedJWT.getClaim("email").asString())
                .authorities(extractAuthoritiesFromClaim(decodedJWT))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT decodedJWT){

        var claim = decodedJWT.getClaim("roles");

        if (claim.isNull() || claim.isMissing()){
            return List.of();
        }

        return claim.asList(SimpleGrantedAuthority.class);

    }

}
