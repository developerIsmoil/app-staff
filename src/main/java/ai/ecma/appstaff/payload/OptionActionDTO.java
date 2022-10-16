package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author IkhtiyorDev
 * Date 15/02/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionActionDTO {
    private String url = null;
    private Boolean create = false;
    private Boolean edit = false;
    private Boolean delete = false;

    public OptionActionDTO(String url) {
        this.url = url;
        create = true;
        edit = true;
        delete = true;
    }
}
