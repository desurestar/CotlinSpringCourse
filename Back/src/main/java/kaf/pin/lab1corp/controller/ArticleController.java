package kaf.pin.lab1corp.controller;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.ArticleCreateDTO;
import kaf.pin.lab1corp.entity.Article;
import kaf.pin.lab1corp.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/create")
    public String createArticle(@Valid @ModelAttribute ArticleCreateDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Ошибка валидации данных");
            return "redirect:/employe/info/" + dto.getMainAuthorId() + "?error=validation";
        }
        
        try {
            Article article = new Article();
            article.setTitle(dto.getTitle());
            article.setDescription(dto.getDescription());
            article.setExternalLink(dto.getExternalLink());
            
            // Parse publication date from string
            if (dto.getPublicationDate() != null && !dto.getPublicationDate().isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dto.getPublicationDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                    article.setPublicationDate(date);
                } catch (DateTimeParseException e) {
                    // Log error and continue without setting date
                    System.err.println("Failed to parse publication date: " + dto.getPublicationDate());
                }
            }
            
            articleService.createArticle(article, dto.getMainAuthorId(), dto.getCoauthorIds());
            
            return "redirect:/employe/info/" + dto.getMainAuthorId() + "?success=article_created";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/employe/info/" + dto.getMainAuthorId() + "?error=creation_failed";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, @RequestParam Long employeeId) {
        try {
            articleService.deleteArticle(id);
            return "redirect:/employe/info/" + employeeId + "?success=article_deleted";
        } catch (Exception e) {
            return "redirect:/employe/info/" + employeeId + "?error=deletion_failed";
        }
    }
}
