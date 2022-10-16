package ai.ecma.appstaff.controller.customField;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;
import ai.ecma.appstaff.service.customField.CustomFieldValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomFieldValueControllerImpl implements CustomFieldValueController {
    private final CustomFieldValueService customFieldValueService;


    @CheckAuth(permission = PermissionEnum.FINANCE_ADD_CUSTOM_FIELD_VALUE)
    @Override
    public ApiResult<CustomFieldValueDTO> addCustomFieldValue(CustomFieldValueDTO customFieldValueDTO) {
        return customFieldValueService.addCustomFieldValue(customFieldValueDTO);
    }


    @CheckAuth(permission = PermissionEnum.FINANCE_ADD_CUSTOM_FIELD_VALUE)
    @Override
    public ApiResult<List<CustomFieldValueDTO>> addCustomFieldValueList(List<CustomFieldValueDTO> customFieldValueDTOList) {
        return customFieldValueService.addCustomFieldValueList(customFieldValueDTOList);
    }
}
