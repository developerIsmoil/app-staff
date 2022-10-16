package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * DEPARTMENT UCHUN CONTROLLER
 * TIZIMGA QO'SHILADIGAN DEPARTMENTLAR BU FAQAT HISOBOT UCHUN KERAK.
 * NAVBARDA MODULNI ICHIDA CHIQADIGAN DEPARTMENTLARGA ALOQASI YO'Q
 */
@RequestMapping(path = DepartmentController.DEPARTMENT_CONTROLLER_PATH)
public interface DepartmentController {
    String DEPARTMENT_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/department";

    String ADD_DEPARTMENT_PATH = "/add";
    String EDIT_DEPARTMENT_PATH = "/edit/{id}";
    String GET_ALL_DEPARTMENT_PATH = "/get-all";
    String GET_ALL_DEPARTMENT_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_DEPARTMENT_PATH = "/get/{id}";
    String DELETE_DEPARTMENT_PATH = "/delete/{id}";

    /**
     * DEPARTMENT YARATISH UCHUN FAQAT NOMI BILAN ACTIVLIGI BERILSA BO'LDI
     *
     * @param departmentDTO BUNDA NAME BILAN ACTIVLIGI KELADI
     * @return SUCCESS OR ERROR
     */
    @PostMapping(path = ADD_DEPARTMENT_PATH)
    ApiResult<DepartmentDTO> addDepartment(
            @RequestBody @Valid DepartmentDTO departmentDTO
    );

    /**
     * DEPARTMENTNI NOMI VA ACTIVLIGI TAHRIRLANISHI MUMKIN.
     * BUNDA DEPARTMENTNING ID SI BILAN DTO KELISHI KERAK
     *
     * @param id            EDIT QILINISHI KERKA BO'LGAN DEPARTMENT ID SI
     * @param departmentDTO EDIT QILINGAN MA'LUMOT (EDIT QILINMAGANIYAM QAYTIB KELISHI KERAK)
     * @return
     */
    @PutMapping(path = EDIT_DEPARTMENT_PATH)
    ApiResult<DepartmentDTO> editDepartment(
            @PathVariable(name = "id") UUID id,
            @RequestBody DepartmentDTO departmentDTO
    );

    /**
     * BARCHA BO'LIMLARNI OLISH UCHUN. BU BO'LIMLAR RO'YXATI PAGEDA CHAQIRILADI
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_DEPARTMENT_PATH)
    ApiResult<List<DepartmentDTO>> getAllDepartment(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * FAQAT ACTIVE TRUE BO'LGAN BO'LIMLARNI OLISH UCHUN. BU SELECTLARDA CHAQIRILADI
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_DEPARTMENT_FOR_SELECT_PATH)
    ApiResult<List<DepartmentDTO>> getAllDepartmentForSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * BITTA BO'LIMNI OLISH UCHUN
     *
     * @param id
     * @return
     */
    @GetMapping(path = GET_ONE_DEPARTMENT_PATH)
    ApiResult<DepartmentDTO> getOneDepartment(
            @PathVariable(name = "id") UUID id
    );

    /**
     * BO'LIMNI O'CHIRISH UCHUN
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_DEPARTMENT_PATH)
    ApiResult<?> deleteDepartment(
            @PathVariable(name = "id") UUID id
    );


}
