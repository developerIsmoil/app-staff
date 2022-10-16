package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.EmploymentInfo;
import ai.ecma.appstaff.entity.TimeSheet;
import ai.ecma.appstaff.entity.TimeSheetEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetEmployeeRepository extends JpaRepository<TimeSheetEmployee, UUID> {

    List<TimeSheetEmployee> findAllByTimeSheetIn(Collection<TimeSheet> TimeSheet);

    List<TimeSheetEmployee> findAllByTimeSheet(TimeSheet TimeSheet);

    List<TimeSheetEmployee> findAllByEmploymentInfoIn(Collection<EmploymentInfo> employmentInfo);

    @Transactional
    @Modifying
    void deleteAllByEmploymentInfo_Id(UUID employmentInfo_id);


    List<TimeSheetEmployee> findAllByEmploymentInfoInAndTimeSheetIn(Collection<EmploymentInfo> employmentInfo, Collection<TimeSheet> timeSheet);

}
