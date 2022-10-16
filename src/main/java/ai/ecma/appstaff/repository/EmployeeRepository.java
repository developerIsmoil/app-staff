package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.projection.IEmployee;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.TableNameConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {


    @Query(nativeQuery = true,
            value = "SELECT *\n" +
                    "FROM employee\n" +
                    "WHERE id IN\n" +
                    "      (SELECT employee_id FROM employment_info WHERE department_id = (:departmentId));")
    List<Employee> findAllByDepartmentId(
            @Param("departmentId") UUID departmentId
    );

    Optional<Employee> findByUserId(UUID userId);

    @Query(value = "SELECT * FROM get_entity_id_list_for_generic_view(:myQuery)", nativeQuery = true)
    List<String> getEntityIdListForGenericView(
            @Param("myQuery") String myQuery
    );


    @Query(
            value = "select branch_id\n" +
                    "from employment_info ei\n" +
                    "where ei.employee_id = (select e.id from employee e where e.user_id = (:userId) and e.deleted = false)\n" +
                    "  and deleted = false\n" +
                    "order by branch_id;",
            nativeQuery = true
    )
    List<Long> findAllBranchIdByUserId(
            @Param("userId") UUID userId
    );

    boolean existsByPrivilegeTypesIn(Collection<PrivilegeType> privilegeTypes);

    @Query(
            value = "select cast(e.id as varchar) as id,\n" +
                    "       e.first_name          as firstName,\n" +
                    "       e.last_name           as lastName\n" +
                    "from employee e\n" +
                    "         join employment_info ei on e.id = ei.employee_id\n" +
                    "where e.id in (:employeesId)\n" +
                    "  and e.roles in (:rolesId)\n" +
                    "  and ei.employer_status = 'WORKING'\n" +
                    "group by e.id;",
            nativeQuery = true
    )
    List<IEmployee> findAllOperatorsByIdAndRoleId(
            @Param("employeesId") List<UUID> employeesId,
            @Param("rolesId") List<Long> rolesId
    );

    @Query(
            value = "select cast(user_id as varchar)    as id,\n" +
                    "     (first_name  ||' '|| last_name) as fullName,\n" +
                    "       first_name as firstName,\n" +
                    "       last_name  as lastName\n" +
                    "from employee\n" +
                    "where user_id in (:mentorsId)\n" +
                    "  and id in (select employee_id from employment_info where branch_id = :branchId and deleted = false)\n" +
                    "  and deleted = false\n" +
                    "order by first_name, last_name;",
            nativeQuery = true
    )
    List<IEmployee> getEmployeeByBranchId(
            @Param("mentorsId") List<UUID> mentorsId,
            @Param("branchId") Integer branchId);

    @Query(value = "select max(" + ColumnKey.TURNIKET_ID + ") from " + TableNameConstant.EMPLOYEE + " where deleted=false", nativeQuery = true)
    Integer maxTurniketId();

    Optional<Employee> findFirstByTurniketId(Integer turniketId);

    @Query(value = "select e.*\n" +
            "from " + TableNameConstant.EMPLOYEE + " e\n" +
            "         join " + TableNameConstant.EMPLOYMENT_INFO + " ei on e.id = ei.employee_id where ei.department_id =:depId and e.deleted=false and ei.deleted=false order by " + ColumnKey.LAST_NAME, nativeQuery = true)
    List<Employee> findAllByDepartmentIdForTurniket(@Param("depId") UUID departmentId);


    @Query(value = "select e.*\n" +
            "from " + TableNameConstant.EMPLOYEE + " e\n" +
            "         join " + TableNameConstant.EMPLOYMENT_INFO + " ei on e.id = ei.employee_id where e.deleted=false and ei.deleted=false and ei.company_id=:compId order by " + ColumnKey.LAST_NAME, nativeQuery = true)
    List<Employee> findAllByCompanyId(@Param("compId") Long compId);


    boolean existsByUserId(UUID userId);
}
