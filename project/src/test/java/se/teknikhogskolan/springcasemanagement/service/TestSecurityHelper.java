package se.teknikhogskolan.springcasemanagement.service;

import java.security.KeyStoreException;
import org.junit.Test;
import se.teknikhogskolan.springcasemanagement.model.exception.EncodingException;

public class TestSecurityHelper {

    @Test
    public void readJwtSecretFromFile() throws EncodingException, KeyStoreException {
        System.out.println(SecurityHelper.getSecret());
    }
}
