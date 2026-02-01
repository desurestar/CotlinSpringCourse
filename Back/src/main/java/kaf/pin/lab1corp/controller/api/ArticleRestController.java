package kaf.pin.lab1corp.controller.api;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.ArticleCreateDTO;
import kaf.pin.lab1corp.entity.Article;
import kaf.pin.lab1corp.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleRestController {
    
    private final ArticleService articleService;
    
    @Autowired
    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }
    
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleService.getArticleById(id);
        return article.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Article>> getArticlesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(articleService.getArticlesByEmployee(employeeId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(@RequestParam String query) {
        return ResponseEntity.ok(articleService.searchArticles(query));
    }
    
    @PostMapping
    public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleCreateDTO dto) {
        try {
            System.out.println("SERVER: Creating article with data: " + dto);
            System.out.println("  Title: " + dto.getTitle());
            System.out.println("  Description: " + dto.getDescription());
            System.out.println("  Main Author ID: " + dto.getMainAuthorId());
            System.out.println("  Coauthor IDs: " + dto.getCoauthorIds());
            
            Article article = new Article();
            article.setTitle(dto.getTitle());
            article.setDescription(dto.getDescription());
            article.setExternalLink(dto.getExternalLink());
            
            // Publication date is nullable and can be set later if needed
            // It's not required during creation
            
            Article savedArticle = articleService.createArticle(
                article, 
                dto.getMainAuthorId(), 
                dto.getCoauthorIds()
            );
            
            System.out.println("SERVER: Article created successfully with ID: " + savedArticle.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
        } catch (Exception e) {
            System.err.println("SERVER: Error creating article: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleCreateDTO dto) {
        try {
            Article article = new Article();
            article.setTitle(dto.getTitle());
            article.setDescription(dto.getDescription());
            article.setExternalLink(dto.getExternalLink());
            
            // Publication date is not updated - it preserves the existing value
            
            Article updatedArticle = articleService.updateArticle(id, article, dto.getCoauthorIds());
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        System.out.println("SERVER: DELETE /api/articles/" + id + " called");
        try {
            articleService.deleteArticle(id);
            System.out.println("SERVER: Article " + id + " deleted successfully");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println("SERVER: Error deleting article " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
