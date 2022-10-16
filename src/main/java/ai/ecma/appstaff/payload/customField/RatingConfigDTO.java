package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RATING UCHUN CONFIG CLASS
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RatingConfigDTO {
    //RATINGDA EMOJI CODI UCHUN
    private String codePoint;

    //RATINGDA EMOJI SONI UCHUN
    private Integer count;
}
