package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.EmployeeExperienceHistory;
import ai.ecma.appstaff.payload.EmployeeExperienceInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeExperienceHistoryRepository extends JpaRepository<EmployeeExperienceHistory, UUID> {

    /**
     * EmployeeExperienceHistory larni employee bo'yicha olish uchun
     *
     * @param employee
     * @return
     */
    List<EmployeeExperienceHistory> findAllByEmployee(Employee employee);

    /**
     * employee_id bo'yicha EmployeeExperienceHistoryni o'chirish uchun
     *
     * @param employee_id
     */
    void deleteByEmployee_Id(UUID employee_id);

    @Query(
            nativeQuery = true,
            value = "SELECT CAST(id AS VARCHAR)\n" +
                    "FROM employee_experience_history\n" +
                    "WHERE (employee_id = :employeeId\n" +
                    "    AND deleted = false);"
    )
    List<String> findAllIdByEmployeeId(UUID employeeId);
}
