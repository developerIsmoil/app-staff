package ai.ecma.appstaff.payload.customField;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CustomFieldAddDTO {

    @NotNull
    private UUID viewId;

    @NotBlank
    private String name;

    @NotNull
    private CustomFieldTypeEnum type;

    private boolean required;

    private CustomFiledTypeConfigDTO typeConfig;
}
