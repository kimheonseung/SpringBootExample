package com.devheon.springboot.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testEncode() {
        String password = "1111";
        String enPw = passwordEncoder.encode(password);
        System.out.println("encoded password : " + enPw);
        boolean matchResult = passwordEncoder.matches(password, enPw);
        System.out.println("match result : " + matchResult);
        /**
         * encoded password : $2a$10$31LySA/0AiX7BXY97GQVEO36GeIGWUU5e4wKlvkm16/qJqyWhTEAK
         * match result : true
         */
    }
}
