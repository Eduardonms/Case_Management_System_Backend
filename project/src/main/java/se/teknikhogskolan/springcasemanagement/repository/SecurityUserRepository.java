package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;

public interface SecurityUserRepository extends CrudRepository<SecurityUser, Long> {

    @SuppressWarnings("JpaQlInspection")
    @Query("select u from SecurityUser u where KEY(u.tokensExpiration) = ?1")
    SecurityUser findByToken(String token);

    SecurityUser findByUsername(String username);

}