package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.FilterOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFilterOtherServiceDTO {

    private FilterOperatorEnum filterOperator=FilterOperatorEnum.AND;

    private List<FilterOtherServiceDTO> filters;


}
