package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.FilterAggregateType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SortingOtherServiceDTO {

    private String tableName;

    private String columnName;

    private String returnColumn;

    private FilterAggregateType aggregateType;

    private CustomFieldTypeEnum type;

    @Max(1)
    @Min(-1)
    private int direction;
}
