package com.example.edcmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Value("${spring.security.user.name:admin}")
    private String adminUsername;
    
    @Value("${spring.security.user.password:admin123}")
    private String adminPassword;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Configure in-memory authentication with NoOp encoder for simplicity
        auth.inMemoryAuthentication()
            .withUser(adminUsername)
            .password(adminPassword)
            .roles("ADMIN");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Allow echo endpoint without authentication (signature validation handles security)
                .antMatchers(HttpMethod.POST, "/edc/echo").permitAll()
                // Allow test endpoints without authentication for development
                .antMatchers("/test/**").permitAll()
                // Allow health check endpoints
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/health").permitAll()
                // Require authentication for all other endpoints
                .antMatchers(HttpMethod.GET, "/edc/terminals/**").authenticated()
                .antMatchers(HttpMethod.POST, "/edc/terminals/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/edc/terminals/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/edc/terminals/**").authenticated()
                .antMatchers(HttpMethod.GET, "/edc/echo-logs/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .headers()
                .frameOptions().deny()
                .contentTypeOptions()
                .and()
                .xssProtection()
                .and()
                .httpStrictTransportSecurity()
                    .includeSubDomains(true)  // ✅ versi Spring Security 5.x
                    .maxAgeInSeconds(31536000);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use NoOpPasswordEncoder for development (plain text passwords)
        // For production, use BCryptPasswordEncoder with encoded passwords
        return NoOpPasswordEncoder.getInstance();
    }
    
    /* 
    // Alternative: Use BCrypt with pre-encoded password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // If using BCrypt, update application.properties with encoded password:
    // spring.security.user.password=$2a$10$your_bcrypt_encoded_password_here
    */
}