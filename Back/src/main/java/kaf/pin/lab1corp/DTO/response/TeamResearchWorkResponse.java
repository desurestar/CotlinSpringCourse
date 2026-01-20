package kaf.pin.lab1corp.DTO.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TeamResearchWorkResponse {
    private Long id;
    private Long teamId;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDateTime createdAt;

    public TeamResearchWorkResponse() {}

    public TeamResearchWorkResponse(Long id, Long teamId, String title, String description, 
                                   String status, LocalDate startDate, LocalDateTime createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
