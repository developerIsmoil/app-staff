package ai.ecma.appstaff.controller.view;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping(path = TimeSheetViewFinanceController.TIME_SHEET_CONTROLLER_PATH)
public interface TimeSheetViewFinanceController {
    String TIME_SHEET_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/time-sheet-finance";

    String GET_VIEW_TYPES_PATH = "/get-view-types";
    String GET_VIEW_BY_ID_PATH = "/get-view-by-id/{viewId}";
    String GENERIC_VIEW_PATH = "/generic-view";
    String GET_VIEW_DATA_BY_ID_LIST_PATH = "get-data/{viewId}";
    String EDIT_ROW_DATA_PATH = "/edit-row-data/{viewId}/{rowId}";


    @GetMapping(path = GET_VIEW_TYPES_PATH)
    ApiResult<InitialViewTypesDTO> getViewTypes();


    @GetMapping(path = GET_VIEW_BY_ID_PATH)
    ApiResult<ViewDTO> getViewById(
            @PathVariable UUID viewId
    );

    /**
     * VIEW_DTO QABUL QILIB VIEW DTO DAGI SORT, FILTER, SEARCH LAR ORQALI
     * QUERY YARATADI VA EXECUTE QILADI VA ID LARNI QAYTARADI
     */
    @PostMapping(path = GENERIC_VIEW_PATH)
    ApiResult<?> genericView(@RequestParam(defaultValue = RestConstants.DEFAULT_PAGE) int page,
                             @RequestBody @Valid ViewDTO viewDTO,
                             @RequestParam(required = false) String statusId
    );


    @PostMapping(path = GET_VIEW_DATA_BY_ID_LIST_PATH)
    ApiResult<?> getViewDataByIdList(
            @PathVariable UUID viewId,
            @RequestBody List<String> idList
    );


    @PatchMapping(path = EDIT_ROW_DATA_PATH)
    ApiResult<?> editViewRowData(
            @PathVariable UUID viewId,
            @PathVariable UUID rowId,
            @RequestBody Map<String, Object> map);



}
