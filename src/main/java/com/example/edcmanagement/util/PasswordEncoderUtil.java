package com.example.edcmanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt encoded passwords
 * Run this as main method to generate encoded passwords
 */
public class PasswordEncoderUtil {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate encoded passwords
        String plainPassword1 = "admin123";
        String plainPassword2 = "password";
        String plainPassword3 = "admin";
        
        String encoded1 = encoder.encode(plainPassword1);
        String encoded2 = encoder.encode(plainPassword2);
        String encoded3 = encoder.encode(plainPassword3);
        
        System.out.println("=== BCrypt Password Generator ===");
        System.out.println();
        System.out.println("Plain: " + plainPassword1);
        System.out.println("BCrypt: " + encoded1);
        System.out.println();
        System.out.println("Plain: " + plainPassword2);
        System.out.println("BCrypt: " + encoded2);
        System.out.println();
        System.out.println("Plain: " + plainPassword3);
        System.out.println("BCrypt: " + encoded3);
        System.out.println();
        System.out.println("=== Application Properties Example ===");
        System.out.println("spring.security.user.password=" + encoded1);
        System.out.println();
        
        // Test encoding
        System.out.println("=== Verification ===");
        System.out.println("Password 'admin123' matches: " + encoder.matches("admin123", encoded1));
        System.out.println("Password 'wrong' matches: " + encoder.matches("wrong", encoded1));
    }
}