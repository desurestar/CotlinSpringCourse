package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.ResearchTeamDTO;
import kaf.pin.lab1corp.DTO.TeamMemberDTO;
import kaf.pin.lab1corp.DTO.TeamResearchWorkDTO;
import kaf.pin.lab1corp.DTO.response.*;
import kaf.pin.lab1corp.entity.*;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.ResearchTeamService;
import kaf.pin.lab1corp.util.RequestParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/research-teams")
@CrossOrigin(origins = "*")
public class ResearchTeamRestController {

    private final ResearchTeamService researchTeamService;
    private final Logger logger = LoggerFactory.getLogger(ResearchTeamRestController.class);

    @Autowired
    public ResearchTeamRestController(ResearchTeamService researchTeamService) {
        this.researchTeamService = researchTeamService;
    }

    @GetMapping
    public ResponseEntity<List<ResearchTeamResponse>> getAllTeams() {
        try {
            List<ResearchTeam> teams = researchTeamService.getAllTeams();
            List<ResearchTeamResponse> response = teams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching research teams", e);
            throw new BadRequestException("Failed to fetch research teams: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResearchTeamResponse> getTeamById(@PathVariable Long id) {
        try {
            Optional<ResearchTeam> team = researchTeamService.getTeamById(id);
            if (team.isEmpty()) {
                throw new ResourceNotFoundException("Research team not found with id: " + id);
            }
            return ResponseEntity.ok(convertToResponse(team.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching research team", e);
            throw new BadRequestException("Failed to fetch research team: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ResearchTeamResponse>> getTeamsByEmployee(@PathVariable Long employeeId) {
        try {
            List<ResearchTeam> teams = researchTeamService.getTeamsByEmployee(employeeId);
            List<ResearchTeamResponse> response = teams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching teams for employee: " + employeeId, e);
            throw new BadRequestException("Failed to fetch teams for employee: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ResearchTeamResponse> createTeam(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long leaderId = RequestParamUtil.getLongValue(request.get("leaderId"));

            if (name == null || name.trim().isEmpty()) {
                throw new BadRequestException("Team name is required");
            }
            if (leaderId == null) {
                throw new BadRequestException("Leader ID is required");
            }

            ResearchTeam team = researchTeamService.createTeam(name, description, leaderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(team));
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating research team", e);
            throw new BadRequestException("Failed to create research team: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResearchTeamResponse> updateTeam(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long leaderId = RequestParamUtil.getLongValue(request.get("leaderId"));

            ResearchTeam team = researchTeamService.updateTeam(id, name, description, leaderId);
            return ResponseEntity.ok(convertToResponse(team));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating research team", e);
            throw new BadRequestException("Failed to update research team: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            boolean deleted = researchTeamService.deleteTeam(id);
            if (!deleted) {
                throw new ResourceNotFoundException("Research team not found with id: " + id);
            }
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting research team", e);
            throw new BadRequestException("Failed to delete research team: " + e.getMessage());
        }
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long teamId) {
        try {
            List<TeamMember> members = researchTeamService.getTeamMembers(teamId);
            List<TeamMemberResponse> response = members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching team members", e);
            throw new BadRequestException("Failed to fetch team members: " + e.getMessage());
        }
    }

    @PostMapping("/members/employee")
    public ResponseEntity<TeamMemberResponse> addEmployeeToTeam(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = RequestParamUtil.getLongValue(request.get("teamId"));
            Long employeeId = RequestParamUtil.getLongValue(request.get("employeeId"));
            String role = (String) request.get("role");

            if (teamId == null) {
                throw new BadRequestException("Team ID is required");
            }
            if (employeeId == null) {
                throw new BadRequestException("Employee ID is required");
            }

            TeamMember member = researchTeamService.addEmployeeToTeam(teamId, employeeId, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToMemberResponse(member));
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error adding employee to team", e);
            throw new BadRequestException("Failed to add employee to team: " + e.getMessage());
        }
    }

    @PostMapping("/members/student")
    public ResponseEntity<TeamMemberResponse> addStudentToTeam(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = RequestParamUtil.getLongValue(request.get("teamId"));
            Long studentId = RequestParamUtil.getLongValue(request.get("studentId"));
            String role = (String) request.get("role");

            if (teamId == null) {
                throw new BadRequestException("Team ID is required");
            }
            if (studentId == null) {
                throw new BadRequestException("Student ID is required");
            }

            TeamMember member = researchTeamService.addStudentToTeam(teamId, studentId, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToMemberResponse(member));
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error adding student to team", e);
            throw new BadRequestException("Failed to add student to team: " + e.getMessage());
        }
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) {
        try {
            boolean deleted = researchTeamService.removeMember(memberId);
            if (!deleted) {
                throw new ResourceNotFoundException("Team member not found with id: " + memberId);
            }
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error removing team member", e);
            throw new BadRequestException("Failed to remove team member: " + e.getMessage());
        }
    }

    @GetMapping("/{teamId}/works")
    public ResponseEntity<List<TeamResearchWorkResponse>> getTeamWorks(@PathVariable Long teamId) {
        try {
            List<TeamResearchWork> works = researchTeamService.getTeamWorks(teamId);
            List<TeamResearchWorkResponse> response = works.stream()
                .map(this::convertToWorkResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching team works", e);
            throw new BadRequestException("Failed to fetch team works: " + e.getMessage());
        }
    }

    @PostMapping("/works")
    public ResponseEntity<TeamResearchWorkResponse> createWork(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = RequestParamUtil.getLongValue(request.get("teamId"));
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String status = (String) request.get("status");
            String startDateStr = (String) request.get("startDate");

            if (teamId == null) {
                throw new BadRequestException("Team ID is required");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new BadRequestException("Title is required");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new BadRequestException("Description is required");
            }

            LocalDate startDate = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }

            TeamResearchWork work = researchTeamService.createWork(teamId, title, description, status, startDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToWorkResponse(work));
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating research work", e);
            throw new BadRequestException("Failed to create research work: " + e.getMessage());
        }
    }

    @PutMapping("/works/{id}")
    public ResponseEntity<TeamResearchWorkResponse> updateWork(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String status = (String) request.get("status");
            String startDateStr = (String) request.get("startDate");

            LocalDate startDate = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }

            TeamResearchWork work = researchTeamService.updateWork(id, title, description, status, startDate);
            return ResponseEntity.ok(convertToWorkResponse(work));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating research work", e);
            throw new BadRequestException("Failed to update research work: " + e.getMessage());
        }
    }

    @DeleteMapping("/works/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        try {
            boolean deleted = researchTeamService.deleteWork(id);
            if (!deleted) {
                throw new ResourceNotFoundException("Research work not found with id: " + id);
            }
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting research work", e);
            throw new BadRequestException("Failed to delete research work: " + e.getMessage());
        }
    }

    private ResearchTeamResponse convertToResponse(ResearchTeam team) {
        EmployeeResponse leaderResponse = team.getLeader() != null 
            ? convertToEmployeeResponse(team.getLeader())
            : null;

        return new ResearchTeamResponse(
            team.getId(),
            team.getName(),
            team.getDescription(),
            leaderResponse,
            team.getCreatedAt(),
            null, // members не включаем по умолчанию для производительности
            null  // works не включаем по умолчанию для производительности
        );
    }

    private EmployeeResponse convertToEmployeeResponse(Employes employee) {
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

    private StudentResponse convertToStudentResponse(Students student) {
        GroupResponse groupResponse = student.getGroups() != null
            ? new GroupResponse(student.getGroups().getId(), 
                              student.getGroups().getGroup_name(), null, null)
            : null;
            
        UserResponse userResponse = student.getUser() != null
            ? new UserResponse(student.getUser().getId(), 
                              student.getUser().getEmail(), 
                              student.getUser().getRole())
            : null;
            
        return new StudentResponse(student.getId(), student.getName(), 
                                  groupResponse, userResponse);
    }

    private TeamMemberResponse convertToMemberResponse(TeamMember member) {
        EmployeeResponse employeeResponse = member.getEmployee() != null
            ? convertToEmployeeResponse(member.getEmployee())
            : null;
            
        StudentResponse studentResponse = member.getStudent() != null
            ? convertToStudentResponse(member.getStudent())
            : null;

        return new TeamMemberResponse(
            member.getId(),
            member.getTeam().getId(),
            employeeResponse,
            studentResponse,
            member.getRole(),
            member.getJoinedAt()
        );
    }

    private TeamResearchWorkResponse convertToWorkResponse(TeamResearchWork work) {
        return new TeamResearchWorkResponse(
            work.getId(),
            work.getTeam().getId(),
            work.getTitle(),
            work.getDescription(),
            work.getStatus(),
            work.getStartDate(),
            work.getCreatedAt()
        );
    }
}
