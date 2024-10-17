package com.mightyjava.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mightyjava.controller.UserController;
import com.mightyjava.model.Users;
import com.mightyjava.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUserList() throws Exception {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setFullName("user1");
        user1.setFullName("User One");
        user1.setEmail("user1@example.com");
        user1.setMobile("1234567890");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setFullName("user2");
        user2.setFullName("User Two");
        user2.setEmail("user2@example.com");
        user2.setMobile("0987654321");

        List<Users> userList = Arrays.asList(user1, user2);

        when(userService.userList()).thenReturn(userList);

        mockMvc.perform(get("/user/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("/user/list"));

        verify(userService, times(1)).userList();
    }

    @Test
    public void testGetUser() throws Exception {
        Users user = new Users();
        user.setId(1L);
        user.setFullName("testuser");
        user.setFullName("Test User");
        user.setEmail("testuser@example.com");
        user.setMobile("1234567890");

        when(userService.findOne(1L)).thenReturn(user);

        mockMvc.perform(get("/user/get/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService, times(1)).findOne(1L);
    }

    @Test
    public void testAddUser() throws Exception {
        Users user = new Users();
        user.setFullName("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setMobile("1234567890");

        when(userService.addUser(any(Users.class))).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService, times(1)).addUser(any(Users.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/user/delete/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}
