package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.ResearchTeam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchTeamRepository extends CrudRepository<ResearchTeam, Long> {

    List<ResearchTeam> findByLeaderId(Long leaderId);

    @Query("SELECT DISTINCT rt FROM ResearchTeam rt LEFT JOIN FETCH rt.members")
    List<ResearchTeam> findAllWithMembers();
}
