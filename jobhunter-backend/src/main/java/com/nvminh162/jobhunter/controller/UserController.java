package com.nvminh162.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.dto.ResCreateUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResUpdateUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResUserDTO;
import com.nvminh162.jobhunter.domain.dto.ResultPaginationDTO;
import com.nvminh162.jobhunter.service.UserService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/${api.version}")
public class UserController {
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User reqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email " + reqUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword = this.passwordEncoder.encode(reqUser.getPassword());
        reqUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> specification, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetAllUsers(specification, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        Optional<User> findUser = this.userService.handleGetUserById(id);
        if(!findUser.isPresent()) {
            throw new IdInvalidException("User ID: " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<User> findUser = this.userService.handleGetUserById(id);
        if(!findUser.isPresent()) {
            throw new IdInvalidException("User ID: " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(findUser.get()));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User reqUser) throws IdInvalidException {
        // Optional<User> findUser = this.userService.handleGetUserById(reqUser.getId());
        User user = this.userService.handleUpdateUser(reqUser);
        if(user == null) {
            throw new IdInvalidException("User ID: " + reqUser.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(user));
    }
}
