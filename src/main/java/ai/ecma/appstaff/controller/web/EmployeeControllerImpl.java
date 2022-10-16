package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeDTO;
import ai.ecma.appstaff.payload.EmployeeResignationDTO;
import ai.ecma.appstaff.payload.view.GenericViewResultDTO;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.service.EmployeeService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Employee controller.
 */
@RestController
@RequiredArgsConstructor
public class EmployeeControllerImpl implements EmployeeController {

    private final EmployeeService employeeService;
    private final ViewService viewService;


    @CheckAuth(permission = {PermissionEnum.HRM_ADD_EMPLOYEE})
    @Override
    public ApiResult<?> addEmployee(EmployeeDTO employeeDTO) {
        return employeeService.addEmployee(employeeDTO);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_EMPLOYEE})
    @Override
    public ApiResult<?> editEmployee(UUID id, EmployeeDTO employeeDTO) {
        return employeeService.editEmployee(id, employeeDTO);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_EMPLOYEE})
    @Override
    public ApiResult<?> getOneEmployee(UUID id) {
//        return employeeService.getOneEmployee(id);
        if (id == null) {
            return employeeService.getEmployeeForm();
        } else {
            return employeeService.getEmployeeFormById(id);
        }
    }

    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_EMPLOYEE})
    @Override
    public ApiResult<?> deleteEmployee(UUID id) {
        return employeeService.deleteEmployee(id);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CheckAuth(permission = {PermissionEnum.HRM_GET_EMPLOYEE_VIEW_TYPES})
    @Override
    public ApiResult<InitialViewTypesDTO> getViewTypes() {
        return viewService.getViewTypes(TableNameConstant.EMPLOYEE);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_EMPLOYEE_VIEW})
    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {
        return viewService.getViewById(viewId);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GENERIC_EMPLOYEE_VIEW})
    @Override
    public ApiResult<?> genericView(int page, ViewDTO viewDTO, String statusId) {

        GenericViewResultDTO result = new GenericViewResultDTO();

        return viewService.genericView(page, viewDTO, statusId, null);

    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_EMPLOYEE_VIEW_DATA})
    @Override
    public ApiResult<?> getViewDataByIdList(UUID viewId, List<String> idList) {

//        if (idList.size() > RestConstants.MAX_GENERIC_VALUE_SIZE) {
//            throw RestException.restThrow("MAX_GENERIC_VALUE_SIZE", HttpStatus.BAD_REQUEST);
//        }
        return viewService.getViewDataByIdList(viewId, idList);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_EMPLOYEE_VIEW_ROW_DATA})
    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {
        return employeeService.editViewRowData(viewId, rowId, map);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_RESIGNATION_EMPLOYEE})
    @Override
    public ApiResult<?> resignationEmployee(EmployeeResignationDTO employeeResignationDTO) {
        return employeeService.resignationEmployee(employeeResignationDTO);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_RESIGNATION_EMPLOYEE})
    @Override
    public ApiResult<?> resignationEmployment(EmployeeResignationDTO employeeResignationDTO) {
        return employeeService.resignationEmployment(employeeResignationDTO);
    }


}
