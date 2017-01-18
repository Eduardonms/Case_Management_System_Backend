package se.teknikhogskolan.springcasemanagement.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.teknikhogskolan.springcasemanagement.config.h2.H2InfrastructureConfig;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.service.SecurityUserService;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2InfrastructureConfig.class })
public class TestSecurityUserIntegration {

    @Autowired
    private SecurityUserService service;

    @Test
    public void canCreate() {
        SecurityUser user = service.create("Batman", "backinblack");
        assertNotNull(user.getHashedPassword());
        assertNotNull(user.getSalt());
        assertNotNull(user.getTokensExpiration());
        assertTrue(user.getSaltingIterations() > 0);
        System.out.println(user);
    }
}