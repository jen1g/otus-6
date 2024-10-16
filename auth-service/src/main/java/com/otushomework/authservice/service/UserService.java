package com.otushomework.authservice.service;

import com.otushomework.authservice.model.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserService {

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<UserDTO> getUserByUsername(String username, String password) {
        System.out.println("test");
        System.out.println(userServiceUrl);
        String endpoint = String.format("%s/user?username=%s&password=%s", userServiceUrl, username, password);
        System.out.println(endpoint);
        try {
            UserDTO user = restTemplate.getForObject(endpoint, UserDTO.class);
            if (user != null && user.getId() != 0) {
                return Optional.of(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}