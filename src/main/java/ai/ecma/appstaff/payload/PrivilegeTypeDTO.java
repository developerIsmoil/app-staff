package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.utils.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivilegeTypeDTO {

    private UUID id;

    @NotBlank(message = ResponseMessage.NAME_IS_REQUIRED)
    private String name;

    private boolean active;

    private Long companyId;

    public static PrivilegeTypeDTO mapPrivilegeTypeToPrivilegeTypeDTO(PrivilegeType privilegeType) {
        return new PrivilegeTypeDTO(
                privilegeType.getId(),
                privilegeType.getName(),
                privilegeType.isActive(),
                privilegeType.getCompanyId()
        );
    }


    public static PrivilegeTypeDTO fromPrivilegeTypeForSelect(PrivilegeType privilegeType) {
        return new PrivilegeTypeDTO(
                privilegeType.getId(),
                privilegeType.getName(),
                privilegeType.getCompanyId()
        );
    }


    public PrivilegeTypeDTO(UUID id, String name, Long companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public PrivilegeTypeDTO(String name, Boolean active, Long companyId) {
        this.name = name;
        this.active = active;
        this.companyId = companyId;
    }

    public static List<PrivilegeTypeDTO> mapPrivilegeTypeToPrivilegeTypeDTO(Collection<PrivilegeType> privilegeTypeList) {
        return privilegeTypeList
                .stream()
                .map(PrivilegeTypeDTO::mapPrivilegeTypeToPrivilegeTypeDTO)
                .collect(Collectors.toList());
    }
}
