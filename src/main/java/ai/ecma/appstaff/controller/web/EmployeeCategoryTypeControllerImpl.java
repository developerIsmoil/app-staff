package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryTypeDTO;
import ai.ecma.appstaff.service.EmployeeCategoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EmployeeCategoryTypeControllerImpl implements EmployeeCategoryTypeController {

    private final EmployeeCategoryTypeService employeeCategoryTypeService;

    /**
     * Hodim kategoriyasi turini yaratish uchun.
     * Hodim kategoriyasi turi bu ( A1, A2, B1, B2 )
     * Hodim kategoriyasi turini qo'shish uchun nomi va aktivligi kelishi kerak
     *
     * @param employeeCategoryTypeDTO
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_ADD_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> addEmployeeCategoryType(EmployeeCategoryTypeDTO employeeCategoryTypeDTO) {
        return employeeCategoryTypeService.addEmployeeCategoryType(employeeCategoryTypeDTO);
    }

    /**
     * Hodim kategoriyasi turini tahrirlash uchun ID bilan DTO kelishi kerak.
     *
     * @param id
     * @param employeeCategoryTypeDTO
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> editEmployeeCategoryType(UUID id, EmployeeCategoryTypeDTO employeeCategoryTypeDTO) {
        return employeeCategoryTypeService.editEmployeeCategoryType(id, employeeCategoryTypeDTO);
    }


    /**
     * Barcha hodim kategoriyalarini olish uchun.
     * Bunda barcha delete=false bo'lganlar olinadi
     *
     * @param page
     * @param size
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> getAllEmployeeCategoryType(Integer page, Integer size) {
        return employeeCategoryTypeService.getAllEmployeeCategoryType(page, size);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> getAllEmployeeCategoryTypeForSelect(Integer page, Integer size) {
        return employeeCategoryTypeService.getAllEmployeeCategoryTypeForSelect(page, size);
    }


    /**
     * ID bo'yicha bitta hodim kategoriya turini olish
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> getOneEmployeeCategoryType(UUID id) {
        return employeeCategoryTypeService.getOneEmployeeCategoryType(id);
    }


    /**
     * Hodim kategoriyasini o'chirish uchun ishlatiladi
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_EMPLOYEE_CATEGORY_TYPE})
    @Override
    public ApiResult<?> deleteEmployeeCategoryType(UUID id) {
        return employeeCategoryTypeService.deleteEmployeeCategoryType(id);
    }

}
