package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Permission;
import com.nvminh162.jobhunter.domain.Role;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.repository.PermissionRepository;
import com.nvminh162.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        // check permission
        if(role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                .stream().map(permission -> permission.getId())
                .collect(Collectors.toList());
            List<Permission> permissionsDB = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissionsDB);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id) {
        Optional<Role> optionalRole = this.roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }
        return null;
    }

    public Role update(Role role) {
        Role roleInDB = this.fetchById(role.getId());
        //check permissions
        if(role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                .stream().map(permission -> permission.getId())
                .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissions);
        }
        roleInDB.setName(role.getName());
        roleInDB.setDescription(role.getDescription());
        roleInDB.setActive(role.isActive());
        roleInDB.setPermissions(role.getPermissions());
        roleInDB = this.roleRepository.save(roleInDB);
        return roleInDB;
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResResultPaginationDTO getRoles(Specification<Role> specification, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(specification, pageable);
        ResResultPaginationDTO rs = new ResResultPaginationDTO();
        ResResultPaginationDTO.Meta mt = new ResResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());

        return rs;
    }
}
