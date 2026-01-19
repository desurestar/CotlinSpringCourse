/*
package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.repository.StudentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentsService {
    private final StudentsRepository studentsRepository;

    public StudentsService(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }


    public List<Students> getAllStudents() {
        return studentsRepository.findAll();
    }


    public Students getStudentById(Long id) {
        return studentsRepository.findById(id);
    }


    @Transactional
    public Students createStudent(Students student) {
        validateStudent(student);
        studentsRepository.save(student);
        return student;
    }


    @Transactional
    public Students updateStudent(Long id, Students student) {
        Students existingStudent = studentsRepository.findById(id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Студент с ID " + id + " не найден");
        }

        student.setId(id);
        validateStudent(student);
        studentsRepository.update(student);

        return student;
    }

    @Transactional
    public boolean deleteStudent(Long id) {
        Students student = studentsRepository.findById(id);
        if (student == null) {
            return false;
        }

        studentsRepository.deleteById(id);
        return true;
    }

    public List<Students> getStudentsByGroupId(Long groupId) {
        return studentsRepository.findByGroupId(groupId);
    }

    private void validateStudent(Students student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя студента не может быть пустым");
        }

        if (student.getName().length() > 255) {
            throw new IllegalArgumentException("Имя студента слишком длинное");
        }

        if (student.getUserId() != null && student.getUserId() <= 0) {
            throw new IllegalArgumentException("Некорректный user_id");
        }
    }

}*/
