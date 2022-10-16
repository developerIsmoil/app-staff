package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPermissionDTO {

    private UUID userId;

    private PermissionUserThisViewEnum permissions;

    private boolean remove = false;
}
