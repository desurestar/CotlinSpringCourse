package kaf.pin.lab1corp.DTO.response;

public class GroupResponse {
    private Long id;
    private String groupName;
    private DepartmentResponse department;
    private Integer studentCount;

    public GroupResponse() {}

    public GroupResponse(Long id, String groupName, DepartmentResponse department, Integer studentCount) {
        this.id = id;
        this.groupName = groupName;
        this.department = department;
        this.studentCount = studentCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public DepartmentResponse getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponse department) {
        this.department = department;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
