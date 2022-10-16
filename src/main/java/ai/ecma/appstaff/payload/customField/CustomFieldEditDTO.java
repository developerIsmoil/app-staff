package ai.ecma.appstaff.payload.customField;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CustomFieldEditDTO {

    @NotNull
    private UUID viewId;

    @NotNull
    private UUID id;

    private String name;

    @NotNull
    private CustomFieldTypeEnum type;

    private boolean required;

    private CustomFiledTypeConfigDTO typeConfig;
}
