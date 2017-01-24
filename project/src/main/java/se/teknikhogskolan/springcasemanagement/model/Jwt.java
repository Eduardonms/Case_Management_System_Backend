package se.teknikhogskolan.springcasemanagement.model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;

/** Uses HmacSHA256 */
@Entity
public class Jwt {

    // Header
    private final String typ = "JWT";
    private final String alg = "HS256";

    // Payload
    @Id
    @GeneratedValue
    private Long jti;

    @Column(updatable = false)
    private LocalDateTime iat;

    @Column(nullable = false, updatable = false)
    private LocalDateTime exp;

    @Column(nullable = false, updatable = false)
    private String iss;

    @Column(nullable = false, updatable = false)
    private String sub;

    @Column(nullable = false, updatable = false)
    private String username;

    // For signature
    @Column(nullable = false, unique = true, updatable = false)
    private String secret;

    public Jwt() { /* Used by JPA */ }

    public Jwt(String iss, String sub, String username, String secret, LocalDateTime iat, LocalDateTime exp) {
        this.iss = iss;
        this.sub = sub;
        this.username = username;
        this.secret = secret;
        this.iat = iat;
        this.exp = exp;
    }

    public String getUsername() {
        return new String(username);
    }

    public Long getJti() {
        return jti;
    }

    public String getHeader() {
        final String quote = "\"";
        final String comma = ",";
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"typ\":\"").append(typ).append(quote);
        builder.append(comma);
        builder.append("\"alg\":\"").append(alg).append(quote);
        builder.append("}");
        return builder.toString();
    }

    private String getEncodedHeader() {
        return Base64.getUrlEncoder().encodeToString(getHeader().getBytes());
    }

    public String getPayload() {
        final String quote = "\"";
        final String comma = ",";
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"jti\": ").append(jti);
        builder.append(comma);
        builder.append("\"iat\": \"").append(iat).append(quote);
        builder.append(comma);
        builder.append("\"exp\": \"").append(exp).append(quote);
        builder.append(comma);
        builder.append("\"iss\": \"").append(iss).append(quote);
        builder.append(comma);
        builder.append("\"sub\": \"").append(sub).append(quote);
        builder.append(comma);
        builder.append("\"username\": \"").append(username).append(quote);
        builder.append("}");
        return builder.toString();
    }

    private String getEncodedPayload() {
        return Base64.getUrlEncoder().encodeToString(getPayload().getBytes());
    }

    /* uses HmacSHA256 */
    private String getSignature() throws EncodingException {
        if (null == secret) throw new EncodingException("Cannot create JWT signature without secret");

        Mac hmacSha256 = getHmacWithSecret();

        StringBuilder builder = new StringBuilder();
        builder.append(getEncodedHeader());
        builder.append(".");
        builder.append(getEncodedPayload());

        return Base64.getUrlEncoder().encodeToString(hmacSha256.doFinal(builder.toString().getBytes()));
    }

    private Mac getHmacWithSecret() throws EncodingException {
        final String hmacAlgorithm = "HmacSHA256";
        try {
            Mac hmacSha256 = Mac.getInstance(hmacAlgorithm);
            hmacSha256.init(new SecretKeySpec(secret.getBytes(),hmacAlgorithm));
            return hmacSha256;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncodingException("Cannot create JWT signature", e);
        }
    }

    /** @return jwt String with header, payload and signature Base64 url safe encoded. Signed using HmacSHA256 */
    public String generateJWT() throws EncodingException {
        if (null == jti || jti == 0) throw new EncodingException(
                "Cannot generate Jwt string without jti, persist Jwt to retrieve jti");
        if (null == secret || secret.isEmpty()) throw new EncodingException(
                "Cannot generate Jwt string without secret");

        final StringBuilder builder = new StringBuilder();
        builder.append(getEncodedHeader());
        builder.append(".");
        builder.append(getEncodedPayload());
        builder.append(".");
        builder.append(getSignature());
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Jwt{");
        sb.append("typ='").append(typ).append('\'');
        sb.append(", alg='").append(alg).append('\'');
        sb.append(", jti=").append(jti);
        sb.append(", iat=").append(iat);
        sb.append(", exp=").append(exp);
        sb.append(", iss='").append(iss).append('\'');
        sb.append(", sub='").append(sub).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", secret='").append(secret).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Jwt jwt = (Jwt) o;
        if (username != null ? !username.equals(jwt.username) : jwt.username != null) return false;
        return secret != null ? secret.equals(jwt.secret) : jwt.secret == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
