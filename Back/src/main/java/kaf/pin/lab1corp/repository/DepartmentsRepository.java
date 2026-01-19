// DepartmentsRepository.java
package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Departments;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface DepartmentsRepository extends CrudRepository<Departments, Long> {
    List<Departments> findByDepartmentNameContainsIgnoreCase(String departmentName);
}