package com.mightyjava.controller;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.mightyjava.model.Users;
import com.mightyjava.service.UserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null && !error.isEmpty())
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "user/login";
    }

    @GetMapping("/form")
    public String userForm(Model model) {
        model.addAttribute("isNew", true);
        model.addAttribute("userForm", new Users());
        model.addAttribute("roles", userService.roleList());
        return "user/form";
    }

    @GetMapping("/edit/{id}")
    public String userOne(@PathVariable Long id, Model model) {
        model.addAttribute("isNew", false);
        model.addAttribute("userForm", userService.findOne(id));
        model.addAttribute("roles", userService.roleList());
        return "user/form";
    }

    @DeleteMapping(value = "/delete/{id}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> userDelete(@PathVariable Long id) {
        String result = userService.deleteUser(id);
        if ("success".equalsIgnoreCase(result)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> userAdd(@Valid @RequestBody Users user, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            return ResponseEntity.badRequest().body(errors);
        } else {
            Users savedUser = userService.addUser(user);
            return ResponseEntity.ok(savedUser);
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> findOne(@PathVariable Long id) {
        Users user = userService.findOne(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String userList(Model model) {
        model.addAttribute("users", userService.userList());
        return "/user/list";
    }
}
