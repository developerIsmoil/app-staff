package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(path = EmployeeCategoryController.EMPLOYEE_CATEGORY_CONTROLLER_PATH)
public interface EmployeeCategoryController {
    String EMPLOYEE_CATEGORY_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/employee-category";

    String ADD_EMPLOYEE_CATEGORY_PATH = "/add";
    String EDIT_EMPLOYEE_CATEGORY_PATH = "/edit/{id}";
    String GET_ALL_EMPLOYEE_CATEGORY_PATH = "/get-all";
    String GET_ALL_EMPLOYEE_CATEGORY_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_EMPLOYEE_CATEGORY_PATH = "/get";
    String GET_ONE_EMPLOYEE_CATEGORY_ID_PATH = "/get/{id}";
    String DELETE_EMPLOYEE_CATEGORY_PATH = "/delete/{id}";

    /**
     * Bunda aniq bir hodim kategoriyasini yaratish uchun ishlatilgan.
     * Hodim kategoriyasini yaratish uchun kiritiladigan qiymatlar:
     * Hodim kategoriyasi turi, Bo'lim, Lavozim lar kiritilishi shart bo'ladi
     *
     * @param employeeCategoryDTO
     * @return
     */
    @PostMapping(path = ADD_EMPLOYEE_CATEGORY_PATH)
    ApiResult<?> addEmployeeCategory(
            @RequestBody EmployeeCategoryDTO employeeCategoryDTO
    );


    /**
     * Hodim kategoriyasini tahrirlash uchun ID bilan DTO kelishi kerak.
     *
     * @param id
     * @param employeeCategoryDTO
     * @return
     */
    @PutMapping(path = EDIT_EMPLOYEE_CATEGORY_PATH)
    ApiResult<?> editEmployeeCategory(
            @PathVariable(name = "id") UUID id,
            @RequestBody EmployeeCategoryDTO employeeCategoryDTO
    );

    /**
     * Barcha hodim kategoriyalarini olish uchun. Bu bo'limlar ro'yxati pageda chaqiriladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_EMPLOYEE_CATEGORY_PATH)
    ApiResult<?> getAllEmployeeCategory(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Faqat active true bo'lgan hodim kategoriyalarini olish uchun. Bu selectlarda chaqiriladi
     *
     * @param departmentId
     * @param positionId
     * @return
     */
    @GetMapping(path = GET_ALL_EMPLOYEE_CATEGORY_FOR_SELECT_PATH)
    ApiResult<?> getAllEmployeeCategoryForSelect(
            @RequestParam(name = "departmentId") UUID departmentId,
            @RequestParam(name = "positionId") UUID positionId,
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta hodim kategoriyasini olish uchun
     *
     * @param id
     * @return
     */
    @GetMapping(path = {GET_ONE_EMPLOYEE_CATEGORY_PATH, GET_ONE_EMPLOYEE_CATEGORY_ID_PATH})
    ApiResult<?> getOneEmployeeCategory(
            @PathVariable(name = "id", required = false) UUID id
    );

    /**
     * Hodim kategoriyasini o'chirish uchun
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_EMPLOYEE_CATEGORY_PATH)
    ApiResult<?> deleteEmployeeCategory(
            @PathVariable(name = "id") UUID id
    );


}
