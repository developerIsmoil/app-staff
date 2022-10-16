package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewAddDTO {

    @NotNull(message = "DEFAULT_VIEW_ID_REQUIRED")
    private UUID defaultViewId;

    private String name;

    private Boolean personal=false;
}
