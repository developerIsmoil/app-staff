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

@RequestMapping(MentorViewController.VIEW_CONTROLLER_PATH)
public interface MentorViewController {
    String VIEW_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/mentor-view/";

    String GET_VIEW_TYPES_PATH = "view-types";
    String GET_VIEW_BY_ID_PATH = "view-by-id";
    String GENERIC_VIEW_PATH = "generic-view";
    String GET_VIEW_DATA_BY_ID_LIST = "data";
    String EDIT_ROW_DATA_PATH = "edit-row-data";


    /**
     * VIEW LARGA KIRGAN PAYTIDA BARCHA VIEW LARNI VA SHU USERNI PERMISSIONLARINI BERIB YUBORADIGAN YO'L
     *
     */
    @GetMapping(GET_VIEW_TYPES_PATH)
    ApiResult<InitialViewTypesDTO> getViewTypes();


    /**
     * VIEW DAN TURIB DATA LARNI OLISH
     *
     */
    @PostMapping(GET_VIEW_DATA_BY_ID_LIST + "/{viewId}")
    ApiResult<?> getViewDataByIdList(@PathVariable UUID viewId, @RequestBody List<String> idList);


    /**
     * VIEW_DTO QABUL QILIB VIEW DTO DAGI SORT, FILTER, SEARCH LAR ORQALI
     * QUERY YARATADI VA EXECUTE QILADI VA ID LARNI QAYTARADI
     */
    @PostMapping(GENERIC_VIEW_PATH)
    ApiResult<?> genericView(
            @RequestParam(defaultValue = RestConstants.DEFAULT_PAGE) int page,
            @RequestBody @Valid ViewDTO viewDTO,
            @RequestParam(required = false) String statusId
    );


    /**
     * KELGAN ID LI VIEW NI DB DAN OLIB QAYTARADI
     *
     */
    @GetMapping(GET_VIEW_BY_ID_PATH + "/{viewId}")
    ApiResult<ViewDTO> getViewById(@PathVariable UUID viewId);


    /**
     * VIEW DA BITTA LEAD NI FIELD LARINI O'ZGARTIRISH
     *
     */
    @PatchMapping(EDIT_ROW_DATA_PATH + "/{viewId}/{rowId}")
    ApiResult<?> editViewRowData(@PathVariable UUID viewId, @PathVariable UUID rowId, @RequestBody Map<String, Object> map);


}
