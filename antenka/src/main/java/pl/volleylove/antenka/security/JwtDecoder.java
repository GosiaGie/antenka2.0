package pl.volleylove.antenka.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;

public class JwtDecoder {

    private final JwtProperties properties;

    @Autowired
    public JwtDecoder(JwtProperties properties) {
        this.properties = properties;
    }

    public DecodedJWT decode(String token){
         return JWT.require(Algorithm.HMAC256(properties.getSecretKey())) //returns Base Verification - object of inner class in JWTVerifier class
                 .build() //returns JWTVerifier object
                 .verify(token); //when token is invalid, then this method throws Exception
    }

}
