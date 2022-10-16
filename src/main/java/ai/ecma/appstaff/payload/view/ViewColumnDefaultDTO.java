package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewColumnDefaultDTO {

    private String id;     // phoneNumber, firstName,  cf=>  patronName;

    private String name;   //telefon raqami

    private CustomFieldTypeEnum type;

    private boolean customField;

    private List<ColumnOptionDTO> options;
}
