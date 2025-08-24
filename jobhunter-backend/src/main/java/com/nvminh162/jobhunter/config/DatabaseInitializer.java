package com.nvminh162.jobhunter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Permission;
import com.nvminh162.jobhunter.domain.Role;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.enumerate.GenderEnum;
import com.nvminh162.jobhunter.repository.PermissionRepository;
import com.nvminh162.jobhunter.repository.RoleRepository;
import com.nvminh162.jobhunter.repository.UserRepository;

/*
 * CommandLineRunner chạy khi ứng dụng chạy
 */

/*
* Tạo class mặc dịnh dù implement CommandLineRunner thì class này vẫn không chạy
* Để biến thành phần của java spring thì thêm annotation
*/
@Service
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${api.version}")
    private String apiVersion;

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/api/"+apiVersion+"/companies", "POST", "COMPANIES"));
            arr.add(new Permission("Update a company", "/api/"+apiVersion+"/companies", "PUT", "COMPANIES"));
            arr.add(new Permission("Delete a company", "/api/"+apiVersion+"/companies/{id}", "DELETE", "COMPANIES"));
            arr.add(new Permission("Get a company by id", "/api/"+apiVersion+"/companies/{id}", "GET", "COMPANIES"));
            arr.add(new Permission("Get companies with pagination", "/api/"+apiVersion+"/companies", "GET", "COMPANIES"));

            arr.add(new Permission("Create a job", "/api/"+apiVersion+"/jobs", "POST", "JOBS"));
            arr.add(new Permission("Update a job", "/api/"+apiVersion+"/jobs", "PUT", "JOBS"));
            arr.add(new Permission("Delete a job", "/api/"+apiVersion+"/jobs/{id}", "DELETE", "JOBS"));
            arr.add(new Permission("Get a job by id", "/api/"+apiVersion+"/jobs/{id}", "GET", "JOBS"));
            arr.add(new Permission("Get jobs with pagination", "/api/"+apiVersion+"/jobs", "GET", "JOBS"));

            arr.add(new Permission("Create a permission", "/api/"+apiVersion+"/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/"+apiVersion+"/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/"+apiVersion+"/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/"+apiVersion+"/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/"+apiVersion+"/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a resume", "/api/"+apiVersion+"/resumes", "POST", "RESUMES"));
            arr.add(new Permission("Update a resume", "/api/"+apiVersion+"/resumes", "PUT", "RESUMES"));
            arr.add(new Permission("Delete a resume", "/api/"+apiVersion+"/resumes/{id}", "DELETE", "RESUMES"));
            arr.add(new Permission("Get a resume by id", "/api/"+apiVersion+"/resumes/{id}", "GET", "RESUMES"));
            arr.add(new Permission("Get resumes with pagination", "/api/"+apiVersion+"/resumes", "GET", "RESUMES"));

            arr.add(new Permission("Create a role", "/api/"+apiVersion+"/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/"+apiVersion+"/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/"+apiVersion+"/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/"+apiVersion+"/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/"+apiVersion+"/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/"+apiVersion+"/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/"+apiVersion+"/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/"+apiVersion+"/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/"+apiVersion+"/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/"+apiVersion+"/users", "GET", "USERS"));

            arr.add(new Permission("Create a subscriber", "/api/"+apiVersion+"/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/api/"+apiVersion+"/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/api/"+apiVersion+"/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/api/"+apiVersion+"/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Get subscribers with pagination", "/api/"+apiVersion+"/subscribers", "GET", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/api/"+apiVersion+"/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/"+apiVersion+"/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}
