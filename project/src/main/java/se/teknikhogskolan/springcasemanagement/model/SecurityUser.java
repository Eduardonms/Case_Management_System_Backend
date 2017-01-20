package se.teknikhogskolan.springcasemanagement.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
public class SecurityUser {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> tokensExpiration = new HashMap<>();

    private String hashedPassword = "";
    private String salt = "";
    private int saltingIterations = 0;

    public SecurityUser(String username) {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        this.username = username;
    }

    protected SecurityUser() { /* used by JPA */ }

    public SecurityUser(String username, Map<String, String> tokensExpiration, String hashedPassword, String salt, int saltingIterations) {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        this.username = username;
        this.tokensExpiration = tokensExpiration;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.saltingIterations = saltingIterations;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setSaltingIterations(int saltingIterations) {
        this.saltingIterations = saltingIterations;
    }

    public void addToken(String token, LocalDateTime expires) {
        this.tokensExpiration.put(token, expires.toString());
    }

    public void removeToken(String token) {
        this.tokensExpiration.remove(token);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTokensExpiration(Map<String, String> tokensExpiration) {
        this.tokensExpiration = tokensExpiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecurityUser user = (SecurityUser) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return 37 * username.hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SecurityUser{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", tokensExpiration=").append(tokensExpiration);
        sb.append(", hashedPassword='").append(hashedPassword).append('\'');
        sb.append(", salt='").append(salt).append('\'');
        sb.append(", saltingIterations=").append(saltingIterations);
        sb.append('}');
        return sb.toString();
    }

    public Long getId() { // TODO check mutable
        return id;
    }

    public String getUsername() { // TODO check mutable
        return username;
    }

    public String getHashedPassword() { // TODO check mutable
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public int getSaltingIterations() {
        return saltingIterations;
    }

    public Map<String, String> getTokensExpiration() {
        return tokensExpiration;
    }
}
