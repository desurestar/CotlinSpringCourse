package kaf.pin.lab1corp.controller.api;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.ArticleCreateDTO;
import kaf.pin.lab1corp.entity.Article;
import kaf.pin.lab1corp.service.ArticleService;
import kaf.pin.lab1corp.service.UserService;
import kaf.pin.lab1corp.repository.EmployesRepository;
import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final UserService userService;
    private final EmployesRepository employesRepository;

    @Autowired
    public ArticleRestController(ArticleService articleService,
                                 UserService userService,
                                 EmployesRepository employesRepository) {
        this.articleService = articleService;
        this.userService = userService;
        this.employesRepository = employesRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++==");
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
    
    @PostMapping()
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
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, Authentication authentication) {
        System.out.println("SERVER: DELETE /api/articles/" + id + " called");
        try {
            // If user has ADMIN role - allow
            if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                articleService.deleteArticle(id);
                System.out.println("SERVER: Article " + id + " deleted by ADMIN");
                return ResponseEntity.noContent().build();
            }

            // Otherwise verify that authenticated user corresponds to the main author
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = authentication.getName();
            Users user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Employes employee = employesRepository.findByUserId(user.getId()).orElse(null);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Check ownership
            Optional<Article> optArticle = articleService.getArticleById(id);
            if (optArticle.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Article article = optArticle.get();
            if (article.getMainAuthor() != null && article.getMainAuthor().getId().equals(employee.getId())) {
                articleService.deleteArticle(id);
                System.out.println("SERVER: Article " + id + " deleted by author " + employee.getId());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            System.out.println("SERVER: Error deleting article " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
