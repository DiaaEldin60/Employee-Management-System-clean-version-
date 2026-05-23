package com.example.employee_management_system.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
/// by me
/// excludes test profile
@Profile("!test")
/// by me
/// @EnableWebSecurity enables Spring Security's web security support and provides the Spring MVC integration. It allows you to configure web-based security for specific HTTP requests. By adding this annotation, you can customize your security configuration by defining beans of type SecurityFilterChain, which will be used to secure your application based on the defined rules and filters.
/// @EnableMethodSecurit is used for
/// @EnableMethodSecurity(prePostEnabled = true), you can secure any method based on a user's roles or authorities without relying only on URL paths.

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    /// by me
    /// Used to hash/verify passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /// by me
    /// Used to authenticate users
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    /// by me
    /// Used to track active sessions
    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }
    /// by me
    /// Used to publish session events for concurrent session control
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Exclude actuator paths - they're handled by higher priority chains
                .securityMatcher(new org.springframework.security.web.util.matcher.RequestMatcher() {
                    @Override
                    public boolean matches(jakarta.servlet.http.HttpServletRequest request) {
                        return !request.getRequestURI().startsWith("/actuator");
                    }
                })
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/css/**", "/js/**", "/images/**") // disable CSRF for API endpoints and static files
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/forgot-password", "/complete-account", "/verify-email", "/verify-success", "/change-password", "/verify-email-step", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/signup", "/complete-account", "/verify-email", "/change-password", "/resend-verification", "/verify-email-step", "/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/login-success", true)
                        .permitAll()
                        .failureUrl("/login?error")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login?logout")
                        .clearAuthentication(true)
                )
                /// by me
                /// Session management is crucial for security. It prevents session fixation attacks by creating a new session on login, limits concurrent sessions to 1 per user, and ensures old sessions are invalidated on logout. This helps protect against unauthorized access and session hijacking.
                /// SessionCreationPolicy.IF_REQUIRED means a session will only be created if necessary (e.g., for form login). Session fixation protection is enabled by migrating the session on authentication. Maximum sessions is set to 1 to prevent multiple concurrent logins, and maxSessionsPreventsLogin is false to allow new logins while invalidating old sessions. The sessionRegistry bean tracks active sessions for enforcement.
                /// sessionFixation().migrateSession() is used to prevent session fixation attacks by migrating the session on authentication.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                )
                /// by me
                /// .headers(headers -> headers) starts configuring HTTP security response headers in Spring Security.
                /// .frameOptions(frameOptions -> frameOptions.deny()) adds the X-Frame-Options: DENY header.
                /// X-Frame-Options: DENY prevents the application from being embedded inside iframes.
                /// Preventing iframe embedding helps protect against clickjacking attacks.
                /// Clickjacking tricks users into clicking hidden UI elements inside malicious pages.
                /// .frameOptions().sameOrigin() allows iframe embedding only from the same domain.
                /// sameOrigin() is commonly used for H2 console or internal admin dashboards.
                /// .httpStrictTransportSecurity(...) configures the Strict-Transport-Security (HSTS) HTTP header.
                /// HSTS forces browsers to use HTTPS instead of HTTP for future requests.
                /// .maxAgeInSeconds(31536000) tells the browser to enforce HTTPS for 1 year.
                /// 31536000 seconds equals 365 days.
                /// .includeSubDomains(true) applies HTTPS enforcement to all subdomains.
                /// includeSubDomains(true) protects domains like api.example.com and admin.example.com.
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                )
                .build();
    }
    /**
     * Public actuator endpoints (health, info, prometheus, metrics) - no auth required
     * These are safe to expose for load balancers and monitoring tools
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain publicActuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/actuator/health", "/actuator/info", "/actuator/prometheus", "/actuator/metrics/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Other actuator endpoints - require ADMIN role
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain secureActuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
