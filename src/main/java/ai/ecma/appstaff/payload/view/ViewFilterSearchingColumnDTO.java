package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFilterSearchingColumnDTO {


    @NotNull(message = "COLUMN_TYPE_REQUIRED")
    private CustomFieldTypeEnum columnType;

    @NotBlank
    private String columnName;

    private List<String> searchedList=new ArrayList<>();

    private boolean customField;
}
