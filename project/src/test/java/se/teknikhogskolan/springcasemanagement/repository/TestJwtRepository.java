package se.teknikhogskolan.springcasemanagement.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.repository.CrudRepository;
import se.teknikhogskolan.springcasemanagement.model.Jwt;
import se.teknikhogskolan.springcasemanagement.model.Team;
import se.teknikhogskolan.springcasemanagement.model.exception.ModelException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class TestJwtRepository {

    private final String projectPackage = "se.teknikhogskolan.springcasemanagement";

    @Test
    public void canGenerateJwtString() throws ModelException {
        LocalDateTime now = LocalDateTime.now();
        Jwt jwt = new Jwt("This test", "userId", "username", "token", now, now.plusDays(1L));
        execute(repository -> repository.save(jwt));
        Jwt result = execute(repo -> repo.findByUsername(jwt.getUsername()));
        assertNotNull(result.generateJWT());
        executeVoid(repo -> repo.delete(result));
    }

    @Test
    public void canPersistJwt() {
        LocalDateTime now = LocalDateTime.now();
        Jwt jwt = new Jwt("This test", "userId", "username", "token", now, now.plusDays(1L));
        execute(repository -> repository.save(jwt));
        Jwt result = execute(repo -> repo.findByUsername(jwt.getUsername()));
        assertNotNull(result.getJti());
        executeVoid(repo -> repo.delete(result));
    }

    private Jwt execute(Function<JwtRepository, Jwt> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(projectPackage);
            context.refresh();
            JwtRepository repository = context.getBean(JwtRepository.class);
            return operation.apply(repository);
        }
    }

    private void executeVoid(Consumer<JwtRepository> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(projectPackage);
            context.refresh();
            JwtRepository repository = context.getBean(JwtRepository.class);
            operation.accept(repository);
        }
    }
}