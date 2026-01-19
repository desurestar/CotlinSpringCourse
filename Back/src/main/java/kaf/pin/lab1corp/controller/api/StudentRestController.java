package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.response.DepartmentResponse;
import kaf.pin.lab1corp.DTO.response.GroupResponse;
import kaf.pin.lab1corp.DTO.response.StudentResponse;
import kaf.pin.lab1corp.DTO.response.UserResponse;
import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.GroupsService;
import kaf.pin.lab1corp.service.StudentsService;
import kaf.pin.lab1corp.service.UserService;
import kaf.pin.lab1corp.util.RequestParamUtil;
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
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentRestController {

    private final StudentsService studentsService;
    private final GroupsService groupsService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(StudentRestController.class);

    @Autowired
    public StudentRestController(StudentsService studentsService, GroupsService groupsService, 
                                 UserService userService) {
        this.studentsService = studentsService;
        this.groupsService = groupsService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String search) {
        try {
            List<Students> students;
            
            if (groupId != null) {
                students = studentsService.getStudentsByGroupId(groupId);
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    students = students.stream()
                        .filter(student -> student.getName().toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
                }
            } else if (search != null && !search.trim().isEmpty()) {
                students = studentsService.searchStudents(search);
            } else {
                students = studentsService.getAllStudents();
            }
            
            List<StudentResponse> response = students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching students", e);
            throw new RuntimeException("Failed to fetch students");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        try {
            Optional<Students> student = studentsService.getStudentById(id);
            if (student.isEmpty()) {
                throw new ResourceNotFoundException("Student not found with id: " + id);
            }
            
            return ResponseEntity.ok(convertToResponse(student.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching student", e);
            throw new RuntimeException("Failed to fetch student");
        }
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Long groupId = RequestParamUtil.getLongValue(request.get("groupId"));
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            
            if (name == null || name.trim().isEmpty()) {
                throw new BadRequestException("Name is required");
            }
            if (groupId == null) {
                throw new BadRequestException("Group ID is required");
            }
            
            Optional<Groups> groupOpt = groupsService.getGroupById(groupId);
            if (groupOpt.isEmpty()) {
                throw new BadRequestException("Group not found with id: " + groupId);
            }
            
            Students student = new Students();
            student.setName(name);
            student.setGroups(groupOpt.get());
            
            // Create user if email and password are provided
            if (email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                Optional<Users> existingUser = userService.findByEmail(email);
                if (existingUser.isPresent()) {
                    throw new BadRequestException("User with email " + email + " already exists");
                }
                
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password);
                user.setRole("STUDENT");
                user.setEnabled(true);
                
                Users savedUser = userService.saveUser(user);
                student.setUser(savedUser);
            }
            
            Students savedStudent = studentsService.saveStudent(student);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedStudent));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating student", e);
            throw new BadRequestException("Failed to create student: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Optional<Students> studentOpt = studentsService.getStudentById(id);
            if (studentOpt.isEmpty()) {
                throw new ResourceNotFoundException("Student not found with id: " + id);
            }
            
            String name = (String) request.get("name");
            Long groupId = RequestParamUtil.getLongValue(request.get("groupId"));
            
            Students student = studentOpt.get();
            
            if (name != null && !name.trim().isEmpty()) {
                student.setName(name);
            }
            
            if (groupId != null) {
                Optional<Groups> groupOpt = groupsService.getGroupById(groupId);
                if (groupOpt.isEmpty()) {
                    throw new BadRequestException("Group not found with id: " + groupId);
                }
                student.setGroups(groupOpt.get());
            }
            
            Students updatedStudent = studentsService.saveStudent(student);
            
            return ResponseEntity.ok(convertToResponse(updatedStudent));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating student", e);
            throw new BadRequestException("Failed to update student: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            boolean deleted = studentsService.safeDeleteStudent(id);
            if (!deleted) {
                throw new ResourceNotFoundException("Student not found with id: " + id);
            }
            
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting student", e);
            throw new BadRequestException("Failed to delete student: " + e.getMessage());
        }
    }

    private StudentResponse convertToResponse(Students student) {
        GroupResponse groupResponse = null;
        if (student.getGroups() != null) {
            Groups group = student.getGroups();
            DepartmentResponse deptResponse = group.getDepartment() != null
                ? new DepartmentResponse(group.getDepartment().getId(), 
                                        group.getDepartment().getDepartmentName(), null)
                : null;
            groupResponse = new GroupResponse(group.getId(), group.getGroup_name(), 
                                            deptResponse, null);
        }
        
        UserResponse userResponse = student.getUser() != null
            ? new UserResponse(student.getUser().getId(), 
                              student.getUser().getEmail(), 
                              student.getUser().getRole())
            : null;
        
        return new StudentResponse(student.getId(), student.getName(), 
                                  groupResponse, userResponse);
    }
}
