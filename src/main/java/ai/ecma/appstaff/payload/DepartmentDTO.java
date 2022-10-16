package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.Department;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {

    private UUID id;

    @NotBlank(message = "{ERROR.DEPARTMENT.NAME.REQUIRED}")
    private String name;

    private Long companyId;
    private String companyName;

    private boolean active;

    public DepartmentDTO(UUID id, String name, Long companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public DepartmentDTO(String name, Boolean active, Long companyId) {
        this.name = name;
        this.active = active;
        this.companyId = companyId;
    }

    public static DepartmentDTO makeDepartmentDTOFromDepartment(Department department) {
        return new DepartmentDTO(
                department.getId(),
                department.getName(),
                department.getCompanyId(),
                department.isActive()
        );
    }

    public DepartmentDTO(UUID id, String name, Long companyId, boolean active) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
        this.active = active;
    }
}
