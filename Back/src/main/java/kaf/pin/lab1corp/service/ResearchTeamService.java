package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.*;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResearchTeamService {

    private final ResearchTeamRepository researchTeamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamResearchWorkRepository teamResearchWorkRepository;
    private final EmployesRepository employesRepository;
    private final StudentsRepository studentsRepository;

    @Autowired
    public ResearchTeamService(ResearchTeamRepository researchTeamRepository,
                              TeamMemberRepository teamMemberRepository,
                              TeamResearchWorkRepository teamResearchWorkRepository,
                              EmployesRepository employesRepository,
                              StudentsRepository studentsRepository) {
        this.researchTeamRepository = researchTeamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamResearchWorkRepository = teamResearchWorkRepository;
        this.employesRepository = employesRepository;
        this.studentsRepository = studentsRepository;
    }

    // CRUD для коллективов
    public List<ResearchTeam> getAllTeams() {
        return (List<ResearchTeam>) researchTeamRepository.findAll();
    }

    public Optional<ResearchTeam> getTeamById(Long id) {
        return researchTeamRepository.findById(id);
    }

    public ResearchTeam createTeam(String name, String description, Long leaderId) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Team name is required");
        }
        if (leaderId == null) {
            throw new BadRequestException("Leader ID is required");
        }

        Optional<Employes> leaderOpt = employesRepository.findById(leaderId);
        if (leaderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Leader not found with id: " + leaderId);
        }

        ResearchTeam team = new ResearchTeam();
        team.setName(name);
        team.setDescription(description);
        team.setLeader(leaderOpt.get());

        return researchTeamRepository.save(team);
    }

    public ResearchTeam updateTeam(Long id, String name, String description, Long leaderId) {
        Optional<ResearchTeam> teamOpt = researchTeamRepository.findById(id);
        if (teamOpt.isEmpty()) {
            throw new ResourceNotFoundException("Research team not found with id: " + id);
        }

        ResearchTeam team = teamOpt.get();

        if (name != null && !name.trim().isEmpty()) {
            team.setName(name);
        }

        if (description != null) {
            team.setDescription(description);
        }

        if (leaderId != null) {
            Optional<Employes> leaderOpt = employesRepository.findById(leaderId);
            if (leaderOpt.isEmpty()) {
                throw new ResourceNotFoundException("Leader not found with id: " + leaderId);
            }
            team.setLeader(leaderOpt.get());
        }

        return researchTeamRepository.save(team);
    }

    public boolean deleteTeam(Long id) {
        if (!researchTeamRepository.existsById(id)) {
            return false;
        }
        researchTeamRepository.deleteById(id);
        return true;
    }

    // Добавить/удалить участника
    public TeamMember addEmployeeToTeam(Long teamId, Long employeeId, String role) {
        Optional<ResearchTeam> teamOpt = researchTeamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new ResourceNotFoundException("Research team not found with id: " + teamId);
        }

        Optional<Employes> employeeOpt = employesRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        if (teamMemberRepository.existsByTeamIdAndEmployeeId(teamId, employeeId)) {
            throw new BadRequestException("Employee is already a member of this team");
        }

        TeamMember member = new TeamMember();
        member.setTeam(teamOpt.get());
        member.setEmployee(employeeOpt.get());
        member.setRole(role != null ? role : "MEMBER");

        return teamMemberRepository.save(member);
    }

    public TeamMember addStudentToTeam(Long teamId, Long studentId, String role) {
        Optional<ResearchTeam> teamOpt = researchTeamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new ResourceNotFoundException("Research team not found with id: " + teamId);
        }

        Optional<Students> studentOpt = studentsRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        if (teamMemberRepository.existsByTeamIdAndStudentId(teamId, studentId)) {
            throw new BadRequestException("Student is already a member of this team");
        }

        TeamMember member = new TeamMember();
        member.setTeam(teamOpt.get());
        member.setStudent(studentOpt.get());
        member.setRole(role != null ? role : "MEMBER");

        return teamMemberRepository.save(member);
    }

    public boolean removeMember(Long memberId) {
        if (!teamMemberRepository.existsById(memberId)) {
            return false;
        }
        teamMemberRepository.deleteById(memberId);
        return true;
    }

    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    // CRUD для научных работ
    public TeamResearchWork createWork(Long teamId, String title, String description, 
                                       String status, LocalDate startDate) {
        Optional<ResearchTeam> teamOpt = researchTeamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new ResourceNotFoundException("Research team not found with id: " + teamId);
        }

        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException("Work title is required");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("Work description is required");
        }

        TeamResearchWork work = new TeamResearchWork();
        work.setTeam(teamOpt.get());
        work.setTitle(title);
        work.setDescription(description);
        work.setStatus(status != null ? status : "IN_PROGRESS");
        work.setStartDate(startDate);

        return teamResearchWorkRepository.save(work);
    }

    public List<TeamResearchWork> getTeamWorks(Long teamId) {
        return teamResearchWorkRepository.findByTeamId(teamId);
    }

    public Optional<TeamResearchWork> getWorkById(Long id) {
        return teamResearchWorkRepository.findById(id);
    }

    public TeamResearchWork updateWork(Long id, String title, String description, 
                                      String status, LocalDate startDate) {
        Optional<TeamResearchWork> workOpt = teamResearchWorkRepository.findById(id);
        if (workOpt.isEmpty()) {
            throw new ResourceNotFoundException("Research work not found with id: " + id);
        }

        TeamResearchWork work = workOpt.get();

        if (title != null && !title.trim().isEmpty()) {
            work.setTitle(title);
        }

        if (description != null && !description.trim().isEmpty()) {
            work.setDescription(description);
        }

        if (status != null) {
            work.setStatus(status);
        }

        if (startDate != null) {
            work.setStartDate(startDate);
        }

        return teamResearchWorkRepository.save(work);
    }

    public boolean deleteWork(Long id) {
        if (!teamResearchWorkRepository.existsById(id)) {
            return false;
        }
        teamResearchWorkRepository.deleteById(id);
        return true;
    }

    public List<ResearchTeam> getTeamsByLeaderId(Long leaderId) {
        return researchTeamRepository.findByLeaderId(leaderId);
    }

    public List<ResearchTeam> getAllTeamsWithMembers() {
        return researchTeamRepository.findAllWithMembers();
    }
}
