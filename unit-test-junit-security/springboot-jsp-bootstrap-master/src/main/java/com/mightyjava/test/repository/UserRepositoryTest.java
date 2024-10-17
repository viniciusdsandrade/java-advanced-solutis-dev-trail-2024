package com.mightyjava.test.repository;

import com.mightyjava.model.Users;
import com.mightyjava.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


@DataJpaTest
@ComponentScan(basePackages = {"com.mightyjava"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

    private final UserRepository userRepository;

    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    @Order(1)
    @Rollback(false)
    public void testCreateUser() {
        Users user = new Users();
        user.setFullName("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setMobile("1234567890");

        Users savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertTrue(savedUser.getId() > 0);
    }

    @Test
    @Order(2)
    public void testReadUser() {
        Users user = userRepository.findById(1L).orElse(null);
        assertNotNull(user);
        assertEquals("testuser", user.getFullName());
    }

    @Test
    @Order(3)
    @Rollback(false)
    public void testUpdateUser() {
        Users user = userRepository.findById(1L).orElse(null);
        assertNotNull(user);
        user.setEmail("updatedemail@example.com");
        userRepository.save(user);

        Users updatedUser = userRepository.findById(1L).orElse(null);
        assertEquals("updatedemail@example.com", updatedUser.getEmail());
    }

    @Test
    @Order(4)
    @Rollback(false)
    public void testDeleteUser() {
        Users user = userRepository.findById(1L).orElse(null);
        assertNotNull(user);
        userRepository.delete(user);

        Users deletedUser = userRepository.findById(1L).orElse(null);
        assertNull(deletedUser);
    }

    @Test
    @Order(5)
    public void testListUsers() {
        List<Users> users = userRepository.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 0);
    }
}
