package kaf.pin.lab1corp.DTO.response;

public class StudentResponse {
    private Long id;
    private String name;
    private GroupResponse group;
    private UserResponse user;

    public StudentResponse() {}

    public StudentResponse(Long id, String name, GroupResponse group, UserResponse user) {
        this.id = id;
        this.name = name;
        this.group = group;
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

    public GroupResponse getGroup() {
        return group;
    }

    public void setGroup(GroupResponse group) {
        this.group = group;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
