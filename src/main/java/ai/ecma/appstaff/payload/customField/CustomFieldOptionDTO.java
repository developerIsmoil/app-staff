package ai.ecma.appstaff.payload.customField;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CustomFieldOptionDTO {

    private String id;

    private Double orderIndex;

    private String name;

//    private String label;

    private String colorCode;

    public CustomFieldOptionDTO(String id, String name, String colorCode) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
    }

    public CustomFieldOptionDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
