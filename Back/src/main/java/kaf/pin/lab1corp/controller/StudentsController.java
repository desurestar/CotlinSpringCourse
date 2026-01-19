package kaf.pin.lab1corp.controller;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.StudentCreateDTO;
import kaf.pin.lab1corp.DTO.StudentUpdateDTO;
import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.service.GroupsService;
import kaf.pin.lab1corp.service.StudentsService;
import kaf.pin.lab1corp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentsController {

    private final StudentsService studentsService;
    private final GroupsService groupsService;
    private final UserService userService;

    @Autowired
    public StudentsController(StudentsService studentsService, GroupsService groupsService, UserService userService) {
        this.studentsService = studentsService;
        this.groupsService = groupsService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String studentsPage(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Students> students;

        if (search != null && !search.trim().isEmpty()) {
            students = studentsService.searchStudents(search);
            model.addAttribute("searchQuery", search);
        } else {
            students = studentsService.getAllStudents();
        }

        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/info/{id}")
    public String studentInfo(@PathVariable("id") Long id, Model model) {
        Optional<Students> student = studentsService.getStudentById(id);
        if (student.isPresent()) {
            List<Groups> groups = groupsService.getAllGroups();
            model.addAttribute("student", student.get());
            model.addAttribute("groups", groups);
            return "student-details";
        }
        return "redirect:/students/";
    }

    @GetMapping("/add")
    public String addStudentForm(Model model) {
        List<Groups> groups = groupsService.getAllGroups();

        model.addAttribute("studentDTO", new StudentCreateDTO());
        model.addAttribute("groups", groups);

        return "add-student";
    }

    @PostMapping("/add")
    public String addStudentSubmit(@ModelAttribute("studentDTO") @Valid StudentCreateDTO studentDTO, Model model, BindingResult result) {
        if (result.hasErrors()) {
            return "add-student";
        }
        try {
            Optional<Groups> groupOpt = groupsService.getGroupById(studentDTO.getGroupId());

            if (groupOpt.isEmpty()) {
                model.addAttribute("error", "Выбранная группа не найдена");
                return reloadForm(model);
            }

            Users user = null;
            if (studentDTO.getEmail() != null && !studentDTO.getEmail().isEmpty() &&
                    studentDTO.getPassword() != null && !studentDTO.getPassword().isEmpty()) {

                Optional<Users> existingUser = userService.findByEmail(studentDTO.getEmail());
                if (existingUser.isPresent()) {
                    model.addAttribute("error", "Пользователь с email " + studentDTO.getEmail() + " уже существует");
                    return reloadForm(model);
                }

                user = new Users();
                user.setEmail(studentDTO.getEmail());
                user.setPassword(studentDTO.getPassword());
                user.setRole(studentDTO.getRole() != null ? studentDTO.getRole() : "STUDENT");

                user = userService.saveUser(user);
            }

            Students student = new Students();
            student.setName(studentDTO.getName());
            student.setGroups(groupOpt.get());

            if (user != null) {
                student.setUser(user);
            }

            studentsService.saveStudent(student);

            return "redirect:/students/?success=Студент успешно добавлен";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при добавлении студента: " + e.getMessage());
            return reloadForm(model);
        }
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Optional<Students> student = studentsService.getStudentById(id);

        if (student.isPresent()) {
            List<Groups> groups = groupsService.getAllGroups();

            StudentUpdateDTO dto = new StudentUpdateDTO();
            dto.setName(student.get().getName());

            if (student.get().getGroups() != null) {
                dto.setGroupId(student.get().getGroups().getId());
            }

            model.addAttribute("studentDTO", dto);
            model.addAttribute("groups", groups);
            model.addAttribute("studentId", id);

            return "edit-student";
        } else {
            return "redirect:/students/?error=Студент не найден";
        }
    }

    @PostMapping("/edit/{id}")
    public String editStudentSubmit(@PathVariable Long id, @ModelAttribute("studentDTO") @Valid StudentUpdateDTO studentDTO, Model model, BindingResult result) {
        if (result.hasErrors()) {
            return "edit-student";
        }
        try {
            Optional<Students> existingStudent = studentsService.getStudentById(id);

            if (existingStudent.isPresent()) {
                Students std = existingStudent.get();
                std.setName(studentDTO.getName());

                if (studentDTO.getGroupId() != null) {
                    Optional<Groups> group = groupsService.getGroupById(studentDTO.getGroupId());
                    group.ifPresent(std::setGroups);
                }

                studentsService.saveStudent(std);

                return "redirect:/students/";
            }

            model.addAttribute("error", "Студент не найден");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении: " + e.getMessage());
        }

        return reloadEditForm(model, id);
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        try {
            boolean deleted = studentsService.safeDeleteStudent(id);
            if (deleted) {
                return "redirect:/students/?success=student_deleted";
            } else {
                return "redirect:/students/?error=student_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/students/?error=delete_failed";
        }
    }

    @GetMapping("/group/{groupId}")
    public String studentsByGroup(@PathVariable("groupId") Long groupId, Model model) {
        Optional<Groups> group = groupsService.getGroupById(groupId);
        if (group.isPresent()) {
            List<Students> students = studentsService.getStudentsByGroupId(groupId);
            model.addAttribute("group", group.get());
            model.addAttribute("students", students);
            return "students-by-group";
        }
        return "redirect:/students/";
    }

    private String reloadForm(Model model) {
        List<Groups> groups = groupsService.getAllGroups();

        model.addAttribute("studentDTO", new StudentCreateDTO());
        model.addAttribute("groups", groups);

        return "add-student";
    }

    private String reloadEditForm(Model model, Long studentId) {
        Optional<Students> student = studentsService.getStudentById(studentId);

        if (student.isPresent()) {
            List<Groups> groups = groupsService.getAllGroups();

            model.addAttribute("student", student.get());
            model.addAttribute("groups", groups);

            return "edit-student";
        }

        return "redirect:/students/";
    }
}
