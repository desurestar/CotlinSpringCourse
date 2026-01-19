package kaf.pin.lab1corp.DTO;

import jakarta.validation.constraints.*;

public class EmployesCreateDTO {
    @NotBlank
    @Size(min =2, max = 25, message = "Имя должно содержать от 2-х до 25 символов")
    private String name;
    @NotNull(message = "Поле должности не может быть пустым")
    private Long postId;
    @NotNull(message = "Поле кафедры не может быть пустым")
    private Long departmentId;
    @Email
    @NotBlank(message = "E-mail не может быть пустым")
    private String email;
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
    @NotBlank(message = "Поле роли не может быть пустым")
    private String role;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
