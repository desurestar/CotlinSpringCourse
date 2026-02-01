package kaf.pin.lab1corp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private ResearchTeam team;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employes employee;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Students student;

    @Column(name = "role")
    private String role = "MEMBER";

    @Column(name = "joined_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResearchTeam getTeam() {
        return team;
    }

    public void setTeam(ResearchTeam team) {
        this.team = team;
    }

    public Employes getEmployee() {
        return employee;
    }

    public void setEmployee(Employes employee) {
        this.employee = employee;
    }

    public Students getStudent() {
        return student;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
