package com.lincentpega.labjdbc.config;

import com.lincentpega.labjdbc.dao.DBUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.*;

@Configuration
public class DefaultDBConfig {

    @Value("${database.default.url}")
    private String url;
    @Value("${database.default.username}")
    private String username;
    @Value("${database.default.password}")
    private String password;

    @Bean
    DataSource defaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    Connection defaultConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Bean
    CommandLineRunner initDb() {
        return args -> {
            Connection connection = defaultConnection();
            Statement statement = connection.createStatement();

            String initScript = DBUtils.readInitScript();
            statement.execute(initScript);
        };
    }
}
