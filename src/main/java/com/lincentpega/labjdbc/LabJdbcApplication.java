package com.lincentpega.labjdbc;

import com.lincentpega.labjdbc.dao.UserDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LabJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabJdbcApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserDAO userDAO) {
        return args -> {
            userDAO.setDatabase("penis");
        };
    }

}
