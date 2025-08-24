package com.nvminh162.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.dto.auth.ReqLoginDTO;
import com.nvminh162.jobhunter.dto.auth.ResLoginDTO;
import com.nvminh162.jobhunter.service.UserService;
import com.nvminh162.jobhunter.util.SecurityUtil;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}")
public class AuthController {

    @Value("${nvminh162.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) { // add @Valid to validate <add dependencies/>
        // Nap input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set thông tin người dùng đăng nhập vào context (Có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName(),
                currentUserDB.getRole()
            );
            resLoginDTO.setUser(userLogin);
        }

        // ********************************************************
        // Có thể tách logic này riêng để tái sử dụng
        // create a token (authentication.getName() => lấy ra email từ authentication)
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
        // ********************************************************

        // Update User Token
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // Set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                // Cho phép cookie chỉ cho server sử dụng
                .httpOnly(true)
                .secure(true)
                // Cookie sử dụng được trong tắt cả dự án chứ không phải /api/${api.version}
                .path("/")
                // Cookie sau bao lâu thì hết hạn, hết hạn tự xoá khỏi cookie browser
                .maxAge(refreshTokenExpiration)
                // Khi nào gửi cookie này? Nếu không định nghĩa thì web nào cũng gửi cookie
                // .domain("example.com")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
        @CookieValue(name = "refresh_token", defaultValue = "cookie_not_exist") String refreshToken
    ) throws IdInvalidException {
        //handle exception if refresh token not exist
        if(refreshToken.equals("cookie_not_exist")) {
            throw new IdInvalidException("Bạn chưa có refresh token ở Cookies");
        }

        // check valid token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);

        // Lấy ra email từ subject của cookies khi login từ Refresh token
        String email = decodedToken.getSubject();

        // Check user by token + email in dbs
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if(currentUser == null) {
            throw new IdInvalidException("Refresh token không hợp lệ!");
        }
        // issue new token/ set refresh token as cookies
        // ng dùng đã tồn tại, tạo mới token và set refresh token giống cookies
        /* Phần dưới giống logic Login - có thể refactor tái sử dụng */
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(email);

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName(),
                currentUserDB.getRole()
            );
            resLoginDTO.setUser(userLogin);
        }

        // ********************************************************
        // Có thể tách logic này riêng để tái sử dụng
        // create a token
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);
        // ********************************************************

        // Update User Token
        this.userService.updateUserToken(newRefreshToken, email);

        // Set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                // Cho phép cookie chỉ cho server sử dụng
                .httpOnly(true)
                .secure(true)
                // Cookie sử dụng được trong tắt cả dự án chứ không phải /api/${api.version}
                .path("/")
                // Cookie sau bao lâu thì hết hạn, hết hạn tự xoá khỏi cookie browser
                .maxAge(refreshTokenExpiration)
                // Khi nào gửi cookie này? Nếu không định nghĩa thì web nào cũng gửi cookie
                // .domain("example.com")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user success")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
            ? SecurityUtil.getCurrentUserLogin().get()
            : "";
        if(email.equals("")) {
            throw new IdInvalidException("Access token không hợp lệ");
        }

        //update refresh token from dbs
        this.userService.updateUserToken(null, email);

        //remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
            .from("refresh_token", null)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build();
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
            .body(null);
    }
}
