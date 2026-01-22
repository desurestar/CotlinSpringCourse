package kaf.pin.lab1corp.controller;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.EmployeeUpdateDTO;
import kaf.pin.lab1corp.DTO.EmployesCreateDTO;
import kaf.pin.lab1corp.entity.Article;
import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.entity.Post;
import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.service.ArticleService;
import kaf.pin.lab1corp.service.DepartmentService;
import kaf.pin.lab1corp.service.EmployeeService;
import kaf.pin.lab1corp.service.PostService;
import kaf.pin.lab1corp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employe")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PostService postService;
    private final UserService userService;
    private final ArticleService articleService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, DepartmentService departmentService, PostService postService, UserService userService, ArticleService articleService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.postService = postService;
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping("/info/{id}")
    public String informEmployeeForm(@PathVariable("id") Long id, Model model) {
        Optional<Employes> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            List<Departments> departments = departmentService.getAllDepartments();
            List<Post> posts = postService.getAllPosts();
            List<Employes> allEmployees = employeeService.getAllEmployees();
            List<Article> articles = articleService.getArticlesByEmployee(id);
            
            model.addAttribute("employee", employee.get());
            model.addAttribute("posts", posts);
            model.addAttribute("departments", departments);
            model.addAttribute("allEmployees", allEmployees);
            model.addAttribute("articles", articles);
            return "employee-detales";
        }
        return "redirect:/departments/";
    }

    @GetMapping("/add")
    public String addEmployeeForm(Model model) {
        List<Departments> departments = departmentService.getAllDepartments();
        List<Post> posts = postService.getAllPosts();

        model.addAttribute("employeeDTO", new EmployesCreateDTO());
        model.addAttribute("departments", departments);
        model.addAttribute("post", posts);

        return "add-employee";
    }

    @PostMapping("/add")
    public String addEmployeeSubmit(@ModelAttribute("employeeDTO") @Valid EmployesCreateDTO employeeDTO, Model model, BindingResult result) {
        if(result.hasErrors()){
            return "add-employee";
        }
        try {
            Optional<Post> postOpt = postService.getPostById(employeeDTO.getPostId());
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(employeeDTO.getDepartmentId());

            if (postOpt.isEmpty() || departmentOpt.isEmpty()) {
                model.addAttribute("error", "Выбранные кафедра или должность не найдены");
                return reloadForm(model);
            }
            Users user = null;
            if (employeeDTO.getEmail() != null && !employeeDTO.getEmail().isEmpty() &&
                    employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {

                Optional<Users> existingUser = userService.findByEmail(employeeDTO.getEmail());
                if (existingUser.isPresent()) {
                    model.addAttribute("error", "Пользователь с email " + employeeDTO.getEmail() + " уже существует");
                    return reloadForm(model);
                }

                user = new Users();
                user.setEmail(employeeDTO.getEmail());
                user.setPassword(employeeDTO.getPassword());
                user.setRole(employeeDTO.getRole() != null ? employeeDTO.getRole() : "EMPLOYEE");

                user = userService.saveUser(user);
            }

            Employes employee = new Employes();
            employee.setName(employeeDTO.getName());
            employee.setPost(postOpt.get());
            employee.setDepartment(departmentOpt.get());

            if (user != null) {
                employee.setUser(user);
            }

            employeeService.saveEmployee(employee);

            return "redirect:/departments/?success=Сотрудник успешно добавлен";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при добавлении сотрудника: " + e.getMessage());
            return reloadForm(model);
        }
    }

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        Optional<Employes> employee = employeeService.getEmployeeById(id);

        if (employee.isPresent()) {
            List<Departments> departments = departmentService.getAllDepartments();
            List<Post> posts = postService.getAllPosts();

            EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
            dto.setName(employee.get().getName());

            if (employee.get().getDepartment() != null) {
                dto.setDepartmentId(employee.get().getDepartment().getId());
            }

            if (employee.get().getPost() != null) {
                dto.setPostId(employee.get().getPost().getId());
            }

            model.addAttribute("employeeDTO", dto);
            model.addAttribute("departments", departments);
            model.addAttribute("posts", posts);
            model.addAttribute("employeeId", id);

            return "edit-employee";
        } else {
            return "redirect:/departments/?error=Сотрудник не найден";
        }
    }

    @PostMapping("/edit/{id}")
    public String editEmployeeSubmit(@PathVariable Long id, @ModelAttribute("employeeDTO") @Valid EmployeeUpdateDTO employeeDTO, Model model, BindingResult result) {
        if(result.hasErrors()){
            return "edit-employee";
        }
        try {
            Optional<Employes> existingEmployee = employeeService.getEmployeeById(id);

            if (existingEmployee.isPresent()) {
                Employes emp = existingEmployee.get();
                emp.setName(employeeDTO.getName());

                if (employeeDTO.getDepartmentId() != null) {
                    Optional<Departments> department = departmentService.getDepartmentById(employeeDTO.getDepartmentId());
                    department.ifPresent(emp::setDepartment);
                }

                if (employeeDTO.getPostId() != null) {
                    Optional<Post> post = postService.getPostById(employeeDTO.getPostId());
                    post.ifPresent(emp::setPost);
                }

                employeeService.saveEmployee(emp);

                return "redirect:/departments/";
            }

            model.addAttribute("error", "Сотрудник не найден");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении: " + e.getMessage());
        }

        return reloadEditForm(model, id);
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        try {
            boolean deleted = employeeService.safeDeleteEmployee(id);
            if (deleted) {
                return "redirect:/departments/?success=employee_deleted";
            } else {
                return "redirect:/departments/?error=employee_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/departments/?error=delete_failed";
        }
    }

    @GetMapping("/details/{id}")
    public String employeeDetails(@PathVariable("id") Long id, Model model) {
        Optional<Employes> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employee-details";
        }
        return "redirect:/departments/";
    }

    private String reloadForm(Model model) {
        List<Departments> departments = departmentService.getAllDepartments();
        List<Post> posts = postService.getAllPosts();

        model.addAttribute("employeeDTO", new EmployesCreateDTO());
        model.addAttribute("departments", departments);
        model.addAttribute("post", posts);

        return "add-employee";
    }

    private String reloadEditForm(Model model, Long employeeId) {
        // Логика перезагрузки формы с данными сотрудника
        Optional<Employes> employee = employeeService.getEmployeeById(employeeId);

        if (employee.isPresent()) {
            List<Departments> departments = departmentService.getAllDepartments();
            List<Post> posts = postService.getAllPosts();

            model.addAttribute("employee", employee.get());
            model.addAttribute("departments", departments);
            model.addAttribute("posts", posts);

            return "edit-employee";
        }

        return "redirect:/departments/";
    }
}