package se.teknikhogskolan.springcasemanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.repository.SecurityUserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateSalt;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashPassword;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashingIterations;

@Service
public class SecurityUserService {

    private  final SecurityUserRepository securityUserRepository;

    @Autowired
    public SecurityUserService(SecurityUserRepository securityUserRepository) {
        this.securityUserRepository = securityUserRepository;
    }

    public SecurityUser create(String username, String password) throws IllegalArgumentException {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        if (null == password) throw new IllegalArgumentException("Password must not be null");
        if (null != securityUserRepository.findByUsername(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' already exist", username));

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        Map<String, String> tokens = new HashMap<>();
        tokens.put(SecurityHelper.generateToken(254), LocalDateTime.now().plusDays(1L).toString());

        SecurityUser user = new SecurityUser(username, tokens, hashedPassword, salt, hashingIterations);

        return securityUserRepository.save(user);
    }

    public boolean isAuthorized(String username, String password) throws NotFoundException {

        Optional<SecurityUser> user = Optional.ofNullable(securityUserRepository.findByUsername(username));
        if (!user.isPresent()) throw new NotFoundException(String.format("Cannot find User '%s'", username));

        String hashedPassword = user.get().getHashedPassword();
        String salt = user.get().getSalt();
        return equalPasswords(password, salt, hashedPassword);
    }

    private boolean equalPasswords(String password, String salt, String hashedPassword) {
        return hashedPassword.equals(hashPassword(password, salt));
    }

    public SecurityUser getById(Long id) {
        return securityUserRepository.findOne(id);
    }

    public SecurityUser getByToken(String token) {
        return securityUserRepository.findByToken(token);
    }

    public SecurityUser getByUsername(String username) {
        return securityUserRepository.findByUsername(username);
    }

    public String createSecurityUserToken(Long id, LocalDate expirationDate, LocalTime expirationTime) {
        String date = expirationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = expirationTime.format(DateTimeFormatter.ofPattern("HHmm"));
        return null;
    }

}