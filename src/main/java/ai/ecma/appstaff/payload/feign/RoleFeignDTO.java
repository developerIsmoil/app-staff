package ai.ecma.appstaff.payload.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleFeignDTO implements Serializable {

    private Long id;

    private String name;

    private List<UUID> values;

    //    ACCOUNT INFORMATION
    private List<RoleFeignDTO> options;
}
