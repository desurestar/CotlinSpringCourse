package kaf.pin.lab1corp.DTO.response;

public class EmployeeResponse {
    private Long id;
    private String name;
    private PostResponse post;
    private DepartmentResponse department;
    private UserResponse user;

    public EmployeeResponse() {}

    public EmployeeResponse(Long id, String name, PostResponse post, DepartmentResponse department, UserResponse user) {
        this.id = id;
        this.name = name;
        this.post = post;
        this.department = department;
        this.user = user;
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

    public PostResponse getPost() {
        return post;
    }

    public void setPost(PostResponse post) {
        this.post = post;
    }

    public DepartmentResponse getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponse department) {
        this.department = department;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
