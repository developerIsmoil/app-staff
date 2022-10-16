package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.EmployeeWorkDay;
import ai.ecma.appstaff.entity.EmploymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeWorkDayRepository extends JpaRepository<EmployeeWorkDay, UUID> {

    List<EmployeeWorkDay> findAllByEmploymentInfoIn(Collection<EmploymentInfo> employmentInfo);

    List<EmployeeWorkDay> findAllByEmploymentInfo(EmploymentInfo employmentInfo);

    void deleteAllByEmploymentInfoId(UUID employmentInfo_id);
}
