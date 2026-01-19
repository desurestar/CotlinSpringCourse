package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.repository.StudentsRepository;
import kaf.pin.lab1corp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentsService {

    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public StudentsService(StudentsRepository studentsRepository, UsersRepository usersRepository) {
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
    }

    public List<Students> getAllStudents() {
        return (List<Students>) studentsRepository.findAll();
    }

    public Optional<Students> getStudentById(Long id) {
        return studentsRepository.findById(id);
    }

    public Students saveStudent(Students student) {
        if (student.getUser() != null && student.getUser().getId() == null) {
            Users savedUser = usersRepository.save(student.getUser());
            student.setUser(savedUser);
        }
        return studentsRepository.save(student);
    }

    @Transactional
    public boolean safeDeleteStudent(Long studentId) {
        try {
            Optional<Students> student = studentsRepository.findById(studentId);
            if (student.isPresent()) {
                Students std = student.get();

                studentsRepository.delete(std);

                if (std.getUser() != null) {
                    usersRepository.delete(std.getUser());
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Students> getStudentsByGroupId(Long groupId) {
        return studentsRepository.findByGroupsId(groupId);
    }

    public List<Students> searchStudents(String searchTerm) {
        return studentsRepository.findByNameContainsIgnoreCase(searchTerm);
    }
}
