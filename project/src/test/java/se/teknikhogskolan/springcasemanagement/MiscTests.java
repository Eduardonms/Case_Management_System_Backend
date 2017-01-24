package se.teknikhogskolan.springcasemanagement;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;

public class MiscTests {

    @Test
    public void mapToJson() {
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "Me, the tester");
        claims.put("sub", "map to json");
        System.out.println(claims);
        JSONObject object = new JSONObject(claims);
        System.out.println(object);
    }
}
