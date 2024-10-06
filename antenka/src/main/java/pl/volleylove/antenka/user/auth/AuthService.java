package pl.volleylove.antenka.user.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.security.JwtIssuer;
import pl.volleylove.antenka.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtIssuer jwtIssuer;

    private final AuthenticationManager authenticationManager;

    public LoginResponse loginAttempt(String email, String password){

        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        //setting this authentication in Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();
        var roles = principal.getAuthorities().stream().
                map(GrantedAuthority::getAuthority)
                .toList();

        //creating token
        var token = jwtIssuer.issue(principal.getUserID(), principal.getEmail(), roles);

        //token return
        return LoginResponse.builder()
                .accessToken(token)
                .build();

    }

    public Long getAuthenticatedUserID(){

        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();

        return ((UserPrincipal) authentication.getPrincipal()).getUserID();

    }

}
