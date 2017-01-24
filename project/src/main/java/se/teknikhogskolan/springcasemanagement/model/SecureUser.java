package se.teknikhogskolan.springcasemanagement.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SecureUser {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private boolean admin = false;

    private String jwt = "";

    private String hashedPassword = "";
    private String salt = "";
    private int saltingIterations = 0;

    protected SecureUser() { /* used by JPA */ }

    public SecureUser(String username, String hashedPassword, String salt, int saltingIterations, String jwt) {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.saltingIterations = saltingIterations;
        this.jwt = jwt;
    }

    public SecureUser setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public SecureUser setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
        return this;
    }

    public SecureUser setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public SecureUser setSaltingIterations(int saltingIterations) {
        this.saltingIterations = saltingIterations;
        return this;
    }

    public SecureUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public SecureUser setJwt(String jwt) {
        this.jwt = jwt;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecureUser user = (SecureUser) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return 37 * username.hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SecureUser{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
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

    public boolean isAdmin() {
        return admin;
    }

    public String getJwt() {
        return jwt;
    }
}
