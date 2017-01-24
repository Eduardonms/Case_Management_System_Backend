package se.teknikhogskolan.springcasemanagement.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.Jwt;
import se.teknikhogskolan.springcasemanagement.model.SecureUser;
import se.teknikhogskolan.springcasemanagement.repository.SecurityUserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateSalt;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateToken;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashPassword;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashingIterations;

@Service
public class SecurityUserService {
//
//    private  final SecurityUserRepository repository;
//
//    @Autowired
//    public SecurityUserService(SecurityUserRepository securityUserRepository) {
//        this.repository = securityUserRepository;
//    }
//
//    /** @return id */
//    public Long create(String username, String password) throws IllegalArgumentException {
//        if (null == username || username.isEmpty()) throw new IllegalArgumentException("Username must have actual value");
//        if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password must have actual value");
//        if (usernameIsTaken(username)) throw new IllegalArgumentException(String.format(
//                "Username '%s' already exist", username));
//
//        String salt = generateSalt();
//        String hashedPassword = hashPassword(password, salt);
//
//        SecureUser user = repository.save(new SecureUser(username, new HashMap<>(), hashedPassword, salt, hashingIterations));
//
//        return user.getId();
//    }
//
//    private boolean usernameIsTaken(String username) {
//        return !usernameIsAvailable(username);
//    }
//
//    public boolean usernameIsAvailable(String username) {
//        return null == repository.findByUsername(username);
//    }
//
//    public String createJwtFor(String username, String password) {
//        SecureUser user = getByUsername(username);
//
//        if (passwordMatchesUser(password, user)) {
//            String secret = generateToken(255);
//            Jwt jwt = new Jwt();
//            return secret;
//        } else throw new NotAuthorizedException("Wrong password");
//
//    }
//
//    public String createTokenFor(String username, String password) {
//        SecureUser user = getByUsername(username);
//
//        if (passwordMatchesUser(password, user)) {
//            String token = generateToken(255);
//            user.addToken(token, LocalDateTime.now().plusDays(1));
//            repository.save(user);
//            return token;
//        } else throw new NotAuthorizedException("Wrong password");
//    }
//
//    private SecureUser getByUsername(String username) {
//
//        Optional<SecureUser> user = Optional.ofNullable(repository.findByUsername(username));
//        if (!user.isPresent()) throw new NotFoundException(String.format("No such User '%s'", username));
//
//        return removeExpiredTokens(user.get());
//    }
//
//    private boolean passwordMatchesUser(String password, SecureUser user) {
//        if (equalPasswords(password, user.getSalt(), user.getHashedPassword())) {
//            return true;
//        } else return false;
//    }
//
//    private SecureUser removeExpiredTokens(SecureUser user) {
//        LocalDateTime now = LocalDateTime.now();
//        user.getTokensExpiration().forEach((token, expirationDate) -> {
//            if (now.isAfter(LocalDateTime.parse(expirationDate))) user.getTokensExpiration().remove(token);
//        });
//        return repository.save(user);
//    }
//
//    private boolean equalPasswords(String password, String salt, String hashedPassword) {
//        return hashedPassword.equals(hashPassword(password, salt));
//    }
//
//    public Long delete(String username, String password) {
//        SecureUser user = getByUsername(username);
//        if (passwordMatchesUser(password, user)) {
//            repository.delete(user.getId());
//            return user.getId();
//        } else throw new NotAuthorizedException("Wrong password");
//    }
//
//    private SecureUser getByToken(String token) {
//        SecureUser user = repository.findByToken(token); // Mysql is NOT case sensitive
//        if (null == user)  throw new NotAuthorizedException("Not authorized");
//        if (tokenIsExpired(token, user)) throw new NotAuthorizedException("Login session expired");
//        user = removeExpiredTokens(user);
//        if (!user.getTokensExpiration().containsKey(token)) throw new NotAuthorizedException("Not authorized");
//        return user;
//    }
//
//    public void verify(String token) {
//        SecureUser user = repository.findByToken(token); // Mysql is NOT case sensitive
//		if (null == user || !user.getTokensExpiration().containsKey(token)){
//			throw new NotAuthorizedException("Not authorized");
//		}
//		user = removeExpiredTokens(user);
//		if (user.getTokensExpiration() == null || !user.getTokensExpiration().containsKey(token)){
//			throw new NotAuthorizedException("Login session has expired");
//		}
//    }
//
//    private boolean tokenIsExpired(String token, SecureUser user) {
//        return LocalDateTime.parse(user.getTokensExpiration().get(token)).isBefore(LocalDateTime.now());
//    }
//
//    public LocalDateTime getExpiration(String token) {
//        SecureUser user = getByToken(token);
//        return LocalDateTime.parse(user.getTokensExpiration().get(token));
//    }
//
//    /** @return new expiration time, after updated in system */
//    public LocalDateTime renewExpiration(String token) {
//        SecureUser user = getByToken(token);
//
//        LocalDateTime renewal = LocalDateTime.now().plusDays(1L);
//        user.getTokensExpiration().replace(token, renewal.toString());
//        repository.save(user);
//
//        return renewal;
//    }
}