package se.teknikhogskolan.springcasemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;
import org.junit.Test;
import se.teknikhogskolan.springcasemanagement.service.SecurityHelper;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MiscTests {

    @Test
    public void getPathToConfigFile() {
        System.out.println(System.getProperty("user.home") + "/spring-case-management.properties");
    }

    @Test
    public void stringIsImmutable() {
        final String hello = "hello world";
        hello.substring(0, 5);
        System.out.println(hello);
//        hello = ""; // cant even compile
    }

    @Test
    public void howToReadPayLoad() {
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOiIxNDg1MzM3NTIwIiwidXNlcm5hbWUiOiJCYXRtYW4ifQ==.3XuAppCeVYhIqO9hVy0FsF44Gqk3YxxHrsRibaIRnW0=";
        String[] parts = jwt.split("\\.");
        String payload = new String(Base64.getDecoder().decode(parts[1]));
        System.out.println(payload);
        JSONObject object = new JSONObject(payload);
        System.out.println("exp: " + object.get("exp"));
    }

    @Test
    public void stringArrayLengthIsParts() {
        String s = "sadfsg.sdgsdfgsdfg.sdfgdsfg";
        String[] parts = s.split("\\.");
        assertEquals(3, parts.length);
        assertNotNull(parts[0]);
        assertNotNull(parts[1]);
        assertNotNull(parts[2]);
    }

    @Test
    public void mapReturnsNullIfMissingKey() {
        Map<String, String> map = new HashMap<>();
        assertNull(map.get("mumbo jumbo"));
    }

    @Test
    public void writeAndLoadKeys() throws IOException {
        File file = new File("spring-case-managenet.properties");

        Properties properties = new Properties();
        properties.put("jwt_key", "secret");
        properties.store(new FileOutputStream(file), null);

        Properties result = new Properties();
        result.load(new FileInputStream(file));

        assertTrue(result.get("jwt_key").equals("secret"));

        file.delete();
    }

    @Test
    public void generateRandomString() {
        System.out.println(SecurityHelper.generateToken(255));
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
