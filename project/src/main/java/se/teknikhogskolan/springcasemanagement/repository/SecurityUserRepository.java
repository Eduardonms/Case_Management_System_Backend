package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecureUser;

public interface SecurityUserRepository extends CrudRepository<SecureUser, Long> {

    @SuppressWarnings("JpaQlInspection")
    @Query("select u from SecurityUser u where KEY(u.tokensExpiration) = ?1")
    SecureUser findByToken(String token);

    SecureUser findByUsername(String username);

}