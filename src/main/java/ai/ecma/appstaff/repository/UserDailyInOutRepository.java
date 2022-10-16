package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.UserDailyInOut;
import ai.ecma.appstaff.projection.EmployeeInOfficeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDailyInOutRepository extends JpaRepository<UserDailyInOut, UUID> {

    Optional<UserDailyInOut> findFirstByDate(Date date);

    Optional<UserDailyInOut> findByDateAndEmployee(Date date, Employee employee);

    Optional<UserDailyInOut> findByDateAndEmployeeId(Date date, UUID employeeId);

    List<UserDailyInOut> findAllByDateBetween(Date start, Date end);

    List<UserDailyInOut> findAllByDateBetweenOrderByDate(Date startDate, Date endDate);

    @Query(value = "select cast(employee_id as varchar) as employeeIdStr,\n" +
            "       in_office as inOffice\n" +
            "from user_daily_in_out where date=:date and employee_id in :empIds", nativeQuery = true)
    List<EmployeeInOfficeProjection> getCurrentTimeEmployeesInOffice(@Param("empIds") Collection<UUID> empIds,
                                                                     @Param("date") Date date);
}
