package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.ViewTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewTypesDTO {

    //VIEW TURI TABLE, LIST, BOARD
    private ViewTypeEnum name;

    //FAQAT LIST, BOARD YOKI TABLE BARCHA VIEW LARI
    private List<ViewDTO> views;

    //1-KIRGANDA OCHILADIGAN VIEW
    private ViewDTO defaultView;
}
