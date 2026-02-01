package kaf.pin.lab1corp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ArticleCreateDTO {
    
    @NotBlank(message = "Название статьи обязательно")
    @Size(min = 3, max = 255, message = "Название должно быть от 3 до 255 символов")
    private String title;
    
    @NotBlank(message = "Описание статьи обязательно")
    @Size(min = 10, message = "Описание должно быть не менее 10 символов")
    private String description;
    
    @Size(max = 500, message = "Ссылка не может превышать 500 символов")
    private String externalLink;
    
    @NotNull(message = "Основной автор обязателен")
    private Long mainAuthorId;
    
    private List<Long> coauthorIds;
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getExternalLink() { return externalLink; }
    public void setExternalLink(String externalLink) { this.externalLink = externalLink; }
    
    public Long getMainAuthorId() { return mainAuthorId; }
    public void setMainAuthorId(Long mainAuthorId) { this.mainAuthorId = mainAuthorId; }
    
    public List<Long> getCoauthorIds() { return coauthorIds; }
    public void setCoauthorIds(List<Long> coauthorIds) { this.coauthorIds = coauthorIds; }
}
