package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CallStatusOptionDTO {

    //ID BU ANSWERED, BUSY,...
    private String id;

    //TILGA BO'GLIQ NOM MASALAN BAND, JAVOB BERILMAGAN,...
    private String name;

    //RANGI MASALAN BUSY BO'LSA RANGI QIZIL
    private String colorCode;
}
