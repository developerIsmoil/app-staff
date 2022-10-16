package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.Skill;
import ai.ecma.appstaff.enums.ActionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillDTO {

    private UUID id;
    // NOMI
    @NotBlank(message = "{REQUIRED.NAME}")
    private String name;
    // RANGI
    @NotBlank(message = "{REQUIRED.COLOR}")
    private String colorCode;
    @NotNull(message = "{REQUIRED.METHOD}")
    private ActionEnum method;
    private Long companyId;

    public SkillDTO(UUID id, String name, String colorCode, Long companyId) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.companyId = companyId;
    }

    public static SkillDTO fromSkill(Skill skill) {
        return new SkillDTO(
                skill.getId(),
                skill.getName(),
                skill.getColor(),
                skill.getCompanyId()
        );
    }


}
