package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TIZIMDAGI USER NI VIEW NI USERIGA NISBATAN HUQUQLARI
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CanReceivePermissionDTO {

    //FULL, EDIT, VIEW_ONLY
    private PermissionUserThisViewEnum permission;

    //DESCRIPTION
    private String description;

    //TEGINOLADIMI
    private boolean disable;
}
