package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.response.DepartmentResponse;
import kaf.pin.lab1corp.DTO.response.EmployeeResponse;
import kaf.pin.lab1corp.DTO.response.PostResponse;
import kaf.pin.lab1corp.DTO.response.UserResponse;
import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.entity.Post;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.DepartmentService;
import kaf.pin.lab1corp.service.EmployeeService;
import kaf.pin.lab1corp.service.PostService;
import kaf.pin.lab1corp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PostService postService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(EmployeeRestController.class);

    @Autowired
    public EmployeeRestController(EmployeeService employeeService, DepartmentService departmentService,
                                  PostService postService, UserService userService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId) {
        try {
            List<Employes> employees;
            
            if (departmentId != null) {
                employees = employeeService.getEmployeesByDepartmentId(departmentId);
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    employees = employees.stream()
                        .filter(emp -> emp.getName().toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
                }
            } else if (search != null && !search.trim().isEmpty()) {
                employees = employeeService.searchEmployees(search);
            } else {
                employees = employeeService.getAllEmployees();
            }
            
            List<EmployeeResponse> response = employees.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching employees", e);
            throw new RuntimeException("Failed to fetch employees");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        try {
            Optional<Employes> employee = employeeService.getEmployeeById(id);
            if (employee.isEmpty()) {
                throw new ResourceNotFoundException("Employee not found with id: " + id);
            }
            
            return ResponseEntity.ok(convertToResponse(employee.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching employee", e);
            throw new RuntimeException("Failed to fetch employee");
        }
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Long postId = getLongValue(request.get("postId"));
            Long departmentId = getLongValue(request.get("departmentId"));
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String role = (String) request.get("role");
            
            if (name == null || name.trim().isEmpty()) {
                throw new BadRequestException("Name is required");
            }
            if (postId == null) {
                throw new BadRequestException("Post ID is required");
            }
            if (departmentId == null) {
                throw new BadRequestException("Department ID is required");
            }
            
            Optional<Post> postOpt = postService.getPostById(postId);
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(departmentId);
            
            if (postOpt.isEmpty()) {
                throw new BadRequestException("Post not found with id: " + postId);
            }
            if (departmentOpt.isEmpty()) {
                throw new BadRequestException("Department not found with id: " + departmentId);
            }
            
            Employes employee = new Employes();
            employee.setName(name);
            employee.setPost(postOpt.get());
            employee.setDepartment(departmentOpt.get());
            
            // Create user if email and password are provided
            if (email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                Optional<Users> existingUser = userService.findByEmail(email);
                if (existingUser.isPresent()) {
                    throw new BadRequestException("User with email " + email + " already exists");
                }
                
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password);
                user.setRole(role != null ? role : "EMPLOYEE");
                user.setEnabled(true);
                
                Users savedUser = userService.saveUser(user);
                employee.setUser(savedUser);
            }
            
            Employes savedEmployee = employeeService.saveEmployee(employee);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedEmployee));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating employee", e);
            throw new BadRequestException("Failed to create employee: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Optional<Employes> employeeOpt = employeeService.getEmployeeById(id);
            if (employeeOpt.isEmpty()) {
                throw new ResourceNotFoundException("Employee not found with id: " + id);
            }
            
            String name = (String) request.get("name");
            Long postId = getLongValue(request.get("postId"));
            Long departmentId = getLongValue(request.get("departmentId"));
            
            Employes employee = employeeOpt.get();
            
            if (name != null && !name.trim().isEmpty()) {
                employee.setName(name);
            }
            
            if (postId != null) {
                Optional<Post> postOpt = postService.getPostById(postId);
                if (postOpt.isEmpty()) {
                    throw new BadRequestException("Post not found with id: " + postId);
                }
                employee.setPost(postOpt.get());
            }
            
            if (departmentId != null) {
                Optional<Departments> departmentOpt = departmentService.getDepartmentById(departmentId);
                if (departmentOpt.isEmpty()) {
                    throw new BadRequestException("Department not found with id: " + departmentId);
                }
                employee.setDepartment(departmentOpt.get());
            }
            
            Employes updatedEmployee = employeeService.saveEmployee(employee);
            
            return ResponseEntity.ok(convertToResponse(updatedEmployee));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating employee", e);
            throw new BadRequestException("Failed to update employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            boolean deleted = employeeService.safeDeleteEmployee(id);
            if (!deleted) {
                throw new ResourceNotFoundException("Employee not found with id: " + id);
            }
            
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting employee", e);
            throw new BadRequestException("Failed to delete employee: " + e.getMessage());
        }
    }

    private EmployeeResponse convertToResponse(Employes employee) {
        PostResponse postResponse = employee.getPost() != null 
            ? new PostResponse(employee.getPost().getId(), employee.getPost().getPost_name())
            : null;
            
        DepartmentResponse departmentResponse = employee.getDepartment() != null
            ? new DepartmentResponse(employee.getDepartment().getId(), 
                                    employee.getDepartment().getDepartmentName(), null)
            : null;
            
        UserResponse userResponse = employee.getUser() != null
            ? new UserResponse(employee.getUser().getId(), 
                              employee.getUser().getEmail(), 
                              employee.getUser().getRole())
            : null;
            
        return new EmployeeResponse(employee.getId(), employee.getName(), 
                                   postResponse, departmentResponse, userResponse);
    }

    private Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
