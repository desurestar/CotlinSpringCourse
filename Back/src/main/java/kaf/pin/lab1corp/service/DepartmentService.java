package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.repository.DepartmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentsRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentsRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Departments> getAllDepartments() {

        return (List<Departments>) departmentRepository.findAll();
    }

    public Optional<Departments> getDepartmentById(Long id) {

        return departmentRepository.findById(id);
    }

    public Departments saveDepartment(Departments department) {

        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {

        departmentRepository.deleteById(id);
    }

    public List<Departments> searchDepartments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDepartments();
        }
        return departmentRepository.findByDepartmentNameContainsIgnoreCase(keyword);
    }
}