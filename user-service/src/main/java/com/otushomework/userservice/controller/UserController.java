package com.otushomework.userservice.controller;

import com.otushomework.userservice.entity.User;
import com.otushomework.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    private ResponseEntity<Map<String, Integer>> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", savedUser.getId());
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/{userId}")
    private ResponseEntity<User> getUser(@PathVariable int userId, @RequestHeader(value = "X-User-Id", required = false) String xUserId) {
        if (xUserId == null || xUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int authenticatedUserId = Integer.parseInt(xUserId);
        if (authenticatedUserId != userId) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> user = userService.getUserById(userId);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }




    @DeleteMapping("/{userId}")
    private ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        if (userService.getUserById(userId).isPresent()) {
            userService.deleteUserById(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{userId}")
    private ResponseEntity<User> updateUser(@PathVariable int userId, @RequestBody User userDetails, @RequestHeader(value = "X-User-Id", required = false) String xUserId) {
        System.out.println(xUserId);
        if (xUserId == null || xUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int authenticatedUserId = Integer.parseInt(xUserId);
        if (authenticatedUserId != userId) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhone(userDetails.getPhone());
            user.setEmail(userDetails.getEmail());
            userService.saveUser(user);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/error")
    private ResponseEntity<Void> generateError() {
        // Simulate a server error
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping
    public ResponseEntity<?> getUserByUsernameAndPassword(@RequestParam String username,
                                                          @RequestParam String password) {
        System.out.println("test6");
        Optional<User> user = userService.findByUsernameAndPassword(username, password);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
