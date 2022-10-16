package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeCategoryTypeDTO {

    private UUID id;
    private String name;
    private Boolean active;
    private Long companyId;

    public EmployeeCategoryTypeDTO(UUID id, String name, Long companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public EmployeeCategoryTypeDTO(String name, Boolean active, Long companyId) {
        this.name = name;
        this.active = active;
        this.companyId = companyId;
    }
}
