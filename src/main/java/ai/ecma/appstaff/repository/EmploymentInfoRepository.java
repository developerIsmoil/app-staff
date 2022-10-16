package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Department;
import ai.ecma.appstaff.entity.EmploymentInfo;
import ai.ecma.appstaff.enums.EmployerStatusEnum;
import ai.ecma.appstaff.projection.EmploymentInfoProjection;
import ai.ecma.appstaff.projection.EmploymentInfoTurniketProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmploymentInfoRepository extends JpaRepository<EmploymentInfo, UUID> {

    /**
     * Bunda biron bir hodim bo'yicha EmploymentInfo larni olingan
     *
     * @param employee_id employee_id
     * @return List<EmploymentInfo>
     */
    List<EmploymentInfo> findAllByEmployee_Id(UUID employee_id);

    /**
     * Bunda biron bir hodimning ID si bo'yicha EmploymentInfo ni o'chiramiz
     *
     * @param employee_id Hodimning ID si
     */
    void deleteByEmployee_Id(UUID employee_id);

    EmploymentInfo findByEmployee_IdAndDepartment_Id(UUID employee_id, UUID department_id);

    List<EmploymentInfo> findAllByDepartmentInAndEmployerStatus(Collection<Department> department, EmployerStatusEnum employerStatus);

    List<EmploymentInfo> findAllByManageTableIsTrueAndEmployee_UserId(UUID employee_userId);

    List<EmploymentInfo> findAllByEmployee_UserId(UUID employee_userId);

    @Query(
            nativeQuery = true,
            value = "SELECT CAST(id AS VARCHAR)\n" +
                    "FROM employment_info\n" +
                    "WHERE (employee_id = :employeeId\n" +
                    "    AND deleted = false);"
    )
    List<String> findAllIdByEmployeeId(
            @Param("employeeId") UUID employeeId
    );

    @Query(
            value = "SELECT *\n" +
                    "FROM employment_info\n" +
                    "WHERE employee_id = (SELECT id FROM employee WHERE user_id = :userId)\n" +
                    "  AND CAST(employer_status AS VARCHAR) = CAST((:employerStatus) AS VARCHAR)",
            nativeQuery = true
    )
    List<EmploymentInfo> getAllEmploymentInfoByUserId(
            @Param("userId") UUID userId,
            @Param("employerStatus") EmployerStatusEnum employerStatus
    );

    boolean existsByDepartmentId(UUID department_id);

    boolean existsByPositionId(UUID position_id);

    boolean existsByEmployeeCategoryId(UUID employeeCategory_id);

    boolean existsByEmployeeCategoryTypeId(UUID employeeCategoryType_id);


    @Query(value = "select cast(employee_id as varchar) as employeeIdStr, cast(department_id as varchar) as departmentIdStr\n" +
            "from employment_info\n" +
            "where employee_id in :employeeIds\n" +
            "  and deleted = false", nativeQuery = true)
    List<EmploymentInfoProjection> findAllEmploymentInfoByEmpIds(@Param("employeeIds") Collection<UUID> employeeIds);

    @Query(value = "select cast(d.name as varchar) as departmentName,\n" +
            "       cast(p.name as varchar) as positionName,\n" +
            "       ei.company_id           as companyId\n" +
            "from employment_info ei\n" +
            "         join department d on ei.department_id = d.id\n" +
            "         join position p on d.id = p.department_id\n" +
            "where employee_id = :employeeId\n" +
            "  and ei.deleted = false\n" +
            "  and d.deleted = false\n" +
            "  and p.deleted = false", nativeQuery = true)
    List<EmploymentInfoTurniketProjection> getTurniketInfoFromEmployee(@Param("employeeId") UUID employeeId);

    List<EmploymentInfo> findAllByDepartment(Department department);


}
