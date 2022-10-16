package ai.ecma.appstaff.service.customField;


import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;

import java.util.List;

public interface CustomFieldValueService {

    ApiResult<CustomFieldValueDTO> addCustomFieldValue(CustomFieldValueDTO customFieldValueDTO);

    ApiResult<List<CustomFieldValueDTO>> addCustomFieldValueList(List<CustomFieldValueDTO> customFieldValueDTOList);
}
