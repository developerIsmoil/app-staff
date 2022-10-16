package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PrivilegeTypeDTO;
import ai.ecma.appstaff.service.PrivilegeTypeService;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PrivilegeTypeControllerImpl implements PrivilegeTypeController {

    private final PrivilegeTypeService privilegeTypeService;

    @CheckAuth(permission = {PermissionEnum.HRM_ADD_PRIVILEGE_TYPE})
    @Override
    public ApiResult<PrivilegeTypeDTO> create(PrivilegeTypeDTO privilegeTypeDTO) {
        log.info("method-entered : create, params : {} privilegeTypeDTO", privilegeTypeDTO);
        PrivilegeTypeDTO resultPrivilegeTypeDTO = privilegeTypeService.create(privilegeTypeDTO);
        log.info("method-exit SUCCESS");
        return ApiResult.successResponse(resultPrivilegeTypeDTO, ResponseMessage.SUCCESS_PRIVILEGE_TYPE_SAVED);
    }


    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_PRIVILEGE_TYPE})
    @Override
    public ApiResult<PrivilegeTypeDTO> edit(UUID id, PrivilegeTypeDTO privilegeTypeDTO) {
        log.info("method-entered : edit, params : {} id , {} privilegeTypeDTO", id, privilegeTypeDTO);
        PrivilegeTypeDTO resultPrivilegeTypeDTO = privilegeTypeService.edit(id, privilegeTypeDTO);
        log.info("method-exit SUCCESS");
        return ApiResult.successResponse(resultPrivilegeTypeDTO, ResponseMessage.SUCCESS_PRIVILEGE_TYPE_EDITED);
    }


    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_PRIVILEGE_TYPE})
    @Override
    public ApiResult<?> delete(UUID id) {
        log.info("method-entered : delete, params : {} id", id);
        privilegeTypeService.delete(id);
        log.info("method-exit SUCCESS");
        return ApiResult.successResponse(ResponseMessage.SUCCESS_PRIVILEGE_TYPE_DELETED);
    }


    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_PRIVILEGE_TYPE})
    @Override
    public ApiResult<List<PrivilegeTypeDTO>> getAll() {
        log.info("method-entered : getAll");
        List<PrivilegeTypeDTO> resultPrivilegeTypeDTOList = privilegeTypeService.getAll();
        log.info("method-exit SUCCESS {} resultPrivilegeTypeDTOList", resultPrivilegeTypeDTOList.size());
        return ApiResult.successResponse(resultPrivilegeTypeDTOList);
    }


    @CheckAuth
    @Override
    public ApiResult<List<PrivilegeTypeDTO>> getAllForSelect() {
        log.info("method-entered : getAllForSelect");
        List<PrivilegeTypeDTO> resultPrivilegeTypeDTOList = privilegeTypeService.getAllForSelect();
        log.info("method-exit SUCCESS {} resultPrivilegeTypeDTOList", resultPrivilegeTypeDTOList.size());
        return ApiResult.successResponse(resultPrivilegeTypeDTOList);
    }


    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_PRIVILEGE_TYPE})
    @Override
    public ApiResult<PrivilegeTypeDTO> getById(UUID id) {
        log.info("method-entered : getById, params : {} id ", id);
        PrivilegeTypeDTO resultPrivilegeTypeDTO = privilegeTypeService.getById(id);
        log.info("method-exit SUCCESS");
        return ApiResult.successResponse(resultPrivilegeTypeDTO);
    }

}
