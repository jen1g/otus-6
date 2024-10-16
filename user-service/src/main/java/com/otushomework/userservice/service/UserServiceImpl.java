package com.otushomework.userservice.service;

import com.otushomework.userservice.entity.User;
import com.otushomework.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User saveUser(User user) {
       return repository.save(user);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return repository.findById(id);
    }

    @Override
    public void updateUserById(User updatedUser) {
        repository.save(updatedUser);
    }

    @Override
    public void deleteUserById(int id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
       return repository.findByUsernameAndPassword(username, password);
    }

    @Override
    public boolean existsByUsername(String username) {
       return repository.findUsersByUsername(username);
    }

}
