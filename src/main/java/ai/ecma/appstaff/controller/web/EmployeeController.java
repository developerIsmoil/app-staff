package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeDTO;
import ai.ecma.appstaff.payload.EmployeeResignationDTO;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping(path = EmployeeController.EMPLOYEE_CONTROLLER_PATH)
public interface EmployeeController {
    String EMPLOYEE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/employee";

    String ADD_EMPLOYEE_PATH = "/add";
    String EDIT_EMPLOYEE_PATH = "/edit/{id}";
    String GET_ONE_EMPLOYEE_PATH = "/get/{id}";
    String GET_FORM_EMPLOYEE_PATH = "/get";
    String DELETE_EMPLOYEE_PATH = "/delete/{id}";
    //
    String GET_VIEW_TYPES_PATH = "/get-view-types";
    String GET_VIEW_BY_ID_PATH = "/get-view-by-id/{viewId}";
    String GENERIC_VIEW_PATH = "/generic-view";
    String GET_VIEW_DATA_BY_ID_LIST_PATH = "get-data";
    String EDIT_ROW_DATA_PATH = "/edit-row-data";
    String RESIGNATION_EMPLOYEE_PATH = "/resignation-employee";
    String RESIGNATION_EMPLOYMENT_PATH = "/resignation-employment";


    /**
     * Tizimga yangi hozim qo'shish uchun yo'l
     *
     * @param employeeDTO
     * @return
     */
    @PostMapping(path = ADD_EMPLOYEE_PATH)
    ApiResult<?> addEmployee(
            @RequestBody @Valid EmployeeDTO employeeDTO
    );

    /**
     * Mavjud hodimni tahrirlash uchun yo'l
     *
     * @param id
     * @param employeeDTO
     * @return
     */
    @PutMapping(path = EDIT_EMPLOYEE_PATH)
    ApiResult<?> editEmployee(
            @PathVariable(name = "id") UUID id,
            @RequestBody @Valid EmployeeDTO employeeDTO
    );

    /**
     * Bitta hodimni olish uhun yo'l
     * Agar bu yo'lning o'ziga id siz kelsa hodim qo'shish uchun kerak bo'lgan formani olish mumkin.
     * Agar bu yo'lga qaysidir hozimning id si bilan kelsa unda shu hodimning ma'lumotlarini olishi mumkin
     *
     * @param id
     * @return
     */
    @GetMapping(path = {GET_FORM_EMPLOYEE_PATH, GET_ONE_EMPLOYEE_PATH})
    ApiResult<?> getOneEmployee(
            @PathVariable(name = "id", required = false) UUID id
    );

    @DeleteMapping(path = DELETE_EMPLOYEE_PATH)
    ApiResult<?> deleteEmployee(
            @PathVariable(name = "id") UUID id
    );

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * VIEW LARGA KIRGAN PAYTIDA BARCHA VIEW LARNI VA SHU USERNI PERMISSIONLARINI BERIB YUBORADIGAN YO'L
     *
     * @return
     */
    @GetMapping(GET_VIEW_TYPES_PATH)
    ApiResult<InitialViewTypesDTO> getViewTypes();

    /**
     * KELGAN ID LI VIEW NI DB DAN OLIB QAYTARADI
     *
     * @param viewId
     * @return
     */
    @GetMapping(GET_VIEW_BY_ID_PATH)
    ApiResult<ViewDTO> getViewById(
            @PathVariable UUID viewId
    );

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

    @PostMapping(GET_VIEW_DATA_BY_ID_LIST_PATH + "/{viewId}")
    ApiResult<?> getViewDataByIdList(
            @PathVariable UUID viewId,
            @RequestBody List<String> idList
    );

    @PatchMapping(EDIT_ROW_DATA_PATH)
    ApiResult<?> editViewRowData(
            @RequestParam("viewId") UUID viewId,
            @RequestParam("rowId") UUID rowId,
            @RequestBody Map<String, Object> map);


    @PostMapping(RESIGNATION_EMPLOYEE_PATH)
    ApiResult<?> resignationEmployee(
            @RequestBody @Valid EmployeeResignationDTO employeeResignationDTO
    );

    @PostMapping(RESIGNATION_EMPLOYMENT_PATH)
    ApiResult<?> resignationEmployment(
            @RequestBody @Valid EmployeeResignationDTO employeeResignationDTO
    );

}

