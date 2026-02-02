package kaf.pin.lab1corp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

    /**
     * Custom EntryPoint для /api/** чтобы возвращать JSON-ошибки, а не редирект/HTML.
     */
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> data = new HashMap<>();
            data.put("error", "Unauthorized");
            data.put("message", authException.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), data);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // --- matchers ниже для разделения API и web ---
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                )
                .authorizeHttpRequests(auth -> auth
                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Web authentication pages
                        .requestMatchers("/login", "/register", "/perform_login", "/logout").permitAll()

                        // API authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // API GET endpoints - public access
                        .requestMatchers(HttpMethod.GET, "/api/departments/**", "/api/employees/**",
                                "/api/groups/**", "/api/students/**", "/api/posts/**", "/api/articles/**").permitAll()

                        // API POST/PUT endpoints - require ADMIN or EMPLOYEE role
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "EMPLOYEE")

                        // API DELETE endpoints - require ADMIN role
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // Web endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/departments/**", "/employees/**", "/employe/**", "/articles/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                // Важно! Для API-секции:
                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                restAuthenticationEntryPoint(),
                                request -> request.getRequestURI().startsWith("/api/")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                Map<String, Object> data = new HashMap<>();
                                data.put("error", "Forbidden");
                                data.put("message", accessDeniedException.getMessage());
                                new ObjectMapper().writeValue(response.getOutputStream(), data);
                            } else {
                                response.sendRedirect("/access-denied");
                            }
                        })
                )
                // Стандартная web-форма (оставьте если нужен обычный веб-интерфейс):
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                );
        // JWT authentication filter processes Bearer tokens in Authorization header for /api/**
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // Без .accessDeniedPage("/access-denied") - теперь перенаправление контролирует accessDeniedHandler!
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Лучше использовать BCrypt, вот так:
        return new BCryptPasswordEncoder();
    }
}