package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.dto.user.ResCreateUserDTO;
import com.nvminh162.jobhunter.dto.user.ResUpdateUserDTO;
import com.nvminh162.jobhunter.dto.user.ResUserDTO;
import com.nvminh162.jobhunter.repository.CompanyRespository;
import com.nvminh162.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private CompanyRespository companyRespository;

    public UserService(UserRepository userRepository, CompanyRespository companyRespository) {
        this.userRepository = userRepository;
        this.companyRespository = companyRespository;
    }

    public Optional<User> handleGetUserById(long id) {
        return userRepository.findById(id);
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRespository.findById(user.getCompany().getId());
            user.setCompany(optionalCompany.isPresent() ? optionalCompany.get() : null);
        }

        return userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResResultPaginationDTO handleGetAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResResultPaginationDTO resultPaginationDTO = new ResResultPaginationDTO();
        ResResultPaginationDTO.Meta mt = new ResResultPaginationDTO.Meta();
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
                        item.getCreatedAt(),
                        new ResUserDTO.ResUserCompanyDTO(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null))) // <-- Added closing
                                                                                                  // parenthesis here
                .collect(Collectors.toList());
        // .stream().map(item -> this.convertToResUserDTO(item))
        // .collect(Collectors.toList());
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

            // check company
            if (reqUser.getCompany() != null) {
                Optional<Company> optionalCompany = this.companyRespository.findById(reqUser.getCompany().getId());
                user.setCompany(optionalCompany.isPresent() ? optionalCompany.get() : null);
            }

            user = this.userRepository.save(user);
            return user;
        }
        return null;
    }

    public void updateUserToken(String token, String email) {
        User findUser = this.handleGetUserByUsername(email);
        if (findUser != null) {
            findUser.setRefreshToken(token);
            this.userRepository.save(findUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.ResUserCompanyDTO resUserCompanyDTO = new ResCreateUserDTO.ResUserCompanyDTO();

        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreateAt(user.getCreatedAt());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            resUserCompanyDTO.setId(user.getCompany().getId());
            resUserCompanyDTO.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(resUserCompanyDTO);
        }

        return resCreateUserDTO;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.ResUserCompanyDTO resUserCompanyDTO = new ResUserDTO.ResUserCompanyDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            resUserCompanyDTO.setId(user.getCompany().getId());
            resUserCompanyDTO.setName(user.getCompany().getName());
            resUserDTO.setCompany(resUserCompanyDTO);
        }

        return resUserDTO;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.ResUserCompanyDTO resUserCompanyDTO = new ResUpdateUserDTO.ResUserCompanyDTO();
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            resUserCompanyDTO.setId(user.getCompany().getId());
            resUserCompanyDTO.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(resUserCompanyDTO);
        }

        return resUpdateUserDTO;
    }
}
