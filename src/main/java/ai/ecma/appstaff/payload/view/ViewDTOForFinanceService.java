package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewDTOForFinanceService implements Serializable {

    @Valid
    private List<ViewSortingDTO> sorting;

    @Valid
    private ViewFilterDTO viewFilter;

}
