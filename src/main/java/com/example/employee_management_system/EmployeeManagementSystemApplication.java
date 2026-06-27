package com.example.employee_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Main entry point for the Employee Management System application.
 * 
 * <p>This Spring Boot application provides a complete employee management solution
 * with features including:</p>
 * <ul>
 *   <li>Employee CRUD operations with CSV import/export</li>
 *   <li>User management with role-based access control</li>
 *   <li>Leave request management with approval workflow</li>
 *   <li>Email verification and password reset functionality</li>
 *   <li>Caching with Ehcache for performance optimization</li>
 *   <li>Monitoring and metrics via Spring Boot Actuator</li>
 * </ul>
 * 
 * <p>The application uses:</p>
 * <ul>
 *   <li>Spring Security for authentication and authorization</li>
 *   <li>JPA/Hibernate for database persistence</li>
 *   <li>Thymeleaf for server-side rendering</li>
 *   <li>Swagger/OpenAPI for API documentation</li>
 * </ul>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableJpaAuditing
public class EmployeeManagementSystemApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 * 
	 * <p>Before starting the application, ensures the mime.types file exists
	 * in the user's home directory for proper email handling with JavaMail.</p>
	 * 
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementSystemApplication.class, args);
	}
}
