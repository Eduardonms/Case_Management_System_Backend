package se.teknikhogskolan.springcasemanagement.security;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import se.teknikhogskolan.springcasemanagement.security.exception.EncodingException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;

public final class JwtReader {

    /** @param jwt must have been built by JwtBuilder to have correct format and signature
     * @throws IllegalArgumentException if jwt has wrong format, see param */
    public Map<String, String> readClaims(String jwt) throws EncodingException, IllegalArgumentException {
        if (isBroken(jwt)) throw new IllegalArgumentException("Corrupt Jwt");
        if (isExpired(jwt)) throw new NotAuthorizedException("Jwt expired");
        if (!isCorrectSigned(jwt)) throw new NotAuthorizedException("Bad Jwt signature");

        String[] parts = jwt.split("\\.");
        JSONObject payload = new JSONObject(new String(Base64.getDecoder().decode(parts[1])));
        return toMap(payload);
    }

    private boolean isBroken(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) return true;
        for (String s : parts){
            if (null == s || s.isEmpty()) return true;
        }
        if (!isJson(parts[0]) || !isJson(parts[1])) return true;
        if (null == parts[2] || parts[2].isEmpty()) return true;
        return false;
    }

    private boolean isJson(String text) {
        try {
            new JSONObject(new String(Base64.getDecoder().decode(text)));
        } catch (JSONException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private boolean isExpired(String jwt) {
        String[] parts = jwt.split("\\.");
        JSONObject payload = new JSONObject(new String(Base64.getDecoder().decode(parts[1])));

        long expirationTime;
        try {
            expirationTime = payload.getLong("exp");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Broken authorization credentials");
        }
        long now = Instant.now().getEpochSecond();

        if (now > expirationTime) return true;
        else return false;
    }

    private boolean isCorrectSigned(String jwt) throws EncodingException {
        String[] parts = jwt.split("\\.");
        Map<String, String> payload = toMap(new JSONObject(new String(Base64.getDecoder().decode(parts[1]))));
        String signature = parts[2];

        JwtBuilder jwtBuilder = new JwtBuilder();
        payload.forEach((key, value) -> jwtBuilder.putClaim(key, value));
        String generatedJwt;
        try {
            generatedJwt = jwtBuilder.build();
        } catch (EncodingException e) {
            throw new EncodingException("Cannot recreate Jwt signature", e);
        }
        String[] generatedParts = generatedJwt.split("\\.");
        String generatedSignature = generatedParts[2];

        return generatedSignature.equals(signature);
    }

    private Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        Iterator<?> keys = object.keys();
        while(keys.hasNext()){
            String key = (String) keys.next();
            String value = object.getString(key);
            map.put(key, value);
        }
        return map;
    }

    public boolean isValid(String jwt) {
        return !isBroken(jwt) && !isExpired(jwt) && isCorrectSigned(jwt);
    }
}
