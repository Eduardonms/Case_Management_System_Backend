package se.teknikhogskolan.springcasemanagement.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;

/** Uses HmacSHA256 */
public final class JwtBuilder {

    private final String typ = "JWT";
    private final String alg = "HS256";
    private final String secret = "jwt_key";

    private HashMap<String, String> claims = new HashMap<>();

    public JwtBuilder() {}

    public JwtBuilder putClaim(String claim, String value) {
        claims.put(claim, value);
        return this;
    }

    public JwtBuilder putClaims(Map<String, String> claims) {
        this.claims.putAll(claims);
        return this;
    }

    /** @return jwt String with header, payload and signature Base64 url safe encoded. Signed using HmacSHA256 */
    public String build() throws EncodingException {

        final StringBuilder builder = new StringBuilder();

        builder.append(getEncodedHeader());
        builder.append(".");
        builder.append(getEncodedPayload());
        builder.append(".");
        builder.append(getSignature());
        return builder.toString();
    }

    private String getEncodedHeader() {
        final String quote = "\"";
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"typ\":\"").append(typ).append(quote);
        builder.append(",");
        builder.append("\"alg\":\"").append(alg).append(quote);
        builder.append("}");
        return Base64.getUrlEncoder().encodeToString(builder.toString().getBytes());
    }

    private String getEncodedPayload() {
        return Base64.getUrlEncoder().encodeToString(new JSONObject(claims).toString().getBytes());
    }

    /* uses HmacSHA256 */
    private String getSignature() throws EncodingException {
        Mac hmacSha256 = createHmacWithSecret();

        StringBuilder builder = new StringBuilder();
        builder.append(getEncodedHeader());
        builder.append(".");
        builder.append(getEncodedPayload());
        return Base64.getUrlEncoder().encodeToString(hmacSha256.doFinal(builder.toString().getBytes()));
    }

    private final Mac createHmacWithSecret() throws EncodingException {
        final String hmacAlgorithm = "HmacSHA256";
        try {

            Mac hmacSha256 = Mac.getInstance(hmacAlgorithm);
            hmacSha256.init(new SecretKeySpec(getSecret().getBytes(),hmacAlgorithm));
            return hmacSha256;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncodingException("Cannot create JWT signature", e);
        }
    }

    private final String getSecret() throws EncodingException {

        final Properties properties = new Properties();
        final File file = new File(System.getProperty("user.home") + "/spring-case-management.properties");
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new EncodingException(String.format("Cannot retrieve secret Jwt key, file missing '%s'", file));
        }

        if (null == properties.getProperty(secret)) {
            properties.put(secret, generateToken(255));
            try {
                properties.store(new FileOutputStream(file), null);
            } catch (IOException e) {
                throw new EncodingException("Cannot create secret Jwt key.", e);
            }
        }

        return properties.getProperty(secret);
    }

    private final String generateToken(int length) {
        final StringBuilder builder = new StringBuilder();
        final SecureRandom random = new SecureRandom();
        final String characters = "0123456789abcdfghijklmopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWXYZ";
        for (int i = 0; i < length; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }
}
