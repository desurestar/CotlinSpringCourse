/*
// GroupsMapper.java
package kaf.pin.lab1corp.mapper;

import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.entity.Departments;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class GroupsMapper implements RowMapper<Groups> {
    @Override
    public Groups mapRow(ResultSet rs, int rowNum) throws SQLException {
        Groups group = new Groups();
        group.setId(rs.getLong("id"));
        group.setGroupName(rs.getString("group_name"));

        Departments department = new Departments();
        department.setId(rs.getLong("department_id"));

        group.setDepartment(department);

        return group;
    }
}*/
