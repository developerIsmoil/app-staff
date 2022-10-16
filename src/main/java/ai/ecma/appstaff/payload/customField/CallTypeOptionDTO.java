package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CallTypeOptionDTO {

    //ID BU INCOMING YOKI OUTGOING
    private String id;

    //NAME BU TILGA BO'GLIQ NOM MASALAN, INCOMING NI O'ZBEKCHASI kiruvchi qo'ng'iroq
    private String name;
}
