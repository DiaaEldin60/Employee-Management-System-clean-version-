package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setUserName("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);
    }

    @Test
    void testSaveUser() {
        Users saved = userRepository.save(testUser);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUserName());
    }

    @Test
    void testFindByUserName_Success() {
        entityManager.persist(testUser);

        Optional<Users> found = userRepository.findByUserName("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUserName());
    }

    @Test
    void testFindByUserName_NotFound() {
        Optional<Users> found = userRepository.findByUserName("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_Success() {
        Users saved = entityManager.persist(testUser);

        Optional<Users> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUserName());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Users> found = userRepository.findById(999);

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteUser() {
        Users saved = entityManager.persist(testUser);

        userRepository.deleteById(saved.getId());

        Optional<Users> found = userRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
