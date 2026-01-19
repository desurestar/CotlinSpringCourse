package kaf.pin.lab1corp.DTO.response;

public class DepartmentResponse {
    private Long id;
    private String departmentName;
    private Integer employeeCount;

    public DepartmentResponse() {}

    public DepartmentResponse(Long id, String departmentName, Integer employeeCount) {
        this.id = id;
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }
}
