package se.teknikhogskolan.springcasemanagement.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.teknikhogskolan.springcasemanagement.config.h2.H2InfrastructureConfig;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.service.SecurityUserService;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2InfrastructureConfig.class })
public class TestSecurityUserIntegration {

    private String username = "Batman";
    private String password = "backinblack";
    private SecurityUser user;

    @Autowired
    private SecurityUserService service;

    @Before
    public void setup() {
        user = service.create(username, password);
    }

    @After
    public void teardown() {
        service.delete(user.getId());
    }

    @Test
    public void canGetUserByToken() {
        String token = service.createTokenFor(username, password);
        SecurityUser result = service.getByToken(token);
        assertEquals(user, result);
    }

    @Test
    public void canCreateToken() {
        String token = service.createTokenFor(username, password);
        SecurityUser user = service.getByUsername(username);
        assertTrue(user.getTokensExpiration().containsKey(token));
    }

    @Test
    public void canGetById() {
        SecurityUser result = service.getById(user.getId());
        assertNotNull(result);
        assertNotNull(result.getHashedPassword());
        assertNotNull(result.getSalt());
        assertNotNull(result.getTokensExpiration());
        assertTrue(result.getSaltingIterations() > 0);
    }

    @Test
    public void canGetByUsername() {
        SecurityUser result = service.getByUsername(user.getUsername());
        assertNotNull(result);
        assertNotNull(result.getHashedPassword());
        assertNotNull(result.getSalt());
        assertNotNull(result.getTokensExpiration());
        assertTrue(result.getSaltingIterations() > 0);
    }
}