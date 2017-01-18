package se.teknikhogskolan.springcasemanagement.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.repository.SecurityUserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateSalt;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateToken;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashPassword;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashingIterations;

@Service
public class SecurityUserService {

    private  final SecurityUserRepository securityUserRepository;

    @Autowired
    public SecurityUserService(SecurityUserRepository securityUserRepository) {
        this.securityUserRepository = securityUserRepository;
    }

    /** @return id */
    public Long create(String username, String password) throws IllegalArgumentException {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        if (null == password) throw new IllegalArgumentException("Password must not be null");
        if (null != securityUserRepository.findByUsername(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' already exist", username));

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        Map<String, String> tokens = new HashMap<>();
        tokens.put(generateToken(254), LocalDateTime.now().plusDays(1L).toString());

        SecurityUser user = securityUserRepository.save(new SecurityUser(username, tokens, hashedPassword, salt, hashingIterations));

        return user.getId();
    }

    public String createTokenFor(String username, String password) {
        SecurityUser user = getByUsername(username);

        if (passwordMatchesUser(password, user)) {
            String token = generateToken(255);
            user.addToken(token, LocalDateTime.now().plusDays(1));
            securityUserRepository.save(user);
            return token;
        } else throw new NotAuthorizedException("Wrong password");
    }

    public SecurityUser getByUsername(String username) {

        Optional<SecurityUser> user = Optional.ofNullable(securityUserRepository.findByUsername(username));
        if (!user.isPresent()) throw new NotFoundException(String.format("No such User '%s'", username));

        return removeExpiredTokens(user.get());
    }

    private boolean passwordMatchesUser(String password, SecurityUser user) {
        if (equalPasswords(password, user.getSalt(), user.getHashedPassword())) {
            return true;
        } else return false;
    }

    private SecurityUser removeExpiredTokens(SecurityUser user) {
        LocalDateTime now = LocalDateTime.now();
        user.getTokensExpiration().forEach((token, expirationDate) -> {
            if (now.isAfter(LocalDateTime.parse(expirationDate))) user.getTokensExpiration().remove(token);
        });
        return securityUserRepository.save(user);
    }

    private boolean equalPasswords(String password, String salt, String hashedPassword) {
        return hashedPassword.equals(hashPassword(password, salt));
    }

    public SecurityUser getById(Long id) {

        Optional<SecurityUser> user = Optional.ofNullable(securityUserRepository.findOne(id));
        if (!user.isPresent()) throw new NotFoundException(String.format("No such User '%s'", id));

        return removeExpiredTokens(user.get());
    }

    public SecurityUser delete(Long id) {
        SecurityUser user = getById(id);
        securityUserRepository.delete(id);
        return user;
    }

    public SecurityUser getByToken(String token) {
        return securityUserRepository.findByToken(token);
    }

}