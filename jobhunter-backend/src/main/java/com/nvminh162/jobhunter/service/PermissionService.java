package com.nvminh162.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Permission;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(long id) {
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            return optionalPermission.get();
        }
        return null;
    }

    public Permission update(Permission p) {
        Permission permissionInDB = this.fetchById(p.getId());
        if (permissionInDB != null) {
            permissionInDB.setName(p.getName());
            permissionInDB.setApiPath(p.getApiPath());
            permissionInDB.setMethod(p.getMethod());
            permissionInDB.setModule(p.getModule());

            // update
            permissionInDB = this.permissionRepository.save(permissionInDB);
            return permissionInDB;
        }
        return null;
    }

    public void delete(long id) {
        // delete permission_role contain
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        Permission permission = optionalPermission.get();
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        // delete permission
        this.permissionRepository.delete(permission);
    }

    public ResResultPaginationDTO getPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(specification, pageable);
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

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());

        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }
}
