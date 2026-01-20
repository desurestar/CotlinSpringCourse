package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
    
    // Найти статьи по основному автору
    List<Article> findByMainAuthorId(Long mainAuthorId);
    
    // Найти статьи где сотрудник является соавтором
    @Query("SELECT a FROM Article a JOIN a.coauthors c WHERE c.id = :employeeId")
    List<Article> findByCoauthorId(@Param("employeeId") Long employeeId);
    
    // Найти все статьи сотрудника (как автор или соавтор)
    @Query("SELECT DISTINCT a FROM Article a LEFT JOIN a.coauthors c " +
           "WHERE a.mainAuthor.id = :employeeId OR c.id = :employeeId " +
           "ORDER BY a.createdAt DESC")
    List<Article> findAllByEmployeeId(@Param("employeeId") Long employeeId);
    
    // Поиск по названию
    List<Article> findByTitleContainingIgnoreCase(String title);
    
    // Получить все статьи с сортировкой по дате
    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    List<Article> findAllOrderByCreatedAtDesc();
}
