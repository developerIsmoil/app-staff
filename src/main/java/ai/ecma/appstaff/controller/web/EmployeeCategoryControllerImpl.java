package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryDTO;
import ai.ecma.appstaff.service.EmployeeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EmployeeCategoryControllerImpl implements EmployeeCategoryController {

    private final EmployeeCategoryService employeeCategoryService;


    @CheckAuth(permission = {PermissionEnum.HRM_ADD_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<EmployeeCategoryDTO> addEmployeeCategory(EmployeeCategoryDTO employeeCategoryDTO) {
        return employeeCategoryService.addEmployeeCategory(employeeCategoryDTO);
    }


    /**
     * Hodim kategoriyasini tahrirlash uchun ID bilan DTO kelishi kerak.
     *
     * @param id
     * @param employeeCategoryDTO
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<EmployeeCategoryDTO> editEmployeeCategory(UUID id, EmployeeCategoryDTO employeeCategoryDTO) {
        return employeeCategoryService.editEmployeeCategory(id, employeeCategoryDTO);
    }


    /**
     * Barcha hodim kategoriya turilarini olish uchun.
     * Bunda barcha delete=false bo'lganlar olinadi
     *
     * @param page
     * @param size
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategory(Integer page, Integer size) {
        return employeeCategoryService.getAllEmployeeCategory(page, size);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategoryForSelect(UUID departmentId, UUID positionId, Integer page, Integer size) {
        return employeeCategoryService.getAllEmployeeCategoryForSelect(departmentId, positionId, page, size);
    }

    /**
     * ID bo'yicha bitta hodim kategoriya turini olish
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<EmployeeCategoryDTO> getOneEmployeeCategory(UUID id) {

        if (id == null) {
            return employeeCategoryService.getFormEmployeeCategory();
        } else {
            return employeeCategoryService.getOneEmployeeCategory(id);
        }

    }


    /**
     * Hodim kategoriyasi turini o'chirish uchun ishlatiladi
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_EMPLOYEE_CATEGORY})
    @Override
    public ApiResult<?> deleteEmployeeCategory(UUID id) {
        return employeeCategoryService.deleteEmployeeCategory(id);
    }

}
