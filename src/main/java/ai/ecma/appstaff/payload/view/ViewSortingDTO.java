package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewSortingDTO {

    @NotBlank(message = "FIELD_REQUIRED")
    private String field;

    private Double orderIndex;

    @NotNull(message = "DIRECTION_REQUIRED")
    private Integer direction;

    @NotNull(message = "FIELD_TYPE_REQUIRED")
    private CustomFieldTypeEnum fieldType;

    private List<String> sortingIdList;

    private boolean customField;

    public ViewSortingDTO(String field, Double orderIndex, Integer direction) {
        this.field = field;
        this.orderIndex = orderIndex;
        this.direction = direction;
    }
}
