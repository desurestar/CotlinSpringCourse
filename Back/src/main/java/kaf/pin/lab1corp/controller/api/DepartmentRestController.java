package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.response.DepartmentResponse;
import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.DepartmentService;
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
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentRestController {

    private final DepartmentService departmentService;
    private final Logger logger = LoggerFactory.getLogger(DepartmentRestController.class);

    @Autowired
    public DepartmentRestController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments(
            @RequestParam(required = false) String search) {
        try {
            List<Departments> departments = search != null && !search.trim().isEmpty()
                ? departmentService.searchDepartments(search)
                : departmentService.getAllDepartments();
            
            List<DepartmentResponse> response = departments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching departments", e);
            throw new RuntimeException("Failed to fetch departments");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        try {
            Optional<Departments> department = departmentService.getDepartmentById(id);
            if (department.isEmpty()) {
                throw new ResourceNotFoundException("Department not found with id: " + id);
            }
            
            return ResponseEntity.ok(convertToResponse(department.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching department", e);
            throw new RuntimeException("Failed to fetch department");
        }
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody Map<String, String> request) {
        try {
            String departmentName = request.get("departmentName");
            if (departmentName == null || departmentName.trim().isEmpty()) {
                throw new BadRequestException("Department name is required");
            }
            
            Departments department = new Departments();
            department.setDepartmentName(departmentName);
            
            Departments savedDepartment = departmentService.saveDepartment(department);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedDepartment));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating department", e);
            throw new BadRequestException("Failed to create department: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(id);
            if (departmentOpt.isEmpty()) {
                throw new ResourceNotFoundException("Department not found with id: " + id);
            }
            
            String departmentName = request.get("departmentName");
            if (departmentName == null || departmentName.trim().isEmpty()) {
                throw new BadRequestException("Department name is required");
            }
            
            Departments department = departmentOpt.get();
            department.setDepartmentName(departmentName);
            
            Departments updatedDepartment = departmentService.saveDepartment(department);
            
            return ResponseEntity.ok(convertToResponse(updatedDepartment));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating department", e);
            throw new BadRequestException("Failed to update department: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(id);
            if (departmentOpt.isEmpty()) {
                throw new ResourceNotFoundException("Department not found with id: " + id);
            }
            
            Departments department = departmentOpt.get();
            if (department.getEmployesList() != null && !department.getEmployesList().isEmpty()) {
                throw new BadRequestException("Cannot delete department with employees");
            }
            
            departmentService.deleteDepartment(id);
            
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting department", e);
            throw new BadRequestException("Failed to delete department: " + e.getMessage());
        }
    }

    private DepartmentResponse convertToResponse(Departments department) {
        int employeeCount = department.getEmployesList() != null ? department.getEmployesList().size() : 0;
        return new DepartmentResponse(department.getId(), department.getDepartmentName(), employeeCount);
    }
}
