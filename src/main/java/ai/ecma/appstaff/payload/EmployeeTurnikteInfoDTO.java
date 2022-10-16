package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeTurnikteInfoDTO {

    private UUID id;

    private String avatar;

    private String fullName;

    private List<String> companies;

    private List<String> departments;

    private List<String> positions;

    private String phoneNumber;

    public EmployeeTurnikteInfoDTO(UUID id, String fullName, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
}
