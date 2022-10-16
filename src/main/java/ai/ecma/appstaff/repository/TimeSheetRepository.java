package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Department;
import ai.ecma.appstaff.entity.TimeSheet;
import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import ai.ecma.appstaff.projection.ITimeSheet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, UUID> {

    List<TimeSheet> findAllByDepartment_Id(UUID department_id);

    List<TimeSheet> findAllByDateAndDepartmentIdInAndTimeSheetStatusAndConfirmDateIsNull(Date date, Collection<UUID> department_id, TimeSheetStatusEnum TimeSheetStatus);

    boolean existsByTimeSheetStatusAndDepartmentAndDate(TimeSheetStatusEnum timeSheetStatus, Department department, Date date);

    List<TimeSheet> findAllByDate(Date date);

    List<TimeSheet> findAllByDateAndDepartmentIn(Date date, Collection<Department> departments);
    List<TimeSheet> findAllByDateAndDepartment_Id(Date date, UUID department_id);
    List<TimeSheet> findAllByDepartmentIn(Collection<Department> department, Sort sort);


    @Query(
            value = "SELECT CAST(t.id AS VARCHAR)                 AS id,\n" +
                    "       COALESCE(SUM(te.addition_salary), 0)  AS additionSalary,\n" +
                    "       COALESCE(SUM(te.advance_salary), 0)   AS advanceSalary,\n" +
                    "       COALESCE(SUM(te.bonus), 0)            AS bonus,\n" +
                    "       COALESCE(SUM(te.paid_salary), 0)      AS paidSalary,\n" +
                    "       COALESCE(SUM(te.premium), 0)          AS premium,\n" +
                    "       COALESCE(SUM(te.retention_salary), 0) AS retentionSalary,\n" +
                    "       COALESCE(SUM(te.salary), 0)           AS salary,\n" +
                    "       COALESCE(SUM(te.tax_amount), 0)       AS taxAmount,\n" +
                    "       COALESCE(SUM(te.total_salary), 0)     AS totalSalary,\n" +
                    "       COALESCE(SUM(te.work_days), 0)        AS workDays,\n" +
                    "       COALESCE(SUM(te.worked_days), 0)      AS workedDays,\n" +
                    "       COUNT(te.employment_info_id)          AS employeeCount,\n" +
                    "       t.timesheet_status                   AS timeSheetStatus,\n" +
                    "       t.date                                AS timeSheetDate\n" +
                    "FROM timesheet t\n" +
                    "         JOIN timesheet_employee te ON t.id = te.timesheet_id\n" +
                    "GROUP BY t.id;",
            nativeQuery = true
    )
    List<ITimeSheet> getAllTimeSheet();


    @Query(
            value = "SELECT COALESCE(SUM(te.work_days), 0)    AS workDays,\n" +
                    "       COALESCE(SUM(te.worked_days), 0)  AS workedDays,\n" +
                    "       COALESCE(SUM(te.work_hours), 0)   AS workHours,\n" +
                    "       COALESCE(SUM(te.worked_hours), 0) AS workedHours,\n" +
                    "       COUNT(te.employment_info_id)      AS employeeCount,\n" +
                    "       EXTRACT(epoch FROM t.date) * 1000 AS id,\n" +
                    "       CAST(t.date AS VARCHAR)           AS month \n" +
                    "FROM timesheet t\n" +
                    "         JOIN timesheet_employee te ON t.id = te.timesheet_id\n" +
                    "GROUP BY t.date;",
            nativeQuery = true
    )
    List<ITimeSheet> getAllTimeSheetForAllDepartmentsManageUser();

    @Query(
            value = "SELECT COALESCE(SUM(te.work_days), 0)    AS workDays,\n" +
                    "       COALESCE(SUM(te.worked_days), 0)  AS workedDays,\n" +
                    "       COALESCE(SUM(te.work_hours), 0)   AS workHours,\n" +
                    "       COALESCE(SUM(te.worked_hours), 0) AS workedHours,\n" +
                    "       COUNT(te.employment_info_id)      AS employeeCount,\n" +
                    "       EXTRACT(epoch FROM t.date) * 1000 AS id," +
                    "       CAST(t.date AS VARCHAR)           AS month \n" +
                    "FROM timesheet t\n" +
                    "         JOIN timesheet_employee te ON t.id = te.timesheet_id\n" +
                    "where department_id in (:departmentIdList)\n" +
                    "GROUP BY t.date;",
            nativeQuery = true
    )
    List<ITimeSheet> getAllTimeSheetForSomeDepartmentsManageUser(
            @Param("departmentIdList") List<UUID> departmentIdList
    );

    boolean existsByDepartmentId(UUID department_id);

    List<TimeSheet> findAllByTimeSheetStatus(TimeSheetStatusEnum timeSheetStatus);
}
