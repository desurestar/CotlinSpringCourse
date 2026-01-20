package kaf.pin.lab1corp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "Departments")
public class Departments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "departmentName", nullable = false)
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Employes> employesList;
    @OneToOne(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private DepartmentInformation departmentInformation;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Groups> groupsList;

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

    public List<Employes> getEmployesList() {
        return employesList;
    }

    public void setEmployesList(List<Employes> employesList) {
        this.employesList = employesList;
    }

    public DepartmentInformation getDepartmentInformation() {
        return departmentInformation;
    }

    public void setDepartmentInformation(DepartmentInformation departmentInformation) {
        this.departmentInformation = departmentInformation;
    }
}
