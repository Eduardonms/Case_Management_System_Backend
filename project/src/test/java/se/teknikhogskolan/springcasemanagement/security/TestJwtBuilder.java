package se.teknikhogskolan.springcasemanagement.security;

import org.junit.Test;
import se.teknikhogskolan.springcasemanagement.security.exception.EncodingException;

public class TestJwtBuilder {

    @Test
    public void canBuildJwt() throws EncodingException {
        JwtBuilder jwtBuilder = new JwtBuilder();
        jwtBuilder.putClaim("iss", "Me, the tester");
        jwtBuilder.putClaim("username", "Username_1");
        jwtBuilder.putClaim("exp", "65466161631561");
        String jwt = jwtBuilder.build();
        System.out.println(jwt);
    }
}
