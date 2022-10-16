package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.enums.GenderEnum;
import ai.ecma.appstaff.enums.MaritalStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeViewDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Long birthDate;
    private MaritalStatusEnum maritalStatus;
    private GenderEnum gender;
    private String email;
    private String passportSerial;
    private String passportNumber;
    private String passportGivenOrganisation;
    private Long passportGivenDate;
    private Long passportExpireDate;
    private String permanentAddress;
    private String currentAddress;
    private String jshshr;
    private boolean access;
    private Map<UUID, Object> customFields;


    public static EmployeeViewDTO fromEmployee(Employee employee) {
        return new EmployeeViewDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName(),
                employee.getBirthDate().getTime(),
                employee.getMaritalStatus(),
                employee.getGender(),
                employee.getEmail(),
                employee.getPassportSerial(),
                employee.getPassportNumber(),
                employee.getPassportGivenOrganisation(),
                employee.getPassportGivenDate().getTime(),
                employee.getPassportExpireDate().getTime(),
                employee.getPermanentAddress(),
                employee.getCurrentAddress(),
                employee.getPersonalNumber(),
                employee.isAccess(),
                null
        );
    }

}
