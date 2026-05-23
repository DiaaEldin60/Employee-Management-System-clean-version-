package com.example.employee_management_system.config;

import com.example.employee_management_system.securityConfig.CustomAccessDeniedHandler;
import com.example.employee_management_system.securityConfig.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.TemplateEngine;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
@EnableWebSecurity
@ComponentScan(
    basePackages = "com.example.employee_management_system",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FlywayConfig.class)
    }
)
public class TestSecurityConfig {

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return mock(CustomAccessDeniedHandler.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TemplateEngine templateEngine() {
        return mock(TemplateEngine.class);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
