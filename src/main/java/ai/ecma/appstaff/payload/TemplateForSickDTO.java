package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.TemplateForSick;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateForSickDTO {

    private UUID id;
    private boolean privilege;

    private UUID privilegeTypeId;
    private String privilegeTypeName;

    private OptionDTO<PrivilegeTypeDTO> privilegeType;

    private Integer fromCount;
    private Integer toCount;

    @NotNull(message = "{ERROR.TEMPLATE.FOR.SICK.PERCENT.DOES.NOT.NULL}")
    private Double percent;
    private boolean active;

    private String name;

    private Long companyId;

    public TemplateForSickDTO(UUID id, boolean privilege, UUID privilegeTypeId, String privilegeTypeName, Integer fromCount, Integer toCount, Double percent, boolean active, String name, Long companyId) {
        this.id = id;
        this.privilege = privilege;
        this.privilegeTypeId = privilegeTypeId;
        this.privilegeTypeName = privilegeTypeName;
        this.fromCount = fromCount;
        this.toCount = toCount;
        this.percent = percent;
        this.active = active;
        this.name = name;
        this.companyId = companyId;
    }

    public static TemplateForSickDTO fromTemplateForSick(TemplateForSick templateForSick) {

        String name;

        if (templateForSick.isPrivilege()) {
            name = templateForSick.getPrivilegeType().getName();
        } else {
            name = templateForSick.getFromCount() + " - " + templateForSick.getToCount();
        }

        return new TemplateForSickDTO(
                templateForSick.getId(),
                templateForSick.isPrivilege(),
                templateForSick.getPrivilegeType() != null ? templateForSick.getPrivilegeType().getId() : null,
                templateForSick.getPrivilegeType() != null ? templateForSick.getPrivilegeType().getName() : null,
                templateForSick.getFromCount(),
                templateForSick.getToCount(),
                templateForSick.getPercent(),
                templateForSick.isActive(),
                name,
                templateForSick.getCompanyId()
        );
    }

    public TemplateForSickDTO(OptionDTO<PrivilegeTypeDTO> privilegeType) {
        this.privilegeType = privilegeType;
    }

    public TemplateForSickDTO(UUID id, boolean privilege, UUID privilegeTypeId, String privilegeTypeName, Integer fromCount, Integer toCount, Double percent, boolean active, Long companyId) {
        this.id = id;
        this.privilege = privilege;
        this.privilegeTypeId = privilegeTypeId;
        this.privilegeTypeName = privilegeTypeName;
        this.fromCount = fromCount;
        this.toCount = toCount;
        this.percent = percent;
        this.active = active;
        this.companyId = companyId;
    }

    public TemplateForSickDTO(UUID id, boolean privilege, OptionDTO<PrivilegeTypeDTO> privilegeType, Integer fromCount, Integer toCount, Double percent, boolean active, Long companyId) {
        this.id = id;
        this.privilege = privilege;
        this.privilegeType = privilegeType;
        this.fromCount = fromCount;
        this.toCount = toCount;
        this.percent = percent;
        this.active = active;
        this.companyId = companyId;
    }

    public TemplateForSickDTO(boolean privilege, UUID privilegeTypeId, Double percent, boolean active, Long companyId) {
        this.privilege = privilege;
        this.privilegeTypeId = privilegeTypeId;
        this.percent = percent;
        this.active = active;
        this.companyId = companyId;
    }
}
