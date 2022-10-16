package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.FilterOperatorEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewFilterDTO {

    private UUID id;
    private FilterOperatorEnum filterOperator = FilterOperatorEnum.AND;

    private String search = "";

    @Valid
    private Set<ViewFilterSearchingColumnDTO> searchingColumns = new HashSet<>();

    @Valid
    private List<FilterFieldDTO> filterFields = new ArrayList<>();


    public ViewFilterDTO(UUID id, FilterOperatorEnum filterOperator, String search, @Valid List<FilterFieldDTO> filterFields) {
        this.id = id;
        this.filterOperator = filterOperator;
        this.search = search;
        this.filterFields = filterFields;
    }
}