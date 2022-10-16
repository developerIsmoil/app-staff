package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryTypeDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(path = EmployeeCategoryTypeController.EMPLOYEE_CATEGORY_TYPE_CONTROLLER_PATH)
public interface EmployeeCategoryTypeController {
    String EMPLOYEE_CATEGORY_TYPE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/employee-category-type";

    String ADD_EMPLOYEE_CATEGORY_TYPE_PATH = "/add";
    String EDIT_EMPLOYEE_CATEGORY_TYPE_PATH = "/edit/{id}";
    String GET_ALL_EMPLOYEE_CATEGORY_TYPE_PATH = "/get-all";
    String GET_ALL_EMPLOYEE_CATEGORY_TYPE_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_EMPLOYEE_CATEGORY_TYPE_PATH = "/get/{id}";
    String DELETE_EMPLOYEE_CATEGORY_TYPE_PATH = "/delete/{id}";

    /**
     * Hodim kategoriyasi turini yaratish uchun.
     * Hodim kategoriyasi turi bu ( A1, A2, B1, B2 )
     * Hodim kategoriyasi turini qo'shish uchun nomi va aktivligi kelishi kerak
     *
     * @param employeeCategoryTypeDTO
     * @return
     */
    @PostMapping(path = ADD_EMPLOYEE_CATEGORY_TYPE_PATH)
    ApiResult<?> addEmployeeCategoryType(
            @RequestBody EmployeeCategoryTypeDTO employeeCategoryTypeDTO
    );

    /**
     * Mavjud hodim kategoriyasi turini tahrirlash uchun
     *
     * @param id
     * @param employeeCategoryTypeDTO
     * @return
     */
    @PutMapping(path = EDIT_EMPLOYEE_CATEGORY_TYPE_PATH)
    ApiResult<?> editEmployeeCategoryType(
            @PathVariable(name = "id") UUID id,
            @RequestBody EmployeeCategoryTypeDTO employeeCategoryTypeDTO
    );

    /**
     * Barcha hodim kategoriyasi turlarini olish uchun. Bu bo'limlar ro'yxati pageda chaqiriladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_EMPLOYEE_CATEGORY_TYPE_PATH)
    ApiResult<?> getAllEmployeeCategoryType(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Faqat active true bo'lgan Hodim kategoriyasi turlarini olish uchun. Bu selectlarda chaqiriladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_EMPLOYEE_CATEGORY_TYPE_FOR_SELECT_PATH)
    ApiResult<?> getAllEmployeeCategoryTypeForSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta hodim kategoriyasi turini olish uchun
     *
     * @param id
     * @return
     */
    @GetMapping(path = GET_ONE_EMPLOYEE_CATEGORY_TYPE_PATH)
    ApiResult<?> getOneEmployeeCategoryType(
            @PathVariable(name = "id") UUID id
    );

    /**
     * Hodim kategoriyasi turini o'chirish uchun
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_EMPLOYEE_CATEGORY_TYPE_PATH)
    ApiResult<?> deleteEmployeeCategoryType(
            @PathVariable(name = "id") UUID id
    );


}
