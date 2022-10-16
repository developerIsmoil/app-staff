package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeSkillInfoDTO {

    private List<UUID> skillsIdList = new ArrayList<>();
    private List<SkillDTO> skills = new ArrayList<>();

    public EmployeeSkillInfoDTO(List<SkillDTO> skills) {
        this.skills = skills;
    }
}
