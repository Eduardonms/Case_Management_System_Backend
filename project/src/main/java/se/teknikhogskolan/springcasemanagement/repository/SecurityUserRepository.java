package se.teknikhogskolan.springcasemanagement.repository;

import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;

public interface SecurityUserRepository extends CrudRepository<SecurityUser, Long> {
}
