package kaf.pin.lab1corp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GroupUpdateDTO {
    @NotBlank(message = "Название группы не может быть пустым")
    @Size(min = 2, max = 50, message = "Название группы должно содержать от 2 до 50 символов")
    private String groupName;

    @NotNull(message = "Поле кафедры не может быть пустым")
    private Long departmentId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
