package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MESSAGE YUBORISHI UCHUN TYPE CONFIG
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageConfigDTO {

    private String url;

//    private MessageTemplateId ;

}
