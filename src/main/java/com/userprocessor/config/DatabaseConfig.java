package com.userprocessor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DATABASE_URL:postgresql://postgres:VHNcMuVaXSkHWTbQTqYCBwewRMLvovwS@shinkansen.proxy.rlwy.net:44677/railway}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Creating DataSource with DATABASE_URL: {}", databaseUrl);
        
        try {
            URI dbUri = new URI(databaseUrl);
            
            String username = null;
            String password = null;
            
            if (dbUri.getUserInfo() != null) {
                String[] userInfo = dbUri.getUserInfo().split(":");
                username = userInfo[0];
                if (userInfo.length > 1) {
                    password = userInfo[1];
                }
            }
            
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                    dbUri.getHost(),
                    dbUri.getPort(),
                    dbUri.getPath());
            
            logger.info("Parsed JDBC URL: {}", jdbcUrl);
            logger.info("Username: {}", username);
            
            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
        } catch (Exception e) {
            logger.error("Failed to parse DATABASE_URL, falling back to H2: {}", e.getMessage());
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:testdb")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }
    }
}
