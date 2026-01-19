/*
package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.mapper.StudentMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class StudentsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final StudentMapper studentMapper;

    public StudentsRepository(JdbcTemplate jdbcTemplate, StudentMapper studentMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentMapper = studentMapper;
    }

    public List<Students> findAll() {
        String sql = "SELECT * FROM Students";
        return jdbcTemplate.query(sql, studentMapper);
    }

    public Students findById(Long id) {
        String sql = "SELECT * FROM Students WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, studentMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void save(Students student) {
        String sql = "INSERT INTO Students (name, group_id, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                student.getName(),
                student.getGroup() != null ? student.getGroup().getId() : null,
                student.getUserId());
    }

    public void update(Students student) {
        String sql = "UPDATE Students SET name = ?, group_id = ?, user_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                student.getName(),
                student.getGroup() != null ? student.getGroup().getId() : null,
                student.getUserId(),
                student.getId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM Students WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Students> findByGroupId(Long groupId) {
        String sql = "SELECT * FROM Students WHERE group_id = ?";
        return jdbcTemplate.query(sql, studentMapper, groupId);
    }
}*/
