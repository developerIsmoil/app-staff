package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewUserAddDTO {

    @NotNull(message = "VIEW_ID_REQUIRED")
    private UUID viewId;

    @NotNull(message = "USER_ID_REQUIRED")
    private UUID userId;

    @NotNull(message = "PERMISSION_REQUIRED")
    private PermissionUserThisViewEnum permission;
}
