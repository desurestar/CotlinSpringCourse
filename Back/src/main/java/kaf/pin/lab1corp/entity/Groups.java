package kaf.pin.lab1corp.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@Table(name = "Groups")
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String group_name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Departments department;

    @OneToMany(mappedBy = "groups",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Students> studentsList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public List<Students> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(List<Students> studentsList) {
        this.studentsList = studentsList;
    }
/*private Long id;
    private String groupName;
    private Departments department;*/


    /*public Groups() {}

    public Groups(Long id, String groupName, Departments department) {
        this.id = id;
        this.groupName = groupName;
        this.department = department;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Departments getDepartment() { return department; }
    public void setDepartment(Departments department) { this.department = department; }*/
}
