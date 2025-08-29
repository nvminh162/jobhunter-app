package com.nvminh162.jobhunter.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.nvminh162.jobhunter.domain.Permission;
import com.nvminh162.jobhunter.domain.Role;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.service.UserService;
import com.nvminh162.jobhunter.util.SecurityUtil;
import com.nvminh162.jobhunter.util.error.PermissionException;

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
// Giải thích cơ chế @Transactional
/* #1: @Transactional
 * fix org.hibernate.LazyInitializationException - could not initialize proxy - no Session
 * Do dùng cơ chế FetchType.LAZY
 * Chưa có session (Để có 1 session do java spring tự động tạo và quản lý) chưa vào tới Controller
 * @Transactional nói với spring: tạo 1 phiên đăng nhập vào dbs khi kết thúc thì xoá session đó đi
 */

 /* #2: @Transactional
  * (Quan tâm đến số lượng ng dùng lớn)
  * Nếu có 2 lời gọi request, Mỗi lời gọi request nó sẽ là block
  * Sau khi request thứ I xong sẽ cập nhật data cho request thứ 2 (theo thứ tự)
  * Trường hợp thất bại có thể rollback lại
  */

public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

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
        
        // Log request details
        logger.debug("Processing permission check for request");
        logger.debug("Path: {}", path);
        logger.debug("HTTP Method: {}", httpMethod);
        logger.debug("Request URI: {}", requestURI);
        

        // check permission
        // lấy email từ spring security
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        
        if (email != null && !email.isEmpty()) {
            logger.debug("Checking permissions for user: {}", email);
            User user = userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    logger.debug("User has role: {}", role.getName());
                    List<Permission> permissions = role.getPermissions(); // error if haven't session => fix: @Transactional create session
                    
                    boolean isAllow = permissions
                            .stream()
                            .anyMatch(
                                    item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    
                    if (isAllow) {
                        logger.debug("Permission granted for {} {} to user {}", httpMethod, path, email);
                    } else {
                        logger.warn("Permission denied for {} {} to user {} with role {}", httpMethod, path, email, role.getName());
                        throw new PermissionException("Bạn có quyền truy cập endpoint này!");
                    }
                } else {
                    logger.warn("User {} has no role assigned", email);
                    throw new PermissionException("Bạn có quyền truy cập endpoint này!");
                }
            } else {
                logger.warn("User not found for email: {}", email);
            }
        } else {
            logger.debug("No authenticated user found for request");
        }
        // không cần xử lý email null vì nếu email null thì đã bị chặn
        // Security (.anyRequest().authenticated())
        // Trả về true cho phép đi tới controller, false thì chặn lại (chặn đã xử lý Exception trên)
        return true;
    }
}
