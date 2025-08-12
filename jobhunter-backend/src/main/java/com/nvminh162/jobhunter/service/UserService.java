package com.nvminh162.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return userRepository.save(user);
    }

    public List<User> handleGetAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
