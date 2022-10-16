package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.entity.TimeSheet;
import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import ai.ecma.appstaff.enums.RowSizeEnum;
import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.payload.TimeSheetStatusDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewDTO {

    @NotNull(message = "ID_REQUIRED")
    private UUID id;

    private String name = "Default";

    private Boolean autoSave;

    private Boolean defaultView;

    private ViewTypeEnum type;

    //SEVIMLI VIEW MI
    private boolean favourite;

    private List<ViewColumnDTO> columns;

    @Valid
    private List<ViewSortingDTO> sorting;

    private Boolean publicly;

    private RowSizeEnum rowSize;

    private ViewPermissionDTO permissionsUser;

    //userning shu viewdagi huquqi
    private PermissionUserThisViewEnum permissionUserThisViewEnum;

    @Valid
    private ViewFilterDTO viewFilter;

    private List<TimeSheetStatusDTO> timesheet;

    public ViewDTO(UUID id, String name, Boolean defaultView, boolean favourite, Boolean publicly, ViewPermissionDTO permissionsUser) {
        this.id = id;
        this.name = name;
        this.defaultView = defaultView;
        this.favourite = favourite;
        this.publicly = publicly;
        this.permissionsUser = permissionsUser;
    }

}
