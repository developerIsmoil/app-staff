package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DepartmentControllerImpl implements DepartmentController {

    private final DepartmentService departmentService;


    @CheckAuth(permission = {PermissionEnum.HRM_ADD_DEPARTMENT})
    @Override
    public ApiResult<DepartmentDTO> addDepartment(DepartmentDTO departmentDTO) {
        return departmentService.addDepartment(departmentDTO);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_DEPARTMENT})
    @Override
    public ApiResult<DepartmentDTO> editDepartment(UUID id, DepartmentDTO departmentDTO) {
        return departmentService.editDepartment(id, departmentDTO);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_DEPARTMENT})
    @Override
    public ApiResult<List<DepartmentDTO>> getAllDepartment(Integer page, Integer size) {
        return departmentService.getAllDepartment(page, size);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_DEPARTMENT})
    @Override
    public ApiResult<List<DepartmentDTO>> getAllDepartmentForSelect(Integer page, Integer size) {
        return departmentService.getAllDepartmentForSelect(page, size);
    }

    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_DEPARTMENT})
    @Override
    public ApiResult<DepartmentDTO> getOneDepartment(UUID id) {
        return departmentService.getOneDepartment(id);
    }


    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_DEPARTMENT})
    @Override
    public ApiResult<?> deleteDepartment(UUID id) {
        return departmentService.deleteDepartment(id);
    }

}
