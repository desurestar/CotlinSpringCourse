package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Groups;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupsRepository extends CrudRepository<Groups, Long> {

    List<Groups> findByDepartmentId(Long departmentId);

    @Query("SELECT g FROM Groups g WHERE LOWER(g.group_name) LIKE LOWER(CONCAT('%', :groupName, '%'))")
    List<Groups> findByGroupNameContainingIgnoreCase(@Param("groupName") String groupName);

    @Query("SELECT g FROM Groups g LEFT JOIN FETCH g.department LEFT JOIN FETCH g.studentsList")
    List<Groups> findAllWithDetails();
}
