package se.teknikhogskolan.springcasemanagement.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;

/** Uses HmacSHA256 */
public final class JwtBuilder {

    private final String typ = "JWT";
    private final String alg = "HS256";

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
            LocalConfigurations localConfigurations = new LocalConfigurations();
            hmacSha256.init(new SecretKeySpec(localConfigurations.getSecret().getBytes(),hmacAlgorithm));
            return hmacSha256;

        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncodingException("Cannot create JWT signature", e);
        }
    }
}
