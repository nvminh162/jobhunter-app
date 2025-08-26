package com.nvminh162.jobhunter.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.nvminh162.jobhunter.util.SecurityUtil;

/*
 * Docs:
 * https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
 */

/* NOTE
 * Vì sao lại thêm @Bean kết hợp @Configuration:
 * Nói với Spring rằng: tôi muốn ghi đè cấu hình mặc định
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${nvminh162.jwt.base64-secret}")
    private String jwtKey;

    @Value("${api.version}")
    private String apiVersion;

    /*
     * NOTE
     * Sử dụng thuật toán BCrypt
     * Ghi đè cấu hình mặc định đối tượng PasswordEncoder
     * => Mã hoá mật khẩu người dùng bằng thuật toán BCrypt =))
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * NOTE
     * Ghi đè cấu hình mặc định đối tượng SecurityFilterChain
     *
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint // custom here
    ) throws Exception {
        String[] whiteList = {
                "/",
                "/api/" + apiVersion + "/auth/login",
                "/api/" + apiVersion + "/auth/refresh",
                "/api/" + apiVersion + "/auth/register",
                "/api/" + apiVersion + "/email/**",
                "/storage/**"
        };

        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                /* Cho phép vào trang /** từ URL */
                                .requestMatchers(whiteList).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/companies").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/jobs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/skills").permitAll()
                                /* Còn lại bất cứ request nào buộc phải xác thực */
                                .anyRequest().authenticated()
                             // .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                /*
                 * Default exception (Config authorization)
                 * Trường hợp này không cần vì đã có customAuthenticationEntryPoint
                 *
                 */
                // .exceptionHandling(
                // exceptions -> exceptions
                // .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                /* Tắt không dùng form login mặc định của spring */
                .formLogin(f -> f.disable())
                /*
                 * Enable Session: Default is stateful, nói với spring tôi dùng mô hình
                 * stateless
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // khi decode thành công
    /*
     * Default spring không chạy qua phần code này
     * Do ta nói với spring đưa token (VD chứa "permission")
     * Nạp vào authentication
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // (nạp "permission" chạy vào hàm convert của spring)
        // set Authorities để có thể dùng hasAuthorities()
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    // Nhờ có hàm này Nó sẽ lưu data vào Spring security! (Security Context)
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }
}
