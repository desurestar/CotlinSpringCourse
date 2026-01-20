package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.TeamResearchWork;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamResearchWorkRepository extends CrudRepository<TeamResearchWork, Long> {

    List<TeamResearchWork> findByTeamId(Long teamId);

    List<TeamResearchWork> findByStatus(String status);
}
