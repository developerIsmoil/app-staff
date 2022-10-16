package ai.ecma.appstaff.payload.customField;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFieldDTO {

    private UUID id;

    private String name;

    private CustomFieldTypeEnum type;

    private boolean required;

    private CustomFiledTypeConfigDTO typeConfig=new CustomFiledTypeConfigDTO();

    private Object value;


}
