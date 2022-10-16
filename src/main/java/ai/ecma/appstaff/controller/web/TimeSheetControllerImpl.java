package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.projection.ITimeSheet;
import ai.ecma.appstaff.service.EmployeeAttendanceService;
import ai.ecma.appstaff.service.TimesheetService;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TimeSheetControllerImpl implements TimeSheetController {

    private final TimesheetService timeSheetService;

    @CheckAuth
    @Override
    public ApiResult<?> createTimeSheet() {
        timeSheetService.createTimeSheet();
        return new ApiResult<>();
    }


    @CheckAuth(
            permission = {
//                    PermissionEnum.MANAGE_TIME_SHEET_ONLY_OWN_DEPARTMENTS,
//                    PermissionEnum.MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS
            }
    )
    @Override
    public ApiResult<?> timeSheetConfirm(Long id, UUID departmentId) {
        return timeSheetService.timeSheetConfirm(id, departmentId);
    }

    @CheckAuth(
            permission = {
//                    PermissionEnum.MANAGE_TIME_SHEET_ONLY_OWN_DEPARTMENTS,
//                    PermissionEnum.MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS
            }
    )
    @Override
    public ApiResult<List<ITimeSheet>> getAllTimeSheet() {
        return timeSheetService.getAllTimeSheet();
    }

}
