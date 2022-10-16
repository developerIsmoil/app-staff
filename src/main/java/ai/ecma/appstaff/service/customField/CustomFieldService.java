package ai.ecma.appstaff.service.customField;


import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldAddDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldEditDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.ViewColumnDTO;

import java.util.List;
import java.util.UUID;


public interface CustomFieldService {

    ApiResult<ViewColumnDTO> addCustomField(CustomFieldAddDTO customFieldAddDTO);

    //TABLE NAME VA OWNER ID BERILSA SHU TABLE NAME VA OWNER ID LI CUSTOM FIELD DTO NI VALUE BILAN BIRGA QAYTARADI
    List<CustomFieldDTO> getCustomFieldByTableNameAndOwnerId(String tableName, String ownerId);

    ApiResult<Boolean> deleteCustomField(UUID customFieldId);

    //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI

    ApiResult<ViewColumnDTO> editCustomField(CustomFieldEditDTO customFieldEditDTO);

    CustomFiledTypeConfigDTO mapCustomFieldTypeConfigFromCustomField(CustomField customField);

    ApiResult<CustomFiledTypeConfigDTO> editViewColumn(CustomFiledTypeConfigDTO typeConfig, UUID customFieldId, String customFieldName);

}
