package kaf.pin.lab1corp.DTO;

public class TeamMemberDTO {
    private Long teamId;
    private Long employeeId;
    private Long studentId;
    private String role;

    public TeamMemberDTO() {}

    public TeamMemberDTO(Long teamId, Long employeeId, Long studentId, String role) {
        this.teamId = teamId;
        this.employeeId = employeeId;
        this.studentId = studentId;
        this.role = role;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
