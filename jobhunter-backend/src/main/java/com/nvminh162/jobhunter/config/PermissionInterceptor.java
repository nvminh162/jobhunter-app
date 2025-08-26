package com.nvminh162.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.nvminh162.jobhunter.domain.Permission;
import com.nvminh162.jobhunter.domain.Role;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.service.UserService;
import com.nvminh162.jobhunter.util.SecurityUtil;
import com.nvminh162.jobhunter.util.error.IdInvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// B1:
/* Interceptor
 * Mô hình hoạt động của dự án này:
 * Request -> Spring Security -> Interceptor <middleware> -> Controller -> Service
 * => Từ Security sau khi giải mã Token (hàm jwtDecoder)
 * => Từ đó đã định danh được người dùng nào đã gửi request
 * => Query xuống dbs biết người đó có vai trò gì?
 * => So sánh controller người dùng muốn truy cập có valid ko?
 */

// B2
/* Ngoài ra còn có thể áp dụng thêm
 * prehandle() – called before the execution of the actual handler
 * postHandle() – called after the handler is executed
 * afterCompletion() – called after the complete request is finished and the view is generated
 *
 * src: https://www.baeldung.com/spring-mvc-handlerinterceptor
 */

// B3 Config spring to run

// B4 Bug
/*
 * fix org.hibernate.LazyInitializationException - could not initialize proxy - no Session
 * Do dùng cơ chế FetchType.LAZY
 * @Transactional nói với spring: hãy chờ thao tác vào database rồi kết thúc session
 */

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path); // check here
        System.out.println(">>> httpMethod= " + httpMethod); // check here
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        // lấy email từ spring security
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        if (email != null && !email.isEmpty()) {
            User user = userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions
                            .stream()
                            .anyMatch(
                                    item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new IdInvalidException("Bạn có quyền truy cập endpoint này!");
                    }
                } else {
                    throw new IdInvalidException("Bạn có quyền truy cập endpoint này!");
                }
            }
        }
        // không cần xử lý email null vì nếu email null thì đã bị chặn
        // Security (.anyRequest().authenticated())

        return true;
    }
}
