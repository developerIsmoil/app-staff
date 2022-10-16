package ai.ecma.appstaff.projection;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import ai.ecma.appstaff.enums.RowSizeEnum;
import ai.ecma.appstaff.enums.ViewTypeEnum;

import java.util.UUID;


public interface ViewObjectAndPermission {

    UUID getId();

    //USHBU VIEW QAYSI TABLE GA TEGISHLI
    String getTableName();

    String getName();

    Boolean getDefaultView();

    UUID getCreatedById();

    Boolean getAutoSave();

//    List<ViewColumn> getColumnList();

//    List<ViewSorting> getSortingList();

    Boolean getPublicly();

    Boolean getShared();

    RowSizeEnum getRowSize();

//    @Value("#{viewFilterRepository.findById(target.id)}")
//    ViewFilter getViewFilter();

    UUID getViewFilterId();

    ViewTypeEnum getType();

    PermissionUserThisViewEnum getPermission();

    UUID getUserId();

    boolean getFavourite();
}
