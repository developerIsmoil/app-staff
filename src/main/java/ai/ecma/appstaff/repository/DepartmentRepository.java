package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    /**
     * Shu nomli bo'lim databaseda bor yoki yo'qligi tekshirilgan
     *
     * @param name DEPARTMENT NOMI
     * @return boolean
     */
    boolean existsByName(String name);

    /**
     * Ma'lum bir ID ga tegishli bo'lmagan bo'limlar orasida shu nomlisi bor yoki yo'qligi tekshirilgan
     *
     * @param name DEPARTMENT NOMI
     * @param id   Hisobga olinmaydigan bo'limning ID si
     * @return boolean
     */
    boolean existsByNameAndIdNot(String name, UUID id);

    Slice<Department> findAllByActiveTrue(Pageable pageable);

    List<Department> findAllByActiveTrue(Sort sort);

    List<Department> findAllByActiveFalse(Sort sort);

    List<Department> findAllByActiveTrue();

    List<Department> findAllByActiveFalse();

    @Query(
            value = "SELECT *\n" +
                    "FROM department\n" +
                    "WHERE id NOT IN\n" +
                    "      (SELECT department_id\n" +
                    "       FROM timesheet AS tb\n" +
                    "       WHERE cast(tb.timesheet_status AS VARCHAR) = cast((:timeSheetStatus) AS VARCHAR)\n" +
                    "         AND cast(tb.date AS DATE) = cast((:date) AS DATE)\n" +
                    "         AND tb.deleted = FALSE)",
            nativeQuery = true
    )
    List<Department> findAllDepartmentNotCreateTimeSheet(
            @Param("timeSheetStatus") String timeSheetStatus,
            @Param("date") Date date
    );


    @Query(
            value = "select *\n" +
                    "from department d\n" +
                    "where d.id in (select ei.department_id\n" +
                    "               from employment_info ei\n" +
                    "               where ei.employee_id = (select e.id from employee e where e.user_id = (:userId) and e.deleted = false)\n" +
                    "                 and ei.deleted = false)\n" +
                    "  and d.deleted = false\n" +
                    "order by d.created_at desc;",
            nativeQuery = true
    )
    List<Department> findAllDepartmentByUserId(
            @Param("userId") UUID userId
    );
}
