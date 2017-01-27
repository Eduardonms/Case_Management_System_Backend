package se.teknikhogskolan.springcasemanagement.system;

import java.security.KeyStoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.teknikhogskolan.springcasemanagement.config.h2.H2InfrastructureConfig;
import se.teknikhogskolan.springcasemanagement.security.exception.EncodingException;
import se.teknikhogskolan.springcasemanagement.service.SecureUserService;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2InfrastructureConfig.class })
public class TestSecureUserIntegration {

    private String username = "Batman";
    private String password = "backinblack";
    private Long userId;

    @Autowired
    private SecureUserService service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        userId = service.create(username, password);
    }

    @After
    public void teardown() {
       service.delete(username, password);
    }

    @Test
    public void canCreateJwt() throws EncodingException, KeyStoreException {
        String result = service.createJwtFor(username, password);
        System.out.println(result);
    }

    @Test
    public void canValidateUsernameAndPassword() {
        assertTrue(service.isValid(username, password));
        assertFalse(service.isValid("fake user", password));
        assertFalse(service.isValid(username, "fake password"));
    }

    @Test
    public void usernameMustBeUnique() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username 'Batman' already exist");
        service.create(username, password);
    }


    @Test
    public void deletingUserReturnsId() {
        String username = "The Joker";
        String password = "burnitalldown";
        Long newUserId = service.create(username, password);
        assertNotNull(newUserId);
        Long removedUserId = service.delete(username, password);
        assertEquals(newUserId, removedUserId);
    }

    @Test
    public void canCheckUsernameIsAvailable() {
        assertFalse(service.usernameIsAvailable(username));
        String antiUsername = "sa98fhs9ah9sdfvh9sodfhv0dfjv9wdfv";
        assertTrue(service.usernameIsAvailable(antiUsername));
    }

}