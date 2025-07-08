package com.example.demo1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Import này để sử dụng disable() an toàn

import com.example.demo1.service.CustomerSuccessHandel;
import com.example.demo1.service.CustomUserDetailsService;
import org.springframework.core.annotation.Order; // Đã có

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomerSuccessHandel customerSuccessHandel;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomerSuccessHandel customerSuccessHandel) {
        this.customerSuccessHandel = customerSuccessHandel;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security Filter Chain cho các API (bắt đầu bằng /api/)
    @Bean
    @Order(1) // Ưu tiên chạy API trước
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**") // Chỉ áp dụng cho các đường dẫn /api/**
            // API thường không cần CSRF token nếu bạn không dùng Session/Cookie Authentication
            // Nếu bạn dùng JWT/OAuth2, bạn có thể tắt CSRF ở đây.
            .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho các API
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Cho phép tất cả API
        return http.build();
    }

    // Security Filter Chain cho các đường dẫn Web UI (bao gồm cả /cart/add)
    @Bean
    @Order(2) // Web chạy sau
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // Áp dụng cho tất cả các đường dẫn còn lại
            .csrf(csrf -> csrf.ignoringRequestMatchers("/cart/add", "/cart/update", "/cart/remove/**", "/email/verify-otp","/email/**","/input_email")) // <-- QUAN TRỌNG: BẬT CSRF VÀ CẤU HÌNH BỎ QUA CHO CÁC API CỦA GIỎ HÀNG
            // Hoặc nếu bạn muốn vô hiệu hóa hoàn toàn CSRF cho web cũng như API
            // .csrf(AbstractHttpConfigurer::disable) // Nếu bạn chọn tắt hoàn toàn CSRF cho Web

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/register",
                    "/register/save",
                    "/vetifyEmail",
                    "/email/send-otp",
                    "/email/verify-otp",
                    "/home",
                    "/reset_email",
                    "/reset-password",
                    "/resetMK",
                    "/input_email",
                    "/products/**",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll() // Các đường dẫn public (không cần đăng nhập)
                // Các đường dẫn cho phép người dùng USER truy cập:
                .requestMatchers("/cart", "/my-orders/**", "/checkout", "/cart/add").hasRole("USER") // <-- THÊM "/cart/add" vào danh sách yêu cầu ROLE_USER
                .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ admin mới vào
                .requestMatchers("/user","/user/**").hasRole("USER") // Các đường dẫn bắt đầu bằng /user/ yêu cầu role USER
                .anyRequest().authenticated() // Mọi request khác đều yêu cầu xác thực
            )
            .formLogin(login -> login
                .loginPage("/login") // Trang đăng nhập tùy chỉnh của bạn
                .loginProcessingUrl("/login") // URL xử lý đăng nhập
                .usernameParameter("email") // Tên tham số cho email người dùng
                .passwordParameter("password") // Tên tham số cho mật khẩu
                .successHandler(customerSuccessHandel) // Handler khi đăng nhập thành công
                .permitAll() // Cho phép tất cả truy cập trang đăng nhập và quá trình xử lý của nó
            )
            .userDetailsService(customUserDetailsService) // Thiết lập UserDetailsService của bạn
            .logout(logout -> logout
            	    .logoutUrl("/logout")
            	    .logoutSuccessUrl("/login?logout")
            	    .invalidateHttpSession(true)              // 👈 Xóa session khỏi server
            	    .deleteCookies("JSESSIONID")              // 👈 Xóa cookie JSESSIONID khỏi trình duyệt
            	    .permitAll()
            	);
        return http.build();
    }
}