package se.teknikhogskolan.springcasemanagement.config.mysql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import se.teknikhogskolan.springcasemanagement.auditing.IssueAuditorAware;
import se.teknikhogskolan.springcasemanagement.config.JpaConfig;
import se.teknikhogskolan.springcasemanagement.security.ConfigurationReader;

@Configuration
@EnableJpaRepositories("se.teknikhogskolan.springcasemanagement.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class MysqlInfrastructureConfig extends JpaConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        return new IssueAuditorAware();
    }

    @Bean
    @Override
    public DataSource dataSource() throws IOException {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");

        ConfigurationReader configurationReader = new ConfigurationReader();
        config.setJdbcUrl(configurationReader.getJdbcUrl());
        config.setUsername(configurationReader.getUsername());
        config.setPassword(configurationReader.getPassword());

        return new HikariDataSource(config);
    }

    @Bean
    @Override
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setGenerateDdl(true);
        return adapter;
    }
}
