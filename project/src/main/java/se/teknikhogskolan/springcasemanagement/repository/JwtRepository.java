package se.teknikhogskolan.springcasemanagement.repository;

import java.time.LocalDateTime;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.Jwt;

public interface JwtRepository extends CrudRepository<Jwt, Long> {

   Jwt findByUsername(String username);

   Jwt findByIss(String issuer);

   Jwt findBySub(String subject);

   Jwt findByIat(LocalDateTime created);

   Jwt findByExp(LocalDateTime expires);
}