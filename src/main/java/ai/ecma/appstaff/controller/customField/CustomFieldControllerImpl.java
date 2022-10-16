package ai.ecma.appstaff.controller.customField;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldAddDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldEditDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.ViewColumnDTO;
import ai.ecma.appstaff.service.customField.CustomFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomFieldControllerImpl implements CustomFieldController {
    private final CustomFieldService customFieldService;


    @CheckAuth(permission = PermissionEnum.FINANCE_ADD_CUSTOM_FIELD)
    @Override
    public ApiResult<ViewColumnDTO> addCustomField(CustomFieldAddDTO customFieldAddDTO) {
        return customFieldService.addCustomField(customFieldAddDTO);
    }

    @CheckAuth(permission = PermissionEnum.FINANCE_DELETE_CUSTOM_FIELD)
    @Override
    public ApiResult<Boolean> deleteCustomField(UUID customFieldId) {
        return customFieldService.deleteCustomField(customFieldId);
    }

    @CheckAuth(permission = PermissionEnum.FINANCE_EDIT_CUSTOM_FIELD)
    @Override
    public ApiResult<ViewColumnDTO> editCustomField(CustomFieldEditDTO customFieldEditDTO) {
        return customFieldService.editCustomField(customFieldEditDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<CustomFiledTypeConfigDTO> editViewColumn(CustomFiledTypeConfigDTO typeConfig, UUID customFieldId, String customFieldName) {
        return customFieldService.editViewColumn(typeConfig, customFieldId,customFieldName);
    }
}
