package kaf.pin.lab1corp.controller.api;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.request.LoginRequest;
import kaf.pin.lab1corp.DTO.request.RegisterRequest;
import kaf.pin.lab1corp.DTO.response.LoginResponse;
import kaf.pin.lab1corp.DTO.response.UserResponse;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.service.JwtService;
import kaf.pin.lab1corp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthRestController {

    private final UserService userService;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    @Autowired
    public AuthRestController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Optional<Users> userOpt = userService.findByEmail(request.getEmail());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Users user = userOpt.get();
            
            // Simple password check (not encrypted in current implementation)
            if (!user.getPassword().equals(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
            LoginResponse response = new LoginResponse(token, user.getId(), user.getEmail(), user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during login", e);
            throw new BadRequestException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Optional<Users> existingUser = userService.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                throw new BadRequestException("User with email " + request.getEmail() + " already exists");
            }
            
            Users user = new Users();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole(request.getRole());
            user.setEnabled(true);
            
            Users savedUser = userService.saveUser(user);
            UserResponse response = new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during registration", e);
            throw new BadRequestException("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Optional<Users> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Users user = userOpt.get();
            UserResponse response = new UserResponse(user.getId(), user.getEmail(), user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
