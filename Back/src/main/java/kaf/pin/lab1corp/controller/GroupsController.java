package kaf.pin.lab1corp.controller;

import jakarta.validation.Valid;
import kaf.pin.lab1corp.DTO.GroupCreateDTO;
import kaf.pin.lab1corp.DTO.GroupUpdateDTO;
import kaf.pin.lab1corp.entity.Departments;
import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.service.DepartmentService;
import kaf.pin.lab1corp.service.GroupsService;
import kaf.pin.lab1corp.service.StudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/groups")
public class GroupsController {

    private final GroupsService groupsService;
    private final DepartmentService departmentService;
    private final StudentsService studentsService;

    @Autowired
    public GroupsController(GroupsService groupsService, DepartmentService departmentService, StudentsService studentsService) {
        this.groupsService = groupsService;
        this.departmentService = departmentService;
        this.studentsService = studentsService;
    }

    @GetMapping("/")
    public String groupsPage(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Groups> groups;

        if (search != null && !search.trim().isEmpty()) {
            groups = groupsService.searchGroups(search);
            model.addAttribute("searchQuery", search);
        } else {
            groups = groupsService.getAllGroups();
        }

        model.addAttribute("groups", groups);
        return "groups";
    }

    @GetMapping("/{id}")
    public String groupDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "search", required = false) String search,
                              Model model) {
        Optional<Groups> group = groupsService.getGroupById(id);
        if (group.isPresent()) {
            model.addAttribute("group", group.get());

            List<Students> students;

            if (search != null && !search.trim().isEmpty()) {
                students = studentsService.searchStudents(search).stream()
                        .filter(std -> std.getGroups() != null && std.getGroups().getId().equals(id))
                        .toList();
                model.addAttribute("searchQuery", search);
            } else {
                students = studentsService.getStudentsByGroupId(id);
            }

            model.addAttribute("students", students);
            return "group-details";
        }
        return "redirect:/groups/";
    }

    @GetMapping("/add")
    public String addGroupForm(Model model) {
        List<Departments> departments = departmentService.getAllDepartments();

        model.addAttribute("groupDTO", new GroupCreateDTO());
        model.addAttribute("departments", departments);

        return "add-group";
    }

    @PostMapping("/add")
    public String addGroupSubmit(@ModelAttribute("groupDTO") @Valid GroupCreateDTO groupDTO, Model model, BindingResult result) {
        if (result.hasErrors()) {
            return "add-group";
        }
        try {
            Optional<Departments> departmentOpt = departmentService.getDepartmentById(groupDTO.getDepartmentId());

            if (departmentOpt.isEmpty()) {
                model.addAttribute("error", "Выбранная кафедра не найдена");
                return reloadForm(model);
            }

            Groups group = new Groups();
            group.setGroup_name(groupDTO.getGroupName());
            group.setDepartment(departmentOpt.get());

            groupsService.saveGroup(group);

            return "redirect:/groups/?success=Группа успешно добавлена";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при добавлении группы: " + e.getMessage());
            return reloadForm(model);
        }
    }

    @GetMapping("/edit/{id}")
    public String editGroupForm(@PathVariable Long id, Model model) {
        Optional<Groups> group = groupsService.getGroupById(id);

        if (group.isPresent()) {
            List<Departments> departments = departmentService.getAllDepartments();

            GroupUpdateDTO dto = new GroupUpdateDTO();
            dto.setGroupName(group.get().getGroup_name());

            if (group.get().getDepartment() != null) {
                dto.setDepartmentId(group.get().getDepartment().getId());
            }

            model.addAttribute("groupDTO", dto);
            model.addAttribute("departments", departments);
            model.addAttribute("groupId", id);

            return "edit-group";
        } else {
            return "redirect:/groups/?error=Группа не найдена";
        }
    }

    @PostMapping("/edit/{id}")
    public String editGroupSubmit(@PathVariable Long id, @ModelAttribute("groupDTO") @Valid GroupUpdateDTO groupDTO, Model model, BindingResult result) {
        if (result.hasErrors()) {
            return "edit-group";
        }
        try {
            Optional<Groups> existingGroup = groupsService.getGroupById(id);

            if (existingGroup.isPresent()) {
                Groups grp = existingGroup.get();
                grp.setGroup_name(groupDTO.getGroupName());

                if (groupDTO.getDepartmentId() != null) {
                    Optional<Departments> department = departmentService.getDepartmentById(groupDTO.getDepartmentId());
                    department.ifPresent(grp::setDepartment);
                }

                groupsService.saveGroup(grp);

                return "redirect:/groups/";
            }

            model.addAttribute("error", "Группа не найдена");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении: " + e.getMessage());
        }

        return reloadEditForm(model, id);
    }

    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable("id") Long id) {
        try {
            boolean deleted = groupsService.deleteGroup(id);
            if (deleted) {
                return "redirect:/groups/?success=group_deleted";
            } else {
                return "redirect:/groups/?error=cannot_delete_group_with_students";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groups/?error=delete_failed";
        }
    }

    @GetMapping("/department/{departmentId}")
    public String groupsByDepartment(@PathVariable("departmentId") Long departmentId, Model model) {
        Optional<Departments> department = departmentService.getDepartmentById(departmentId);
        if (department.isPresent()) {
            List<Groups> groups = groupsService.getGroupsByDepartmentId(departmentId);
            model.addAttribute("department", department.get());
            model.addAttribute("groups", groups);
            return "groups-by-department";
        }
        return "redirect:/groups/";
    }

    private String reloadForm(Model model) {
        List<Departments> departments = departmentService.getAllDepartments();

        model.addAttribute("groupDTO", new GroupCreateDTO());
        model.addAttribute("departments", departments);

        return "add-group";
    }

    private String reloadEditForm(Model model, Long groupId) {
        Optional<Groups> group = groupsService.getGroupById(groupId);

        if (group.isPresent()) {
            List<Departments> departments = departmentService.getAllDepartments();

            model.addAttribute("group", group.get());
            model.addAttribute("departments", departments);

            return "edit-group";
        }

        return "redirect:/groups/";
    }
}
