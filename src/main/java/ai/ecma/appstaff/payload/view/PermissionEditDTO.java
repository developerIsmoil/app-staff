package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * MEMBERNI VIEW DAGI PERMISSION NINI O'ZGARTIRISH UCHUN DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionEditDTO {

    //VIEW ID
    @NotNull(message = "VIEW_ID_REQUIRED")
    private UUID viewId;

    //QAYSI USER GA TEGISHLI
    @NotNull(message = "USER_ID_REQUIRED")
    private UUID userId;

    //QAYSI PERMISSION GA O'TKAZGANLIGI
    private PermissionUserThisViewEnum permission;

    //QO'SHMOQCHI BO'LSA -> TRUE, O'CHIRMOQCHI BO'LSA -> FALSE SHUNCHAKI O'ZGARTIRMOQCHI BO'LSA NULL KELADI
    private Boolean addMember;
}
