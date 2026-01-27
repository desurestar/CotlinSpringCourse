package kaf.pin.lab1corp.controller;

import kaf.pin.lab1corp.entity.*;
import kaf.pin.lab1corp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PostService postService;
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private StudentsService studentsService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ResearchTeamService researchTeamService;
    @Autowired
    private ArticleService articleService;

    // ========== EMPLOYEE MANAGEMENT ==========

    @GetMapping("/employees")
    public String listEmployees(Model model,
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) Long departmentId) {
        List<Employes> employees;

        if (departmentId != null) {
            employees = employeeService.getEmployeesByDepartmentId(departmentId);
        } else if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployees(search);
        } else {
            employees = employeeService.getAllEmployees();
        }

        List<Departments> departments = departmentService.getAllDepartments();

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        model.addAttribute("selectedDepartmentId", departmentId);

        return "admin/employees/list";
    }

    @GetMapping("/employees/new")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("posts", postService.getAllPosts());
        return "admin/employees/create";
    }

    @PostMapping("/employees")
    public String createEmployee(@RequestParam String name,
                                 @RequestParam Long postId,
                                 @RequestParam Long departmentId,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false, defaultValue = "EMPLOYEE") String role,
                                 RedirectAttributes redirectAttributes) {
        try {
            Employes employee = new Employes();
            employee.setName(name);

            // Устанавливаем пост
            Post post = postService.getPostById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Должность не найдена"));
            employee.setPost(post);

            // Устанавливаем отдел
            Departments department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Отдел не найден"));
            employee.setDepartment(department);

            // Создаем учетную запись если указаны email и пароль
            if (email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password); // В реальном приложении нужно хешировать!
                user.setRole(role);
                user.setEnabled(true);

                Users savedUser = userService.saveUser(user);
                employee.setUser(savedUser);
            }

            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно создан!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании сотрудника: " + e.getMessage());
            // Сохраняем введенные данные для повторного заполнения формы
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("postId", postId);
            redirectAttributes.addFlashAttribute("departmentId", departmentId);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("role", role);
            return "redirect:/admin/employees/new";
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.safeDeleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении сотрудника: " + e.getMessage());
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        employeeService.getEmployeeById(id).ifPresent(employee -> {
            model.addAttribute("employee", employee);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("posts", postService.getAllPosts());
        });
        return "admin/employees/edit";
    }

    @PostMapping("/employees/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam Long postId,
                                 @RequestParam Long departmentId,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false, defaultValue = "EMPLOYEE") String role,
                                 RedirectAttributes redirectAttributes) {
        try {
            Employes employee = employeeService.getEmployeeById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

            employee.setName(name);

            Post post = postService.getPostById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Должность не найдена"));
            employee.setPost(post);

            Departments department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Отдел не найден"));
            employee.setDepartment(department);

            // Обновляем учетную запись
            if (employee.getUser() != null && email != null && !email.trim().isEmpty()) {
                Users user = employee.getUser();
                user.setEmail(email);
                if (password != null && !password.trim().isEmpty()) {
                    user.setPassword(password); // В реальном приложении нужно хешировать!
                }
                user.setRole(role);
                userService.saveUser(user);
            }

            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении сотрудника: " + e.getMessage());
        }
        return "redirect:/admin/employees";
    }

    // ========== RESEARCH TEAMS MANAGEMENT ==========

    @GetMapping("/research-teams")
    public String listResearchTeams(Model model) {
        List<ResearchTeam> teams = researchTeamService.getAllTeams();
        List<Employes> employees = employeeService.getAllEmployees();

        model.addAttribute("teams", teams);
        model.addAttribute("employees", employees);
        return "admin/research-teams/list";
    }

    @GetMapping("/research-teams/new")
    public String showCreateTeamForm(Model model) {
        List<Employes> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/research-teams/create";
    }

    @PostMapping("/research-teams")
    public String createTeam(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam Long leaderId,
                             RedirectAttributes redirectAttributes) {
        try {
            // Получаем руководителя
            Employes leader = employeeService.getEmployeeById(leaderId)
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

            // Создаем команду
            ResearchTeam team = new ResearchTeam();
            team.setName(name);
            team.setDescription(description);
            team.setLeader(leader);
            team.setCreatedAt(LocalDate.now().atStartOfDay());

            researchTeamService.saveTeam(team);
            redirectAttributes.addFlashAttribute("success", "Научная группа успешно создана!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании научной группы: " + e.getMessage());
            // Сохраняем данные для повторного заполнения
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("leaderId", leaderId);
            return "redirect:/admin/research-teams/new";
        }
        return "redirect:/admin/research-teams";
    }

    @PostMapping("/research-teams/{id}/delete")
    public String deleteTeam(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            researchTeamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("success", "Научная группа успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении научной группы: " + e.getMessage());
        }
        return "redirect:/admin/research-teams";
    }

    @GetMapping("/research-teams/{id}/edit")
    public String showEditTeamForm(@PathVariable Long id, Model model) {
        researchTeamService.getTeamById(id).ifPresent(team -> {
            model.addAttribute("team", team);
            model.addAttribute("employees", employeeService.getAllEmployees());
        });
        return "admin/research-teams/edit";
    }

    @PostMapping("/research-teams/{id}")
    public String updateTeam(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam Long leaderId,
                             RedirectAttributes redirectAttributes) {
        try {
            ResearchTeam team = researchTeamService.getTeamById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Научная группа не найдена"));

            Employes leader = employeeService.getEmployeeById(leaderId)
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

            team.setName(name);
            team.setDescription(description);
            team.setLeader(leader);

            researchTeamService.saveTeam(team);
            redirectAttributes.addFlashAttribute("success", "Научная группа успешно обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении научной группы: " + e.getMessage());
        }
        return "redirect:/admin/research-teams";
    }

    // ========== STUDENT MANAGEMENT ==========

    @GetMapping("/students")
    public String listStudents(Model model,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) Long groupId) {
        List<Students> students;

        if (groupId != null) {
            students = studentsService.getStudentsByGroupId(groupId);
        } else if (search != null && !search.trim().isEmpty()) {
            students = studentsService.searchStudents(search);
        } else {
            students = studentsService.getAllStudents();
        }

        List<Groups> groups = groupsService.getAllGroups();

        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("search", search);
        model.addAttribute("selectedGroupId", groupId);

        return "admin/students/list";
    }

    @GetMapping("/students/new")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("groups", groupsService.getAllGroups());
        return "admin/students/create";
    }

    @PostMapping("/students")
    public String createStudent(@RequestParam String name,
                                @RequestParam Long groupId,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String password,
                                RedirectAttributes redirectAttributes) {
        try {
            Students student = new Students();
            student.setName(name);

            Groups group = groupsService.getGroupById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));
            student.setGroups(group);

            // Создаем учетную запись если указаны email и пароль
            if (email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password); // В реальном приложении нужно хешировать!
                user.setRole("STUDENT");
                user.setEnabled(true);

                Users savedUser = userService.saveUser(user);
                student.setUser(savedUser);
            }

            studentsService.saveStudent(student);
            redirectAttributes.addFlashAttribute("success", "Студент успешно создан!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании студента: " + e.getMessage());
            // Сохраняем данные для повторного заполнения
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("groupId", groupId);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/admin/students/new";
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            studentsService.safeDeleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Студент успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении студента: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    // ========== DEPARTMENT MANAGEMENT ==========

    @GetMapping("/departments")
    public String listDepartments(Model model,
                                  @RequestParam(required = false) String search) {
        List<Departments> departments;
        if (search != null && !search.trim().isEmpty()) {
            departments = departmentService.searchDepartments(search);
        } else {
            departments = departmentService.getAllDepartments();
        }
        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        return "admin/departments/list";
    }

    @GetMapping("/departments/new")
    public String showCreateDepartmentForm() {
        return "admin/departments/create";
    }

    @PostMapping("/departments")
    public String createDepartment(@RequestParam String departmentName,
                                   RedirectAttributes redirectAttributes) {
        try {
            Departments department = new Departments();
            department.setDepartmentName(departmentName);
            departmentService.saveDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Отдел успешно создан!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании отдела: " + e.getMessage());
            redirectAttributes.addFlashAttribute("departmentName", departmentName);
            return "redirect:/admin/departments/new";
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Отдел успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отдела: " + e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    // ========== GROUP MANAGEMENT ==========

    @GetMapping("/groups")
    public String listGroups(Model model,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) Long departmentId) {
        List<Groups> groups;

        if (departmentId != null) {
            groups = groupsService.getGroupsByDepartmentId(departmentId);
        } else if (search != null && !search.trim().isEmpty()) {
            groups = groupsService.searchGroups(search);
        } else {
            groups = groupsService.getAllGroups();
        }

        List<Departments> departments = departmentService.getAllDepartments();

        model.addAttribute("groups", groups);
        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        model.addAttribute("selectedDepartmentId", departmentId);

        return "admin/groups/list";
    }

    @GetMapping("/groups/new")
    public String showCreateGroupForm(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/groups/create";
    }

    @PostMapping("/groups")
    public String createGroup(@RequestParam String groupName,
                              @RequestParam Long departmentId,
                              RedirectAttributes redirectAttributes) {
        try {
            Groups group = new Groups();
            group.setGroup_name(groupName);

            Departments department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Отдел не найден"));
            group.setDepartment(department);

            groupsService.saveGroup(group);
            redirectAttributes.addFlashAttribute("success", "Группа успешно создана!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании группы: " + e.getMessage());
            redirectAttributes.addFlashAttribute("groupName", groupName);
            redirectAttributes.addFlashAttribute("departmentId", departmentId);
            return "redirect:/admin/groups/new";
        }
        return "redirect:/admin/groups";
    }

    @PostMapping("/groups/{id}/delete")
    public String deleteGroup(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            groupsService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("success", "Группа успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении группы: " + e.getMessage());
        }
        return "redirect:/admin/groups";
    }

    // ========== POST MANAGEMENT ==========

    @GetMapping("/posts")
    public String listPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "admin/posts/list";
    }

    @GetMapping("/posts/new")
    public String showCreatePostForm() {
        return "admin/posts/create";
    }

    @PostMapping("/posts")
    public String createPost(@RequestParam String postName,
                             RedirectAttributes redirectAttributes) {
        try {
            Post post = new Post();
            post.setPost_name(postName);
            postService.savePost(post);
            redirectAttributes.addFlashAttribute("success", "Должность успешно создана!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании должности: " + e.getMessage());
            redirectAttributes.addFlashAttribute("postName", postName);
            return "redirect:/admin/posts/new";
        }
        return "redirect:/admin/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("success", "Должность успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении должности: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }

    // ========== ARTICLE MANAGEMENT ==========

    @GetMapping("/articles")
    public String listArticles(Model model,
                               @RequestParam(required = false) String search) {
        List<Article> articles;

        if (search != null && !search.trim().isEmpty()) {
            articles = articleService.searchArticles(search);
        } else {
            articles = articleService.getAllArticles();
        }

        List<Employes> employees = employeeService.getAllEmployees();

        model.addAttribute("articles", articles);
        model.addAttribute("employees", employees);
        model.addAttribute("search", search);

        return "admin/articles/list";
    }

    @GetMapping("/articles/new")
    public String showCreateArticleForm(Model model) {
        List<Employes> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/articles/create";
    }

    @PostMapping("/articles")
    public String createArticle(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam(required = false) String externalLink,
                                @RequestParam(required = false) String publicationDate,
                                @RequestParam Long mainAuthorId,
                                @RequestParam(required = false) List<Long> coauthorIds,
                                RedirectAttributes redirectAttributes) {
        try {
            Article article = new Article();
            article.setTitle(title);
            article.setDescription(description);
            article.setExternalLink(externalLink);

            if (publicationDate != null && !publicationDate.isEmpty()) {
                article.setPublicationDate(LocalDate.parse(publicationDate));
            }

            // Получаем основного автора
            Employes mainAuthor = employeeService.getEmployeeById(mainAuthorId)
                    .orElseThrow(() -> new IllegalArgumentException("Основной автор не найден"));
            article.setMainAuthor(mainAuthor);

            articleService.saveArticle(article);

            // Добавляем соавторов если есть
            if (coauthorIds != null && !coauthorIds.isEmpty()) {
                for (Long coauthorId : coauthorIds) {
                    articleService.addCoauthor(article.getId(), coauthorId);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Публикация успешно создана!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании публикации: " + e.getMessage());
            // Сохраняем данные для повторного заполнения
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("externalLink", externalLink);
            redirectAttributes.addFlashAttribute("publicationDate", publicationDate);
            redirectAttributes.addFlashAttribute("mainAuthorId", mainAuthorId);
            return "redirect:/admin/articles/new";
        }
        return "redirect:/admin/articles";
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "Публикация успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении публикации: " + e.getMessage());
        }
        return "redirect:/admin/articles";
    }

    // ========== USER MANAGEMENT ==========

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            Users user = userService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            user.setEnabled(!user.isEnabled());
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Статус пользователя обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении статуса: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Главная страница админки
    @GetMapping
    public String adminDashboard(Model model) {
        long departmentsCount = departmentService.getAllDepartments().size();
        long employeesCount = employeeService.getAllEmployees().size();
        long studentsCount = studentsService.getAllStudents().size();
        long groupsCount = groupsService.getAllGroups().size();
        long postsCount = postService.getAllPosts().size();
        long teamsCount = researchTeamService.getAllTeams().size();
        long articlesCount = articleService.getAllArticles().size();

        model.addAttribute("departmentsCount", departmentsCount);
        model.addAttribute("employeesCount", employeesCount);
        model.addAttribute("studentsCount", studentsCount);
        model.addAttribute("groupsCount", groupsCount);
        model.addAttribute("postsCount", postsCount);
        model.addAttribute("teamsCount", teamsCount);
        model.addAttribute("articlesCount", articlesCount);

        return "admin/dashboard";
    }
}