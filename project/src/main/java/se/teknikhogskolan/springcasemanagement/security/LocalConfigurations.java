package se.teknikhogskolan.springcasemanagement.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

public final class LocalConfigurations {

    private final String secret = "jwt_key";
    private final File configFile = new File(System.getProperty("user.home") + "/spring-case-management.properties");

    public final String getUsername() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        return properties.getProperty("mysql_username");
    }

    public final String getPassword() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        return properties.getProperty("mysql_password");
    }

    public final String getJdbcUrl() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        return properties.getProperty("jdbc_url");
    }

    public final String getSecret() throws IOException {
        Properties properties = new Properties();

        properties.load(new FileInputStream(configFile));

        if (null == properties.getProperty(secret)) {
            properties = persistNewSecret(properties);
        }

        return properties.getProperty(secret);
    }

    private Properties persistNewSecret(Properties properties) throws IOException {

        properties.put(secret, generateToken(255));

        properties.store(new FileOutputStream(configFile), null);

        return properties;
    }

    private final String generateToken(int length) {
        final StringBuilder builder = new StringBuilder();
        final SecureRandom random = new SecureRandom();
        final String characters = "0123456789abcdfghijklmopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWXYZ";
        for (int i = 0; i < length; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }
}
