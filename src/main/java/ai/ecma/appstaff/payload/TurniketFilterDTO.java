package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurniketFilterDTO {

    @NotBlank
    private String name;

    private CustomFieldTypeEnum type;

    private CustomFiledTypeConfigDTO typeConfig;

    @NotEmpty
    private List<@NotBlank String> values;
}
