package com.nvminh162.jobhunter.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.dto.Meta;
import com.nvminh162.jobhunter.domain.dto.ResultPaginationDTO;
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

    public ResultPaginationDTO handleGetAllUsers(Specification<User> specification) {
        List<User> pageUser = this.userRepository.findAll(specification);
        ResultPaginationDTO rpd = new ResultPaginationDTO();
        Meta mt = new Meta();
        // mt.setPage(pageUser.getNumber() + 1);
        // mt.setPageSize(pageUser.getSize());
        // mt.setPages(pageUser.getTotalPages());
        // mt.setTotal(pageUser.getTotalElements());
        rpd.setMeta(mt);
        rpd.setResult(pageUser);
        return rpd;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
