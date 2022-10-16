package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.EmployeeAttendance;
import ai.ecma.appstaff.entity.TimeSheetEmployee;
import ai.ecma.appstaff.projection.IEmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, UUID> {

    @Query(
            value = "SELECT COUNT(*)                  AS workDay,\n" +
                    "       SUM(ea.work_hour)         AS workHour,\n" +
                    "       CAST(ea.timesheet_employee_id AS VARCHAR) AS timeSheetEmployeeId\n" +
                    "FROM employee_attendance ea\n" +
                    "WHERE ea.attendance = 'WORKING'\n" +
                    "  AND ea.timesheet_employee_id IN\n" +
                    "      (SELECT id FROM timesheet_employee WHERE timesheet_id IN (:timeSheetIdList))\n" +
                    "GROUP BY ea.timesheet_employee_id;",
            nativeQuery = true
    )
    List<IEmployeeAttendance> findAllTimeSheetEmployeeWorkdayAndWorkHour(
            @Param("timeSheetIdList") List<UUID> timeSheetIdList
    );

    @Query(
            value = "SELECT COUNT(*)          AS workDay,\n" +
                    "       SUM(ea.work_hour) AS workHour\n" +
                    "FROM employee_attendance ea\n" +
                    "WHERE ea.attendance = (:attendance)\n" +
                    "  AND cast(ea.timesheet_employee_id AS VARCHAR) = cast((:timeSheetEmployeeId) AS VARCHAR)",
            nativeQuery = true
    )
    IEmployeeAttendance findTimeSheetEmployeeByIdWorkdayAndWorkHour(
            @Param("timeSheetEmployeeId") String timeSheetEmployeeId,
            @Param("attendance") String attendance
    );


    List<EmployeeAttendance> findAllByTimeSheetEmployeeInAndDayIn(Collection<TimeSheetEmployee> timeSheetEmployee, Collection<Date> day);

    @Query(
            value = "SELECT attendance             AS attendanceEnum,\n" +
                    "       work_hour              AS workHourD,\n" +
                    "       CAST(timesheet_employee_id AS VARCHAR) AS timeSheetEmployeeIdS,\n" +
                    "       day                    AS day\n" +
                    "FROM employee_attendance\n" +
                    "WHERE timesheet_employee_id IN (:uuiIdList) ORDER BY day ASC;",
            nativeQuery = true
    )
    List<IEmployeeAttendance> findAllByTimeSheetEmployeeIdIn(
            @Param("uuiIdList") List<UUID> uuidList
    );


    void deleteAllByTimeSheetEmployee_Id(UUID timeSheetEmployee_id);

    @Transactional
    @Modifying
    @Query(
            value = "UPDATE employee_attendance\n" +
                    "SET deleted= true\n" +
                    "WHERE timesheet_employee_id IN\n" +
                    "      (SELECT id FROM timesheet_employee WHERE timesheet_employee.employment_info_id IN :employmentInfoId)",
            nativeQuery = true
    )
    void deleteAllByTimeSheetEmployeeByEmploymentInfoIdIn(
            @Param("employmentInfoId") List<UUID> employmentInfoId
    );

    Optional<EmployeeAttendance> findByTimeSheetEmployeeIdAndDay(UUID timeSheetEmployee_id, Date day);
}
