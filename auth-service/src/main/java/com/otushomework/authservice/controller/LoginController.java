package com.otushomework.authservice.controller;

import com.otushomework.authservice.model.Session;
import com.otushomework.authservice.model.UserDTO;
import com.otushomework.authservice.repository.SessionRepository;
import com.otushomework.authservice.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
public class LoginController {

    private final SessionRepository sessionRepository;
    private final UserService userService;


    @Autowired
    public LoginController(SessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpServletResponse response) {
        Optional<UserDTO> user = userService.getUserByUsername(username, password);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Login or password is incorrect");
        }

        Session newSession = createSession(user.get());
        Cookie sessionCookie = new Cookie("session", newSession.getId());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge((int) java.time.Duration.between(LocalDateTime.now(), newSession.getExpiresIn()).getSeconds());

        response.addCookie(sessionCookie);
        sessionRepository.save(newSession);

        return ResponseEntity.ok("Authenticated!");
    }

    public Session createSession(UserDTO user) {
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        session.setUserId(user.getId());
        session.setUserName(user.getUsername());
        session.setExpiresIn(LocalDateTime.now().plusMinutes(10));
        return sessionRepository.save(session);
    }
}