package com.mightyjava.test.service;

import com.mightyjava.model.Users;
import com.mightyjava.repository.UserRepository;
import com.mightyjava.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService; // Certifique-se de que UserServiceImpl implementa UserService

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testAddUser() {
        Users user = new Users();
        user.setFullName("testuser"); // Corrigido para setUsername
        user.setPassword("testpassword");
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setMobile("1234567890");

        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users savedUser = userService.addUser(user);

        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getFullName()); // Ajustado para o valor correto
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testFindOne() {
        Users user = new Users();
        user.setId(1L);
        user.setFullName("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Users foundUser = userService.findOne(1L);

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getFullName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUserList() {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setFullName("user1");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setFullName("user2");

        List<Users> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<Users> users = userService.userList();

        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}