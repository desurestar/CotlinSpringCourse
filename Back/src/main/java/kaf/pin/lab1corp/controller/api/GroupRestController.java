package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.response.DepartmentResponse;
import kaf.pin.lab1corp.DTO.response.GroupResponse;
import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.DepartmentService;
import kaf.pin.lab1corp.service.GroupsService;
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
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupRestController {

    private final GroupsService groupsService;
    private final DepartmentService departmentService;
    private final Logger logger = LoggerFactory.getLogger(GroupRestController.class);

    @Autowired
    public GroupRestController(GroupsService groupsService, DepartmentService departmentService) {
        this.groupsService = groupsService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search) {
        try {
            List<Groups> groups;
            
            if (departmentId != null) {
                groups = groupsService.getGroupsByDepartmentId(departmentId);
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    groups = groups.stream()
                        .filter(group -> group.getGroup_name().toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
                }
            } else if (search != null && !search.trim().isEmpty()) {
                groups = groupsService.searchGroups(search);
            } else {
                groups = groupsService.getAllGroups();
            }
            
            List<GroupResponse> response = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching groups", e);
            throw new RuntimeException("Failed to fetch groups");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        try {
            Optional<Groups> group = groupsService.getGroupById(id);
            if (group.isEmpty()) {
                throw new ResourceNotFoundException("Group not found with id: " + id);
            }
            
            return ResponseEntity.ok(convertToResponse(group.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching group", e);
            throw new RuntimeException("Failed to fetch group");
        }
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@RequestBody Map<String, Object> request) {
        try {
            String groupName = (String) request.get("groupName");
            Long departmentId = getLongValue(request.get("departmentId"));
            
            if (groupName == null || groupName.trim().isEmpty()) {
                throw new BadRequestException("Group name is required");
            }
            if (departmentId == null) {
                throw new BadRequestException("Department ID is required");
            }
            
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(departmentId);
            if (departmentOpt.isEmpty()) {
                throw new BadRequestException("Department not found with id: " + departmentId);
            }
            
            Groups group = new Groups();
            group.setGroup_name(groupName);
            group.setDepartment(departmentOpt.get());
            
            Groups savedGroup = groupsService.saveGroup(group);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedGroup));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating group", e);
            throw new BadRequestException("Failed to create group: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Optional<Groups> groupOpt = groupsService.getGroupById(id);
            if (groupOpt.isEmpty()) {
                throw new ResourceNotFoundException("Group not found with id: " + id);
            }
            
            String groupName = (String) request.get("groupName");
            Long departmentId = getLongValue(request.get("departmentId"));
            
            Groups group = groupOpt.get();
            
            if (groupName != null && !groupName.trim().isEmpty()) {
                group.setGroup_name(groupName);
            }
            
            if (departmentId != null) {
                Optional<Departments> departmentOpt = departmentService.getDepartmentById(departmentId);
                if (departmentOpt.isEmpty()) {
                    throw new BadRequestException("Department not found with id: " + departmentId);
                }
                group.setDepartment(departmentOpt.get());
            }
            
            Groups updatedGroup = groupsService.saveGroup(group);
            
            return ResponseEntity.ok(convertToResponse(updatedGroup));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating group", e);
            throw new BadRequestException("Failed to update group: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        try {
            Optional<Groups> groupOpt = groupsService.getGroupById(id);
            if (groupOpt.isEmpty()) {
                throw new ResourceNotFoundException("Group not found with id: " + id);
            }
            
            boolean deleted = groupsService.deleteGroup(id);
            if (!deleted) {
                throw new BadRequestException("Cannot delete group with students");
            }
            
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting group", e);
            throw new BadRequestException("Failed to delete group: " + e.getMessage());
        }
    }

    private GroupResponse convertToResponse(Groups group) {
        DepartmentResponse departmentResponse = group.getDepartment() != null
            ? new DepartmentResponse(group.getDepartment().getId(), 
                                    group.getDepartment().getDepartmentName(), null)
            : null;
            
        int studentCount = group.getStudentsList() != null ? group.getStudentsList().size() : 0;
        
        return new GroupResponse(group.getId(), group.getGroup_name(), 
                               departmentResponse, studentCount);
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
