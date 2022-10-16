package ai.ecma.appstaff.service.view;


import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;

import java.util.List;

public interface ViewColumnOptionsService {


    List<CustomFieldOptionDTO> getOptionForEmployee(String columnName);

    List<CustomFieldOptionDTO> getOptionForTimeSheetEmployee(String columnName);

    List<CustomFieldOptionDTO> getOptionForTimeSheetEmployeeForFinance(String columnName);

    //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
    CustomFiledTypeConfigDTO mapCustomFieldTypeConfigFromEntityColumn(ViewColumn viewColumn, String tableName);
}
