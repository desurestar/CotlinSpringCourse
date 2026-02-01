package kaf.pin.lab1corp.DTO.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public class ResearchTeamResponse {
    private Long id;
    private String name;
    private String description;
    private EmployeeResponse leader;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private List<TeamMemberResponse> members;
    private List<TeamResearchWorkResponse> works;

    public ResearchTeamResponse() {}

    public ResearchTeamResponse(Long id, String name, String description, EmployeeResponse leader, 
                               LocalDateTime createdAt, List<TeamMemberResponse> members, 
                               List<TeamResearchWorkResponse> works) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.createdAt = createdAt;
        this.members = members;
        this.works = works;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmployeeResponse getLeader() {
        return leader;
    }

    public void setLeader(EmployeeResponse leader) {
        this.leader = leader;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TeamMemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMemberResponse> members) {
        this.members = members;
    }

    public List<TeamResearchWorkResponse> getWorks() {
        return works;
    }

    public void setWorks(List<TeamResearchWorkResponse> works) {
        this.works = works;
    }
}
