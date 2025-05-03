package com.example.productmgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class ProductMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductMgrApplication.class, args);
    }
}