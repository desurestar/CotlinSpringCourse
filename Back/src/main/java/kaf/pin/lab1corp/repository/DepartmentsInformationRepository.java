// DepartmentsInformationRepository.java
package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.DepartmentInformation;
import org.springframework.data.repository.CrudRepository;

public interface DepartmentsInformationRepository extends CrudRepository<DepartmentInformation, Long> {
}