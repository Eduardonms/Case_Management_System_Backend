package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;

public interface SecurityUserRepository extends CrudRepository<SecurityUser, Long> {

    @SuppressWarnings("JpaQlInspection")
    @Query("select u from SecurityUser u where KEY(u.tokensExpiration) = ?1")
    SecurityUser findByToken(String token);

    SecurityUser findByUsername(String username);

    @SuppressWarnings("JpaQlInspection")
    @Deprecated /** returns all values from user with token, should return specific value(string date) matching key(token) */
    @Query("select VALUE(u.tokensExpiration) from SecurityUser u where KEY(u.tokensExpiration) = ?1")
    String getTokenExpiration(String token);

}