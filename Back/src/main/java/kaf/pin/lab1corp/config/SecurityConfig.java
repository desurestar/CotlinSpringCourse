package kaf.pin.lab1corp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                                        "/api/groups/**", "/api/students/**", "/api/posts/**").permitAll()
                        
                        // API POST/PUT endpoints - require ADMIN or EMPLOYEE role
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        
                        // API DELETE endpoints - require ADMIN role
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        
                        // Web endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/departments/**", "/employe/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/departments/", true)
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
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}