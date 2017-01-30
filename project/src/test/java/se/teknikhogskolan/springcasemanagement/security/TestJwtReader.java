package se.teknikhogskolan.springcasemanagement.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import se.teknikhogskolan.springcasemanagement.security.exception.EncodingException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestJwtReader {
    private final JwtReader reader = new JwtReader();

    private final String validJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNZSwgdGhlIHRlc3RlciIsImV4cCI6IjY1NDY2MTYxNjMxNTYxIiwidXNlcm5hbWUiOiJVc2VybmFtZV8xIn0=.uhUX0mK5H6FJ_plDfC-r-1Ffe5Qj60SlFfjwJPyRIFg=";
    private final String expiredJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOiIxNDg1MzQwMzk2IiwidXNlcm5hbWUiOiJCYXRtYW4ifQ==.4Soa4TW2DDgb7Y_aLhxikFYYT3yjx7pzhdJAhTYDLaM=";
    private final String jwtWithCorruptPayload = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAOiIxNDg1MzQwMzk2IiwidXNlcm5hbWUiOiJCYXRtYW4ifQ==.4Soa4TW2DDgb7Y_aLhxikFYYT3yjx7pzhdJAhTYDLaM=";
    private final String jwtWithCorruptHeader = "eyJ0eXAiOiJKV1QLCJhbGciOiJIUzI1NiJ9.eyJleHAiOiIxNDg1MzQwMzk2IiwidXNlcm5hbWUiOiJCYXRtYW4ifQ==.4Soa4TW2DDgb7Y_aLhxikFYYT3yjx7pzhdJAhTYDLaM=";
    private final String jwtWithFalseSignature = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNZSwgdGhlIHRlc3RlciIsImV4cCI6IjY1NDY2MTYxNjMxNTYxIiwidXNlcm5hbWUiOiJVc2VybmFtZV8xIn0=.VjmRPkxqY4PJ3YJZn4iglmt53M7ux1E5wP5kbYHCpZA=";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void jwtReadingBug() {
        final String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZWZyZXNoIiwiZXhwIjoiMTQ4NjI4NjA2NiIsInVzZXJuYW1lIjoicm9vdDEzIn0=.lF3MxU0ddYB_QvqwCao_3i9DYZvKjqokbAArdBj4dR8=";
        Map<String, String> result = reader.readClaims(jwt);
        System.out.println("result: " + result);
    }

    @Test
    public void canValidateJwt() {
        assertTrue(reader.isValid(validJwt));

        assertFalse(reader.isValid(expiredJwt));
        assertFalse(reader.isValid(jwtWithCorruptHeader));
        assertFalse(reader.isValid(jwtWithCorruptPayload));
        assertFalse(reader.isValid(jwtWithFalseSignature));
    }

    @Test
    public void readingJwtWithFakeSignatureShouldThrowException() throws EncodingException {
        exception.expect(NotAuthorizedException.class);
        exception.expectMessage("Bad Jwt signature");
        Map<String, String> claims = reader.readClaims(jwtWithFalseSignature);
        System.out.println(claims);
    }

    @Test
    public void readingExpiredJwtShouldThrowException() throws EncodingException {
        exception.expect(NotAuthorizedException.class);
        exception.expectMessage("Jwt expired");
        reader.readClaims(expiredJwt);
    }

    @Test
    public void readingJwtCorruptHeaderShouldThrowException() throws EncodingException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Corrupt Jwt");
        Map<String, String> claims = reader.readClaims(jwtWithCorruptHeader);
    }

    @Test
    public void readingJwtCorruptPayloadShouldThrowException() throws EncodingException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Corrupt Jwt");
        Map<String, String> claims = reader.readClaims(jwtWithCorruptPayload);
    }

    @Test
    public void canReadJwt() throws EncodingException {
        Map<String, String> originalClaims = new HashMap<>();
        originalClaims.put("sub", "test reader");
        originalClaims.put("exp", String.valueOf(getCurrentUnixTime() + 60));

        // if trying to decode signature; reading jwt will random crash (not always crash)
        for (int i = 0; i < 100; i++) {

            JwtBuilder builder = new JwtBuilder();
            builder.putClaims(originalClaims);
            String jwt = builder.build();

            Map<String, String> resultingClaims = reader.readClaims(jwt);

            assertEquals(originalClaims, resultingClaims);
        }
    }

    private Long getCurrentUnixTime() {
        return Instant.now().getEpochSecond();
    }
}
