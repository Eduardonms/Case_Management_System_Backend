package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecureUser;

public interface SecureUserRepository extends CrudRepository<SecureUser, Long> {

    SecureUser findByJwt(String jwt);

    SecureUser findByUsername(String username);

}