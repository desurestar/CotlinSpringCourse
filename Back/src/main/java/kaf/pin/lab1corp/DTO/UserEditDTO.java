package kaf.pin.lab1corp.DTO;

import jakarta.validation.constraints.*;

public class UserEditDTO {
    @NotBlank(message= "Пароль не должен быть пустым")
    @Size(min = 8, message = "Пароль должен состоять минимум из 8 символов")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат Email")
    private String email;

    public UserEditDTO(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public @NotBlank(message = "Пароль не должен быть пустым") @Size(min = 8, message = "Пароль должен состоять минимум из 8 символов") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Пароль не должен быть пустым") @Size(min = 8, message = "Пароль должен состоять минимум из 8 символов") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Email не может быть пустым") @Email(message = "Некорректный формат Email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email не может быть пустым") @Email(message = "Некорректный формат Email") String email) {
        this.email = email;
    }
}
