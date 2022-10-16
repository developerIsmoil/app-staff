package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CompareOperatorTypeEnum;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.FilterAggregateType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterOtherServiceDTO {

    private String tableName;

    private String columnName;

    private FilterAggregateType aggregateType;

    private CustomFieldTypeEnum type;

    private CompareOperatorTypeEnum compareOperatorType;

    private FilterFieldValueDTO filterFieldValue;

    private String returnColumn;
}
