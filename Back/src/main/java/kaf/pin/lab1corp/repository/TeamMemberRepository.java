package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.TeamMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends CrudRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    boolean existsByTeamIdAndEmployeeId(Long teamId, Long employeeId);

    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);
}
