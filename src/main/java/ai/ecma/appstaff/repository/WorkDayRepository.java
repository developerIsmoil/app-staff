package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkDayRepository extends JpaRepository<WorkDay, UUID> {

    List<WorkDay> findAllByActiveTrue();

    List<WorkDay> findAllByActiveFalse();

}
