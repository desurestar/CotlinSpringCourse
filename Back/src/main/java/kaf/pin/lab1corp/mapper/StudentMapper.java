/*
package kaf.pin.lab1corp.mapper;

import kaf.pin.lab1corp.entity.Students;
import kaf.pin.lab1corp.entity.Groups;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StudentMapper implements RowMapper<Students> {
    @Override
    public Students mapRow(ResultSet rs, int rowNum) throws SQLException {
        Students student = new Students();
        student.setId(rs.getLong("id"));
        student.setName(rs.getString("name"));
        student.setUserId(rs.getLong("user_id"));

        Groups group = new Groups();
        group.setId(rs.getLong("group_id"));
        student.setGroup(group);

        return student;
    }
}*/
