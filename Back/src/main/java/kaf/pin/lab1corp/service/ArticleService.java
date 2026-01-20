package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Article;
import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.repository.ArticleRepository;
import kaf.pin.lab1corp.repository.EmployesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final EmployesRepository employesRepository;
    
    @Autowired
    public ArticleService(ArticleRepository articleRepository, EmployesRepository employesRepository) {
        this.articleRepository = articleRepository;
        this.employesRepository = employesRepository;
    }
    
    public List<Article> getAllArticles() {
        return articleRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }
    
    public List<Article> getArticlesByEmployee(Long employeeId) {
        return articleRepository.findAllByEmployeeId(employeeId);
    }
    
    public List<Article> getArticlesByMainAuthor(Long authorId) {
        return articleRepository.findByMainAuthorId(authorId);
    }
    
    public List<Article> searchArticles(String query) {
        return articleRepository.findByTitleContainingIgnoreCase(query);
    }
    
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }
    
    public Article createArticle(Article article, Long mainAuthorId, List<Long> coauthorIds) {
        // Установить основного автора
        Optional<Employes> mainAuthor = employesRepository.findById(mainAuthorId);
        if (mainAuthor.isEmpty()) {
            throw new RuntimeException("Основной автор не найден");
        }
        article.setMainAuthor(mainAuthor.get());
        
        // Установить соавторов
        if (coauthorIds != null && !coauthorIds.isEmpty()) {
            List<Employes> coauthors = new ArrayList<>();
            for (Long coauthorId : coauthorIds) {
                employesRepository.findById(coauthorId).ifPresent(coauthors::add);
            }
            article.setCoauthors(coauthors);
        }
        
        return articleRepository.save(article);
    }
    
    public Article updateArticle(Long id, Article updatedArticle, List<Long> coauthorIds) {
        Optional<Article> existingArticle = articleRepository.findById(id);
        if (existingArticle.isEmpty()) {
            throw new RuntimeException("Статья не найдена");
        }
        
        Article article = existingArticle.get();
        article.setTitle(updatedArticle.getTitle());
        article.setDescription(updatedArticle.getDescription());
        article.setExternalLink(updatedArticle.getExternalLink());
        article.setPublicationDate(updatedArticle.getPublicationDate());
        
        // Обновить соавторов
        if (coauthorIds != null) {
            List<Employes> coauthors = new ArrayList<>();
            for (Long coauthorId : coauthorIds) {
                employesRepository.findById(coauthorId).ifPresent(coauthors::add);
            }
            article.setCoauthors(coauthors);
        }
        
        return articleRepository.save(article);
    }
    
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
