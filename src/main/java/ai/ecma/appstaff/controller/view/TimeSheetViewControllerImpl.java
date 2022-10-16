package ai.ecma.appstaff.controller.view;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.service.TimesheetService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TimeSheetViewControllerImpl implements TimeSheetViewController {

    private final TimesheetService timesheetService;


    @CheckAuth(permission = PermissionEnum.HRM_GET_TIMESHEET_VIEW_TYPES)
    @Override
    public ApiResult<InitialViewTypesDTO> getViewTypes() {
        return timesheetService.getViewTypes(TableNameConstant.TIMESHEET_EMPLOYEE);
    }

    @CheckAuth(permission = PermissionEnum.HRM_GET_TIMESHEET_VIEW)
    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {
        return timesheetService.getViewById(viewId);
    }

    @CheckAuth(permission = PermissionEnum.HRM_GENERIC_TIMESHEET_VIEW)
    @Override
    public ApiResult<?> genericView(int page, ViewDTO viewDTO, String statusId) {
        return timesheetService.genericView(page, viewDTO, statusId);
    }

    @CheckAuth(permission = PermissionEnum.HRM_GET_TIMESHEET_VIEW_DATA)
    @Override
    public ApiResult<?> getViewDataByIdList(UUID viewId, List<String> idList) {
        return timesheetService.getViewDataByIdList(viewId, idList);
    }

    @CheckAuth(permission = PermissionEnum.HRM_EDIT_TIMESHEET_VIEW_ROW_DATA)
    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {
        return timesheetService.editViewRowData(viewId, rowId, map);
    }
}
