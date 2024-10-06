package pl.volleylove.antenka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {

    @Autowired
    private final UserPrincipal userPrincipal;

    public UserPrincipalAuthenticationToken(UserPrincipal userPrincipal) {
        super(userPrincipal.getAuthorities());
        this.userPrincipal = userPrincipal;
        setAuthenticated(true); //sets token to authenticated - without it doesn't work
    }

    @Override
    public Object getCredentials() {return null;}

    @Override
    public UserPrincipal getPrincipal() {
        return userPrincipal;
    }
}
