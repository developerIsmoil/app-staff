package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttachmentConfigDTO {

    //FILE NI OLIB KELAIDGAN URL
    private String url;
}
