INSERT INTO departments (department_name) VALUES
('Department PIN'),
('Department IS'),
('Department PMI'),
('Department IB'),
('Department RT');

INSERT INTO post (post_name) VALUES
('Department Head'),
('Teacher'),
('Deputy Department Head');

INSERT INTO users (email, password, role, enabled) VALUES
('john.doe@company.com', 'password123', 'EMPLOYEE', true),
('jane.smith@company.com', 'password123', 'EMPLOYEE', true),
('mike.johnson@company.com', 'password123', 'EMPLOYEE', true),
('sarah.wilson@company.com', 'password123', 'ADMIN', true),
('robert.brown@company.com', 'password123', 'ADMIN', true),
('emily.davis@company.com', 'password123', 'EMPLOYEE', true),
('admin@institute.ru', 'password123', 'ADMIN', true),
('manager@institute.ru', 'password123', 'EMPLOYEE', true),
('employee@institute.ru', 'password123', 'EMPLOYEE', true),
('student1@institute.ru', 'password123', 'STUDENT', true),
('student2@institute.ru', 'password123', 'STUDENT', true),
('student3@institute.ru', 'password123', 'STUDENT', true),
('student4@institute.ru', 'password123', 'STUDENT', true),
('student5@institute.ru', 'password123', 'STUDENT', true);

INSERT INTO groups (group_name, department_id) VALUES
('PIN-122', 1),
('IS-122', 2),
('PMI-122', 3),
('IB-122', 4),
('RT-122', 5);

INSERT INTO students (name, group_id, user_id) VALUES
('student1', 1, 10),
('student2', 2, 11),
('student3', 3, 12),
('student4', 4, 13),
('student5', 5, 14);

INSERT INTO employes (name, post_id, department_id, user_id) VALUES
('John Doe', 1, 1, 1),
('Jane Smith', 2, 2, 2),
('Mike Johnson', 2, 3, 3),
('Sarah Wilson', 3, 1, 4),
('Robert Brown', 2, 4, 5),
('Emily Davis', 2, 5, 6),
('Администратор', 1, 1, 7),
('Менеджер', 2, 2, 8),
('Сотрудник', 2, 3, 9);

INSERT INTO departments_information (head_department, deputy_head_department, department_information, department_id) VALUES
('Sarah Wilson', 'John Doe', 'Department PIN', 1),
('Jane Smith', 'Assistant Manager', 'Department IS', 2),
('Mike Johnson', 'Senior Accountant', 'Department PMI', 3),
('Robert Brown', 'Marketing Assistant', 'Department IB', 4),
('Emily Davis', 'Senior Researcher', 'Department RT', 5);