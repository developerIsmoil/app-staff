package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PHONE NUMBER TURIDAGI COLUMN NI BERISH UCHUN
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CallConfigDTO {

    //QAYSI YO'LGA BORADI
    private String url;
}
