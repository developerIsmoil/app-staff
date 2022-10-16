package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * MEMBER HAQIDA MA'LUMOT
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ViewMemberDTO {
//=====USER DATA====================

    //ISMI
    private String firstName;

    //FAMILIYASI
    private String lastName;

    //RASMINI ID SI
    private String photoId;

    //USER ID
    private UUID userId;

//=====USER DATA====================

    //MEMBER NI SHU VIEW DAGI HUQUQI
    private PermissionUserThisViewEnum permission;

    //USER SHU MEMBERGA TEGINA OLADIMI. AGAR MEMBER O'ZI BO'LSA YOKI ..
    private Boolean disable;

    //BOSHQA HUQUQLAR
    private List<CanReceivePermissionDTO> canOtherPermissions;

    //MEMBER NI QO'SHISH YOKI O'CHIRISH FUNKSIYASI BORMI, YA'NI SWITCH CASE BORMI
    private boolean haveSwitchAddOrRemoveMember;
}
