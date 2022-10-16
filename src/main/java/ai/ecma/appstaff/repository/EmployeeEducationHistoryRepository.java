package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.EmployeeEducationHistory;
import ai.ecma.appstaff.entity.EmployeeExperienceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeEducationHistoryRepository extends JpaRepository<EmployeeEducationHistory, UUID> {

    /**
     * Bunda aynan biron bir hodimga tegishli bo'lgan EmployeeEducationHistory lar olingan
     *
     * @param employee Hodim
     * @return List<EmployeeEducationHistory>
     */
    List<EmployeeEducationHistory> findAllByEmployee(Employee employee);

    /**
     * Bunda biron bir hodimning ID si bo'yicha EmployeeEducationHistory ni o'chiramiz
     *
     * @param employee_id Hodimning ID si
     */
    void deleteByEmployee_Id(UUID employee_id);


    @Query(
            nativeQuery = true,
            value = "SELECT CAST(id AS VARCHAR)\n" +
                    "FROM employee_education_history\n" +
                    "WHERE (employee_id = :employeeId\n" +
                    "    AND deleted = false);"
    )
    List<String> findAllIdByEmployeeId(
            @Param("employeeId") UUID employeeId
    );
}
