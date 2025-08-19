package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.dto.Meta;
import com.nvminh162.jobhunter.domain.dto.ResCreateUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResUpdateUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResultPaginationDTO;
import com.nvminh162.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> handleGetUserById(long id) {
        return userRepository.findById(id);
    }

    public User handleCreateUser(User user) {
        return userRepository.save(user);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreateAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResultPaginationDTO handleGetAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();
        // Get from frontend send request
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // Get from dbs
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        resultPaginationDTO.setMeta(mt);

        // remove senstive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listUser);
        return resultPaginationDTO;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleUpdateUser(User reqUser) {
        Optional<User> findUser = this.handleGetUserById(reqUser.getId());
        if (findUser.isPresent()) {
            User user = findUser.get();
            user.setAddress(reqUser.getAddress());
            user.setGender(reqUser.getGender());
            user.setAge(reqUser.getAge());
            user.setName(reqUser.getName());
            user = this.userRepository.save(user);
            return user;
        }
        return null;
    }
}
