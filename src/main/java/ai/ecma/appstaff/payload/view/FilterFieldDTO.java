package ai.ecma.appstaff.payload.view;


import ai.ecma.appstaff.enums.CompareOperatorTypeEnum;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
//@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterFieldDTO {

    @NotNull(message = "COMPARE_OPERATOR_TYPE_REQUIRED")
    private CompareOperatorTypeEnum compareOperatorType;

    private CompareOperatorTypeEnum additionCompareTypeForLabels;

    private Double orderIndex;

    @NotBlank(message = "FIELD_REQUIRED")
    private String field;

    private boolean customField;

    @NotNull(message = "FIELD_TYPE_REQUIRED")
    private CustomFieldTypeEnum fieldType;

    private FilterFieldValueDTO value;

    private List<String> idListForFilter = new ArrayList<>();

    public FilterFieldDTO(CompareOperatorTypeEnum compareOperatorType, Double orderIndex, String field, boolean customField, CustomFieldTypeEnum fieldType, FilterFieldValueDTO value) {
        setCompareOperatorType(compareOperatorType);
//        this.compareOperatorType = compareOperatorType;
        this.orderIndex = orderIndex;
        this.field = field;
        this.customField = customField;
        this.fieldType = fieldType;
        this.value = value;
    }

    public CompareOperatorTypeEnum getCompareOperatorType() {
        if (additionCompareTypeForLabels != null && Objects.equals(CustomFieldTypeEnum.LABELS, getFieldType())) {
            if (Objects.equals(additionCompareTypeForLabels, CompareOperatorTypeEnum.ANY)) {
                if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.EQ)) {
                    return CompareOperatorTypeEnum.ANY;
                } else if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.NOT)) {
                    return CompareOperatorTypeEnum.NOT_ANY;
                }
            } else if (Objects.equals(additionCompareTypeForLabels, CompareOperatorTypeEnum.ALL)) {
                if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.EQ)) {
                    return CompareOperatorTypeEnum.ALL;
                } else if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.NOT)) {
                    return CompareOperatorTypeEnum.NOT_ALL;
                }
            }
        }
        return compareOperatorType;
    }

    public void setCompareOperatorType(CompareOperatorTypeEnum compareOperatorType) {
        if (Objects.equals(fieldType, CustomFieldTypeEnum.LABELS)) {
            if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.ANY)) {
                this.compareOperatorType = CompareOperatorTypeEnum.EQ;
                this.additionCompareTypeForLabels = CompareOperatorTypeEnum.ANY;
            } else if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.NOT_ANY)) {
                this.compareOperatorType = CompareOperatorTypeEnum.NOT;
                this.additionCompareTypeForLabels = CompareOperatorTypeEnum.ANY;
            } else if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.ALL)) {
                this.compareOperatorType = CompareOperatorTypeEnum.EQ;
                this.additionCompareTypeForLabels = CompareOperatorTypeEnum.ALL;
            } else if (Objects.equals(compareOperatorType, CompareOperatorTypeEnum.NOT_ALL)) {
                this.compareOperatorType = CompareOperatorTypeEnum.NOT;
                this.additionCompareTypeForLabels = CompareOperatorTypeEnum.ALL;
            }else {
                this.compareOperatorType = compareOperatorType;
            }
        } else {
            this.compareOperatorType = compareOperatorType;
        }
    }
}