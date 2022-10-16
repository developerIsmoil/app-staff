package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtherServiceForSearchAndSortDTO {

    private List<SortingOtherServiceDTO> sortingOtherServiceDTOList;

    private SearchingOtherServiceDTO searchingOtherServiceDTO;

    private ViewFilterOtherServiceDTO filter;


}
