package se.teknikhogskolan.springcasemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.json.JSONObject;
import org.junit.Test;
import se.teknikhogskolan.springcasemanagement.service.SecurityHelper;

import static org.junit.Assert.assertTrue;

public class MiscTests {

    @Test
    public void writeAndLoadKeys() throws IOException {
        String filename = "spring-case-managenet.properties";

        Properties properties = new Properties();
        properties.put("jwt_key", "secret");
        properties.store(new FileOutputStream(filename), null);

        Properties result = new Properties();
        result.load(new FileInputStream(filename));

        assertTrue(result.get("jwt_key").equals("secret"));

        new File(filename).delete();
    }

    @Test
    public void generateRandomString() {
        System.out.println(SecurityHelper.generateToken(255));
    }

    @Test
    public void jose4j() throws JoseException {

        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "Me, the tester");
        claims.put("sub", "map to json");
        claims.put("exp", String.valueOf(Instant.now().getEpochSecond()));
        JSONObject object = new JSONObject(claims);

        Key key = new AesKey(ByteUtil.randomBytes(16));
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setPayload(object.toString());
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        String serializedJwe = jwe.getCompactSerialization();
        System.out.println("Serialized Encrypted JWE: " + serializedJwe);

        jwe = new JsonWebEncryption();
        jwe.setKey(key);
        jwe.setCompactSerialization(serializedJwe);
        System.out.println("Payload: " + jwe.getPayload());

    }

    @Test
    public void mapToJson() {
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "Me, the tester");
        claims.put("sub", "map to json");
        System.out.println(claims);
        JSONObject object = new JSONObject(claims);
        System.out.println(object);
    }

    @Test
    public void unixTimeNow() {
        System.out.println("Seconds since Jan 01 1970. (UTC): " + Instant.now().getEpochSecond());
    }
}
