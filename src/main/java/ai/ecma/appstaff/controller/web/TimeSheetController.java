package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.projection.ITimeSheet;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping(path = TimeSheetController.TIME_SHEET_CONTROLLER_PATH)
public interface TimeSheetController {
    String TIME_SHEET_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/time-sheet";

    String CONFIRM_PATH = "/confirm/{id}";
    String CONFIRM_WITH_DEPARTMENT_PATH = "/confirm/{id}/{departmentId}";
    String GET_ALL_PATH = "/all";


    @GetMapping
    ApiResult<?> createTimeSheet();


    @GetMapping(path = {CONFIRM_PATH, CONFIRM_WITH_DEPARTMENT_PATH})
    ApiResult<?> timeSheetConfirm(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "departmentId", required = false) UUID departmentId
    );

    @GetMapping(path = GET_ALL_PATH)
    ApiResult<List<ITimeSheet>> getAllTimeSheet();


}
