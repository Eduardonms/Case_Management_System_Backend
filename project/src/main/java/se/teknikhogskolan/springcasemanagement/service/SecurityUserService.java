package se.teknikhogskolan.springcasemanagement.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.repository.SecurityUserRepository;

@Service
public class SecurityUserService {
    private  final SecurityUserRepository securityUserRepository;

    @Autowired
    public SecurityUserService(SecurityUserRepository securityUserRepository) {
        this.securityUserRepository = securityUserRepository;
    }

    public Optional<SecurityUser> create() {
        return null; // TODO create
    }

    public Optional<SecurityUser> get() {
        return null; // TODO get
    }
}
