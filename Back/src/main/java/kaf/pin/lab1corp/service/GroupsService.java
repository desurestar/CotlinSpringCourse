package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Groups;
import kaf.pin.lab1corp.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupsService {

    private final GroupsRepository groupsRepository;

    @Autowired
    public GroupsService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    public List<Groups> getAllGroups() {
        return (List<Groups>) groupsRepository.findAll();
    }

    public Optional<Groups> getGroupById(Long id) {
        return groupsRepository.findById(id);
    }

    public Groups saveGroup(Groups group) {
        return groupsRepository.save(group);
    }

    @Transactional
    public boolean deleteGroup(Long id) {
        try {
            Optional<Groups> group = groupsRepository.findById(id);
            if (group.isPresent()) {
                Groups grp = group.get();
                
                // Check if group has students
                if (grp.getStudentsList() != null && !grp.getStudentsList().isEmpty()) {
                    return false; // Cannot delete group with students
                }
                
                groupsRepository.delete(grp);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Groups> getGroupsByDepartmentId(Long departmentId) {
        return groupsRepository.findByDepartmentId(departmentId);
    }

    public List<Groups> searchGroups(String searchTerm) {
        return groupsRepository.findByGroupNameContainingIgnoreCase(searchTerm);
    }
}
