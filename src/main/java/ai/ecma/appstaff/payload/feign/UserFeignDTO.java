package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.payload.EmployeeDTO;
import ai.ecma.appstaff.payload.EmployeeInfoDTO;
import ai.ecma.appstaff.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFeignDTO implements Serializable {
    private UUID id;

    private String firstName;
    private String lastName;
    private String patron;

    private String phoneNumber;
    private String email;

    private Long birthDate;
    private String photoId;

    private List<Long> roles;
    private boolean access;

    public UserFeignDTO(String firstName, String lastName, String patron, String phoneNumber, String email, Long birthDate, String photoId, List<Long> roles, boolean access) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patron = patron;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.photoId = photoId;
        this.roles = roles;
        this.access = access;
    }


    public static UserFeignDTO makeUserFeignDTO(EmployeeDTO employeeDTO) {
        EmployeeInfoDTO employerInfo = employeeDTO.getEmployerInfo();

        return new UserFeignDTO(
                employeeDTO.getId(),
                CommonUtils.makePascalCase(employerInfo.getFirstName()),
                CommonUtils.makePascalCase(employerInfo.getLastName()),
                CommonUtils.makePascalCase(employerInfo.getMiddleName()),
                CommonUtils.makePhoneNumber(employeeDTO.getPhoneNumber()),
                employerInfo.getEmail(),
                employerInfo.getBirthDate(),
                employerInfo.getPhotoId(),
                employeeDTO.getAccountInfo().getRoles(),
                employeeDTO.getAccountInfo().isAccess()
        );
    }
}
