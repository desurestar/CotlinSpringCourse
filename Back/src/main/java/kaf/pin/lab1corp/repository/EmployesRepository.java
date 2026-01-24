package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Employes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployesRepository extends CrudRepository<Employes, Long> {

    List<Employes> findByDepartmentId(Long departmentId);

    List<Employes> findByNameContainsIgnoreCase(String name);

    @Query("SELECT e FROM Employes e WHERE e.user.email LIKE %:email%")
    List<Employes> findByUserEmailContainingIgnoreCase(@Param("email") String email);

    @Query("SELECT e FROM Employes e WHERE LOWER(e.post.post_name) LIKE LOWER(CONCAT('%', :post_name, '%'))")
    List<Employes> findByPostNameContainingIgnoreCase(@Param("post_name") String postName);

    @Query("SELECT e FROM Employes e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.post.post_name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employes> searchByNameOrPostName(@Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Employes e WHERE e.department.id = :departmentId")
    List<Employes> findByDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT e FROM Employes e LEFT JOIN FETCH e.post LEFT JOIN FETCH e.department")
    List<Employes> findAllWithDetails();

    @Query("SELECT e FROM Employes e WHERE e.user.id = :userId")
    java.util.Optional<Employes> findByUserId(@Param("userId") Long userId);
}