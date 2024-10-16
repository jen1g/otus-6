package com.otushomework.authservice.controller;

import com.otushomework.authservice.model.Session;
import com.otushomework.authservice.repository.SessionRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    private final SessionRepository sessionRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@CookieValue(value = "session", defaultValue = "") String sessionId)  {
        if (sessionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", session.getUserId());
            claims.put("user_name", session.getUserName());

            // Установка времени истечения срока действия токена
            Date expirationDate = Date.from(session.getExpiresIn().atZone(ZoneId.systemDefault()).toInstant());

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(expirationDate) // Добавляем стандартное поле exp
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();

            return ResponseEntity.ok().header("X-Auth-Token", token).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
