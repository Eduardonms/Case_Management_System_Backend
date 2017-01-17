package se.teknikhogskolan.springcasemanagement.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;

import static org.junit.Assert.assertNotNull;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateToken;

public final class TestSecurityUserRepository {
    private static final String PROJECT_PACKAGE = "se.teknikhogskolan.springcasemanagement";
    private static SecurityUser user;


    @BeforeClass
    public static void masterSetup() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(PROJECT_PACKAGE);
            context.refresh();

            user = context.getBean(SecurityUserRepository.class).save(new SecurityUser(""));
        }
    }

    @AfterClass
    public static void masterTearDown() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(PROJECT_PACKAGE);
            context.refresh();
            SecurityUserRepository securityUserRepository = context.getBean(SecurityUserRepository.class);
            securityUserRepository.delete(user.getId());
        }
    }

    @Test
    public void canCreateAndAddTokenAndFindByToken() {

        final SecurityUser batman = executeOne(repo -> repo.save(new SecurityUser("Batman")));
        System.out.println(batman);

        String token = generateToken(255);
        LocalDateTime expireTime = LocalDateTime.now().plusDays(1L);
        batman.addToken(token, expireTime);
        SecurityUser batmanWithToken = executeOne(repo -> repo.save(batman));
        System.out.println(batmanWithToken);

        SecurityUser result = executeOne(repo -> repo.findByToken(token));
        System.out.println(result);
    }

    @Test
    public void canGetSecurityUser() {
        Long userIdInDb = user.getId();

        SecurityUser result = executeOne(repo -> repo.findOne(userIdInDb));

        assertNotNull(result);
    }


    private void executeVoid(Consumer<SecurityUserRepository> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(PROJECT_PACKAGE);
            context.refresh();
            SecurityUserRepository securityUserRepository = context.getBean(SecurityUserRepository.class);
            operation.accept(securityUserRepository);
        }
    }

    private SecurityUser executeOne(Function<SecurityUserRepository, SecurityUser> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(PROJECT_PACKAGE);
            context.refresh();
            SecurityUserRepository securityUserRepository = context.getBean(SecurityUserRepository.class);
            return operation.apply(securityUserRepository);
        }
    }

    private Collection<SecurityUser> executeMany(Function<SecurityUserRepository, Collection<SecurityUser>> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(PROJECT_PACKAGE);
            context.refresh();
            SecurityUserRepository securityUserRepository = context.getBean(SecurityUserRepository.class);
            return operation.apply(securityUserRepository);
        }
    }
}