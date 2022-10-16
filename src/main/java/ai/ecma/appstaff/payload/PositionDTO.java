package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.Position;
import ai.ecma.appstaff.utils.ResponseMessage;
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
public class PositionDTO {

    private UUID id;

    @NotBlank(message = ResponseMessage.ERROR_POSITION_NAME_REQUIRED)
    private String name;

    private Boolean active;

    // AGAR DTODAN KELGAN DEPARTMENT ID BO'SH BO'LSA XATOLIKKA TUSHADI
    // CHUNKI DEPARTMENT BO'LMASA LAVOZIM HAM YO'Q :)
    @NotBlank(message = ResponseMessage.ERROR_DEPARTMENT_REQUIRED)
    private UUID departmentId;

    private String departmentName;

    private Long companyId;
    private String companyName;

    private OptionDTO<DepartmentDTO> department;

    private Boolean manageTimesheet;

    public static PositionDTO fromPositionForSelect(Position position) {
        return new PositionDTO(
                position.getId(),
                position.getName(),
                position.getDepartment().getId(),
                position.getManageTimesheet(),
                position.getCompanyId()
        );
    }


    public static PositionDTO fromPosition(Position position) {
        return new PositionDTO(
                position.getId(),
                position.getName(),
                position.isActive(),
                position.getDepartment() != null ? position.getDepartment().getId() : null,
                position.getDepartment() != null ? position.getDepartment().getName() : null,
                position.getDepartment() != null ? position.getDepartment().getCompanyId() : null,
                position.getManageTimesheet()

        );
    }

    public PositionDTO(OptionDTO<DepartmentDTO> department) {
        this.department = department;
    }

    public PositionDTO(UUID id, String name, Boolean active, OptionDTO<DepartmentDTO> department, Boolean manageTimesheet, Long companyId) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.department = department;
        this.manageTimesheet = manageTimesheet;
        this.companyId = companyId;
    }

    public PositionDTO(UUID id, String name, Boolean active, UUID departmentId, String departmentName, Long companyId, Boolean manageTimesheet) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.companyId = companyId;
        this.manageTimesheet = manageTimesheet;
    }

    public PositionDTO(UUID id, String name, Long companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public PositionDTO(String name, Boolean active, UUID departmentId, Long companyId) {
        this.name = name;
        this.active = active;
        this.departmentId = departmentId;
        this.companyId = companyId;
    }

    public PositionDTO(UUID id, String name, UUID departmentId, Boolean manageTimesheet, Long companyId) {
        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
        this.manageTimesheet = manageTimesheet;
        this.companyId = companyId;
    }
}
