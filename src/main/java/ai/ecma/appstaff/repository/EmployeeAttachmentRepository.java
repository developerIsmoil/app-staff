package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.EmployeeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeAttachmentRepository extends JpaRepository<EmployeeAttachment, UUID> {

    List<EmployeeAttachment> findAllByEmployee(Employee employee);

    @Query(
            nativeQuery = true,
            value = "SELECT cast(id as varchar)\n" +
                    "FROM employee_attachment\n" +
                    "WHERE (employee_id = :employeeId\n" +
                    "    AND deleted = false);"
    )
    List<String> findAllIdByEmployeeId(
            @Param("employeeId") UUID employeeId
    );
}
