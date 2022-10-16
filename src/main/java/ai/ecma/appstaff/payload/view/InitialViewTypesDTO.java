package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.entity.view.ViewObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitialViewTypesDTO {

    //BARCHA VIEW TYPE LAR VA VIEW LAR LISTI HAMDA HAR BIR TUR UCHUN DEFAULT VIEW
    private List<ViewTypesDTO> viewTypes;

    //USER NING VIEW LAR DAGI HUQUQLARI
    private ViewPermissionDTO permissions;

    public static InitialViewTypesDTO makeDefaultForTimesheet(ViewObject viewObject) {
        return new InitialViewTypesDTO(
                Collections.singletonList(
                        new ViewTypesDTO(
                                ViewTypeEnum.TABLE,
                                new ArrayList<>(),
                                new ViewDTO(
                                        viewObject.getId(),
                                        viewObject.getName(),
                                        viewObject.isDefaultView(),
                                        false,
                                        viewObject.isPublicly(),
                                        new ViewPermissionDTO(
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                true,
                                                true,
                                                true
                                        )
                                )
                        )
                ),
                new ViewPermissionDTO()
        );
    }
}
