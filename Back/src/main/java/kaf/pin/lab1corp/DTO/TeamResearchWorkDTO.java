package kaf.pin.lab1corp.DTO;

import java.time.LocalDate;

public class TeamResearchWorkDTO {
    private Long teamId;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;

    public TeamResearchWorkDTO() {}

    public TeamResearchWorkDTO(Long teamId, String title, String description, String status, LocalDate startDate) {
        this.teamId = teamId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
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
}
