package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.EmploymentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHrmDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String photoId;

    public static EmployeeHrmDTO mapDTO(EmploymentInfo employmentInfo) {
        return new EmployeeHrmDTO(
                employmentInfo.getId(),
                employmentInfo.getEmployee() != null ? employmentInfo.getEmployee().getFirstName() : "",
                employmentInfo.getEmployee() != null ? employmentInfo.getEmployee().getLastName() : "",
                employmentInfo.getEmployee() != null ? employmentInfo.getEmployee().getPhotoId() : null
        );
    }
}
