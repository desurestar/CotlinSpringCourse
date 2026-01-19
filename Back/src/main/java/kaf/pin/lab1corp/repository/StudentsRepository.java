package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Students;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentsRepository extends CrudRepository<Students, Long> {

    List<Students> findByGroupsId(Long groupId);

    List<Students> findByNameContainsIgnoreCase(String name);

    @Query("SELECT s FROM Students s LEFT JOIN FETCH s.groups LEFT JOIN FETCH s.user")
    List<Students> findAllWithDetails();
}
