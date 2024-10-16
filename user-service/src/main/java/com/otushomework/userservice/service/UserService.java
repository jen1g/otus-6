package com.otushomework.userservice.service;

import com.otushomework.userservice.entity.User;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);
    Optional<User> getUserById(int id);
    void updateUserById(User updatedUser);
    void deleteUserById(int id);
    Optional<User> findByUsernameAndPassword(String username, String password);
    boolean existsByUsername(String username);

}
