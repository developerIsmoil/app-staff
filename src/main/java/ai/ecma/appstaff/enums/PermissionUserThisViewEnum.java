package ai.ecma.appstaff.enums;

import lombok.Getter;

@Getter
public enum PermissionUserThisViewEnum {

    VIEW_ONLY(1, "PERMISSION_USER_THIS_VIEW_ENUM_DESC"),
    CHANGE_DATA(2, "PERMISSION_USER_THIS_VIEW_ENUM_DESC"),
    EDIT(3, "PERMISSION_USER_THIS_VIEW_ENUM_DESC"),
    FULL(4, "PERMISSION_USER_THIS_VIEW_ENUM_DESC");

    private int permissionLevel;

    private String description;

    PermissionUserThisViewEnum(int permissionLevel, String description) {
        this.permissionLevel = permissionLevel;
        this.description = description;
    }
}
