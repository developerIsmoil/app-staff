package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.EmployeeCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeCategoryDTO {

    private UUID id;

    private UUID departmentId;
    private String departmentName;

    private OptionDTO<DepartmentDTO> department;

    private UUID positionId;
    private String positionName;

    private OptionDTO<PositionDTO> position;

    private UUID employeeCategoryTypeId;
    private String employeeCategoryTypeName;

    private OptionDTO<EmployeeCategoryTypeDTO> employeeCategoryType;

    private String requirement;
    private String description;
    private Boolean active;

    private String name;
    private Long companyId;


    public static EmployeeCategoryDTO fromEmployeeCategory(EmployeeCategory employeeCategory) {
        return new EmployeeCategoryDTO(
                employeeCategory.getId(),
                employeeCategory.getDepartment() != null ? employeeCategory.getDepartment().getId() : null,
                employeeCategory.getDepartment() != null ? employeeCategory.getDepartment().getName() : null,
                employeeCategory.getPosition() != null ? employeeCategory.getPosition().getId() : null,
                employeeCategory.getPosition() != null ? employeeCategory.getPosition().getName() : null,
                employeeCategory.getEmployeeCategoryType() != null ? employeeCategory.getEmployeeCategoryType().getId() : null,
                employeeCategory.getEmployeeCategoryType() != null ? employeeCategory.getEmployeeCategoryType().getName() : null,
                employeeCategory.getRequirement(),
                employeeCategory.getDescription(),
                employeeCategory.isActive(),
                employeeCategory.getCompanyId()
        );
    }

    public EmployeeCategoryDTO(UUID id, OptionDTO<DepartmentDTO> department, OptionDTO<PositionDTO> position, OptionDTO<EmployeeCategoryTypeDTO> employeeCategoryType, String requirement, String description, Boolean active, Long companyId) {
        this.id = id;
        this.department = department;
        this.position = position;
        this.employeeCategoryType = employeeCategoryType;
        this.requirement = requirement;
        this.description = description;
        this.active = active;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(OptionDTO<DepartmentDTO> department, OptionDTO<PositionDTO> position, OptionDTO<EmployeeCategoryTypeDTO> employeeCategoryType, Long companyId) {
        this.department = department;
        this.position = position;
        this.employeeCategoryType = employeeCategoryType;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID id, UUID departmentId, String departmentName, UUID positionId, String positionName, UUID employeeCategoryTypeId, String employeeCategoryTypeName, String requirement, String description, Boolean active, String name, Long companyId) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.employeeCategoryTypeId = employeeCategoryTypeId;
        this.employeeCategoryTypeName = employeeCategoryTypeName;
        this.requirement = requirement;
        this.description = description;
        this.active = active;
        this.name = name;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID id, UUID positionId, String employeeCategoryTypeName, Long companyId) {
        this.id = id;
        this.positionId = positionId;
        this.employeeCategoryTypeName = employeeCategoryTypeName;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID id, String employeeCategoryTypeName, Long companyId) {
        this.id = id;
        this.employeeCategoryTypeName = employeeCategoryTypeName;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID id, UUID departmentId, String departmentName, UUID positionId, String positionName, UUID employeeCategoryTypeId, String employeeCategoryTypeName, String requirement, String description, Boolean active, Long companyId) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.employeeCategoryTypeId = employeeCategoryTypeId;
        this.employeeCategoryTypeName = employeeCategoryTypeName;
        this.requirement = requirement;
        this.description = description;
        this.active = active;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID id, Boolean active, String name, Long companyId) {
        this.id = id;
        this.active = active;
        this.name = name;
        this.companyId = companyId;
    }

    public EmployeeCategoryDTO(UUID departmentId, UUID positionId, UUID employeeCategoryTypeId, String requirement, String description, Boolean active, String name, Long companyId) {
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.employeeCategoryTypeId = employeeCategoryTypeId;
        this.requirement = requirement;
        this.description = description;
        this.active = active;
        this.name = name;
        this.companyId = companyId;
    }
}
