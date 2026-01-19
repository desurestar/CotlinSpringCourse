package kaf.pin.lab1corp.DTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;



public class UserCreateDTO {
    @NotBlank(message= "Пароль не должен быть пустым")
    @Size(min = 8, message = "Пароль должен состоять минимум из 8 символов")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат Email")
    private String email;

    public UserCreateDTO(String password, String email) {
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
