package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumDTO {

    private String name;
    private String id;
    private Boolean active;
    private UUID uuid;

    public EnumDTO(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public EnumDTO(String name, UUID id, Boolean active) {
        this.name = name;
        this.uuid = id;
        this.active = active;
    }
}
