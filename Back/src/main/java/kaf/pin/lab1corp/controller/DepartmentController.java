package kaf.pin.lab1corp.controller;

import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.entity.Employes;
import kaf.pin.lab1corp.service.DepartmentService;
import kaf.pin.lab1corp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String departmentPage(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Departments> departments;

        if (search != null && !search.trim().isEmpty()) {
            departments = departmentService.searchDepartments(search);
            model.addAttribute("searchQuery", search);
        } else {
            departments = departmentService.getAllDepartments();
        }

        model.addAttribute("departments", departments);
        return "department";
    }

    @GetMapping("/{id}")
    public String departmentDetails(@PathVariable("id") Long id,
                                    @RequestParam(value = "search", required = false) String search,
                                    Model model) {
        Optional<Departments> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());

            List<Employes> employees;

            if (search != null && !search.trim().isEmpty()) {
                employees = employeeService.searchEmployees(search).stream()
                        .filter(emp -> emp.getDepartment() != null && emp.getDepartment().getId().equals(id))
                        .toList();
                model.addAttribute("searchQuery", search);
            } else {
                employees = employeeService.getEmployeesByDepartmentId(id);
            }

            model.addAttribute("employees", employees);
            return "department-details";
        }
        return "redirect:/departments/";
    }

    @PostMapping("/{id}/search")
    public String searchEmployeesInDepartment(@PathVariable("id") Long id,
                                              @RequestParam("search") String search,
                                              Model model) {
        if (search == null || search.trim().isEmpty()) {
            return "redirect:/departments/" + id;
        }

        Optional<Departments> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());

            List<Employes> employees = employeeService.searchEmployees(search).stream()
                    .filter(emp -> emp.getDepartment() != null && emp.getDepartment().getId().equals(id))
                    .toList();

            model.addAttribute("employees", employees);
            model.addAttribute("searchQuery", search);
            return "department-details";
        }

        return "redirect:/departments/";
    }

    @GetMapping("/add")
    public String addDepartmentForm(Model model) {
        model.addAttribute("department", new Departments());
        return "add-department";
    }

    @GetMapping("/edit/{id}")
    public String editDepartmentForm(@PathVariable("id") Long id, Model model) {
        Optional<Departments> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "edit-department";
        }
        return "redirect:/departments/";
    }

    @PostMapping("/edit/{id}")
    public String editDepartmentSubmit(@PathVariable("id") Long id,
                                       @RequestParam("department.id") Long departmentId) {
        try {
            // Здесь должна быть логика обновления отдела
            return "redirect:/departments/";
        } catch (Exception e) {
            return "redirect:/departments/edit/" + id + "?error=true";
        }
    }
}