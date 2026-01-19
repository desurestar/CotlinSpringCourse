package kaf.pin.lab1corp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmployeeUpdateDTO {
    @NotBlank
    @Size(min =2, max = 25, message = "Имя должно содержать от 2-х до 25 символов")
    private String name;
    @NotNull(message = "Поле кафедры не может быть пустым")
    private Long departmentId;
    @NotNull(message = "Поле должности не может быть пустым")
    private Long postId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}