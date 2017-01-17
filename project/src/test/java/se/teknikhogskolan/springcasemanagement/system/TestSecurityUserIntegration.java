package se.teknikhogskolan.springcasemanagement.system;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.teknikhogskolan.springcasemanagement.config.h2.H2InfrastructureConfig;
import se.teknikhogskolan.springcasemanagement.config.hsql.HsqlInfrastructureConfig;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.model.Team;
import se.teknikhogskolan.springcasemanagement.model.User;
import se.teknikhogskolan.springcasemanagement.service.SecurityUserService;
import se.teknikhogskolan.springcasemanagement.service.TeamService;
import se.teknikhogskolan.springcasemanagement.service.UserService;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2InfrastructureConfig.class })
public class TestSecurityUserIntegration {

    @Autowired
    private SecurityUserService service;

    @Test
    public void canCreate() {
        Optional<SecurityUser> user = service.create("Batman", "backinblack");
        assertTrue(user.isPresent());
        assertNotNull(user.get().getHashedPassword());
        assertNotNull(user.get().getSalt());
        assertNotNull(user.get().getTokensExpiration());
        assertTrue(user.get().getSaltingIterations() > 0);
        System.out.println(user.get());
    }
}