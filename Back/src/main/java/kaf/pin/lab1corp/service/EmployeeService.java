package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.repository.EmployesRepository;
import kaf.pin.lab1corp.repository.PostRepository;
import kaf.pin.lab1corp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private final EmployesRepository employeeRepository;
    private final UsersRepository usersRepository;
    private final PostRepository postRepository;

    @Autowired
    public EmployeeService(EmployesRepository employeeRepository, UsersRepository usersRepository, PostRepository postRepository) {
        this.employeeRepository = employeeRepository;
        this.usersRepository = usersRepository;
        this.postRepository = postRepository;
    }

    public List<Employes> getAllEmployees() {
        return (List<Employes>) employeeRepository.findAll();
    }

    public Optional<Employes> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employes saveEmployee(Employes employee) {
        if (employee.getUser() != null && employee.getUser().getId() == null) {
            Users savedUser = usersRepository.save(employee.getUser());
            employee.setUser(savedUser);
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public boolean safeDeleteEmployee(Long employeeId) {
        try {
            Optional<Employes> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                Employes emp = employee.get();

                employeeRepository.delete(emp);

                if (emp.getUser() != null) {
                    usersRepository.delete(emp.getUser());
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Employes> getEmployeesByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }


    public List<Employes> searchEmployees(String searchTerm) {
        // Простой поиск только по имени
        return employeeRepository.findByNameContainsIgnoreCase(searchTerm);
    }

    

}