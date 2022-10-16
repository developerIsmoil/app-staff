package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.EmployeeAttachment;
import ai.ecma.appstaff.entity.EmployeePhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeePhoneNumberRepository extends JpaRepository<EmployeePhoneNumber, UUID> {

    List<EmployeePhoneNumber> findAllByEmployee(Employee employee);

    List<EmployeePhoneNumber> findAllByEmployeeInAndMainIsTrue(Collection<Employee> employee);

    @Query(
            nativeQuery = true,
            value = "SELECT CAST(id AS VARCHAR)\n" +
                    "FROM employee_phone_number\n" +
                    "WHERE (employee_id = :employeeId\n" +
                    "    AND deleted = false);"
    )
    List<String> findAllIdByEmployeeId(UUID employeeId);

    boolean existsByTypeId(UUID type_id);

    boolean existsByPhoneNumberAndMainIsTrueAndEmployee_IdNot(String phoneNumber, UUID employee_id);
}
