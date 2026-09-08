package com.example.ilms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IlmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlmsApplication.class, args);
    }
    
}
