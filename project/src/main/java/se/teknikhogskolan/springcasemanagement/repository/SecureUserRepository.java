package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecureUser;

public interface SecureUserRepository extends CrudRepository<SecureUser, Long> {

    SecureUser findByUsername(String username);

    @SuppressWarnings("JpaQlInspection")
    @Query(value = "SELECT CASE  WHEN count(su)> 0 THEN true ELSE false END FROM SecureUser su where su.username = ?1")
    boolean exists(String username);

}