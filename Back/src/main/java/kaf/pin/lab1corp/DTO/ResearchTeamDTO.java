package kaf.pin.lab1corp.DTO;

public class ResearchTeamDTO {
    private String name;
    private String description;
    private Long leaderId;

    public ResearchTeamDTO() {}

    public ResearchTeamDTO(String name, String description, Long leaderId) {
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
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

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }
}
