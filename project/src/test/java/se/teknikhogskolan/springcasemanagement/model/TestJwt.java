package se.teknikhogskolan.springcasemanagement.model;

import java.time.LocalDateTime;
import org.junit.Test;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;

import static org.junit.Assert.assertEquals;

public class TestJwt {

    @Test(expected = EncodingException.class)
    public void jwtStringCannotBeGeneratedWithoutObjectFirstBeingPersisted() throws EncodingException {
        Jwt jwt = new Jwt("This test", "userId", "username", "token", LocalDateTime.now(), LocalDateTime.now().plusDays(1L));
        jwt.generateJWT();
    }

    @Test
    public void canCreateJwtObject() throws EncodingException {
        LocalDateTime now = LocalDateTime.now();
        final Jwt jwt = new Jwt("This test", "userId", "username", "token", now, now.plusDays(1L));

        final String expectedHeader = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";
        assertEquals(expectedHeader, jwt.getHeader());

        final String expectedPayload = "{\"jti\": null,\"iat\": \"" + now + "\",\"exp\": \"" + now.plusDays(1L) + "\",\"iss\": \"This test\",\"sub\": \"userId\",\"username\": \"username\"}";
        assertEquals(expectedPayload, jwt.getPayload());
    }
}
