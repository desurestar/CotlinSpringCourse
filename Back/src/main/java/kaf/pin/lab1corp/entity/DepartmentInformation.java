package kaf.pin.lab1corp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "DepartmentsInformation")
public class DepartmentInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "headDepartment", nullable = false)
    private String headDepartment;
    @Column(name = "deputyHeadDepartment", nullable = false)
    private String deputyHeadDepartment;
    @Column(name = "departmentInformation", nullable = false)
    private String departmentInformation;

    @OneToOne
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Departments department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadDepartment() {
        return headDepartment;
    }

    public void setHeadDepartment(String headDepartment) {
        this.headDepartment = headDepartment;
    }

    public String getDeputyHeadDepartment() {
        return deputyHeadDepartment;
    }

    public void setDeputyHeadDepartment(String deputyHeadDepartment) {
        this.deputyHeadDepartment = deputyHeadDepartment;
    }

    public String getDepartmentInformation() {
        return departmentInformation;
    }

    public void setDepartmentInformation(String departmentInformation) {
        this.departmentInformation = departmentInformation;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }
}
