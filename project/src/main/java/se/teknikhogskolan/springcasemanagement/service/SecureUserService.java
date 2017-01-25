package se.teknikhogskolan.springcasemanagement.service;

import java.security.KeyStoreException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.JwtBuilder;
import se.teknikhogskolan.springcasemanagement.model.SecureUser;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;
import se.teknikhogskolan.springcasemanagement.repository.SecureUserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateSalt;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashPassword;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashingIterations;

@Service
public class SecureUserService {

    private  final SecureUserRepository repository;

    @Autowired
    public SecureUserService(SecureUserRepository securityUserRepository) {
        this.repository = securityUserRepository;
    }

    /** @return id */
    public Long create(String username, String password) throws IllegalArgumentException {
        if (null == username || username.isEmpty()) throw new IllegalArgumentException("Username must have actual value");
        if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password must have actual value");
        if (usernameIsTaken(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' already exist", username));

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        SecureUser user = repository.save(new SecureUser(username, hashedPassword, salt, hashingIterations));

        return user.getId();
    }

    private boolean usernameIsTaken(String username) {
        return !usernameIsAvailable(username);
    }

    public boolean usernameIsAvailable(String username) {
        return null == repository.findByUsername(username);
    }

    public String createJwtFor(String username, String password) throws KeyStoreException, EncodingException {
        SecureUser user = getByUsername(username);

        if (passwordMatchesUser(password, user)) {

            JwtBuilder jwtBuilder = new JwtBuilder();
            jwtBuilder.putClaim("username", username);
            jwtBuilder.putClaim("exp", String.valueOf(getCurrentUnixTime() + (60)));

            return jwtBuilder.build();

        } else throw new NotAuthorizedException("Wrong password");

    }

    private Long getCurrentUnixTime() {
        return Instant.now().getEpochSecond();
    }

    private SecureUser getByUsername(String username) {

        Optional<SecureUser> user = Optional.ofNullable(repository.findByUsername(username));
        if (!user.isPresent()) throw new NotFoundException(String.format("No such User '%s'", username));

        return user.get();
    }

    private boolean passwordMatchesUser(String password, SecureUser user) {
        if (equalPasswords(password, user.getSalt(), user.getHashedPassword())) {
            return true;
        } else return false;
    }

    private boolean equalPasswords(String password, String salt, String hashedPassword) {
        return hashedPassword.equals(hashPassword(password, salt));
    }

    public Long delete(String username, String password) {
        SecureUser user = getByUsername(username);
        if (passwordMatchesUser(password, user)) {
            repository.delete(user.getId());
            return user.getId();
        } else throw new NotAuthorizedException("Wrong password");
    }
}