package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditRowDataDTO {

    private String columnName;



    private String value;

}
