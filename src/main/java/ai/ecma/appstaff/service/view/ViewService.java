package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum;
import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.view.*;

import java.util.*;


public interface ViewService {

    ApiResult<InitialViewTypesDTO> getViewTypes(String tableName);

    ApiResult<ViewDTO> getViewById(UUID viewId);

    ApiResult<ViewDTO> duplicateViewById(UUID viewId);

    ApiResult<ViewDTO> updateView(ViewDTO viewDTO);

    ApiResult<ViewDTO> editView(ViewEditDTO viewEditDTO);

    ApiResult<ViewDTO> addView(ViewAddDTO viewAddDTO);

    ViewObject createDefaultView(boolean personal, boolean isDefault, String viewName, ViewTypeEnum viewType, String tableName, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapMap, Optional<Date> timesheetDate);

    void updateDefaultView(String tableName, ViewTypeEnum viewType, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap);

    ViewColumnDTO addFieldToView(ViewObject viewObject, CustomField customField);

    ViewObject getViewObjectByIdIfNotThrow(UUID viewId);

    ApiResult<GenericViewResultDTO> genericView(int page, ViewDTO viewDTO, String forGroupByColumnID,List<String> idListByOtherService);

    ApiResult<ViewMemberHierarchy> sharingPermissions(UUID viewId);

    ApiResult<ViewMemberDTO> changeMemberPermission(PermissionEditDTO permissionEditDTO);


    //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
    Map<String, CustomField> mapCustomFieldToHashMap(List<CustomField> customFieldList);

    //VIEW COLUMN NI ViewColumnDTO GA PARSE QILADI
    ViewColumnDTO mapViewColumnToViewColumnDTOForHeader(String tableName, ViewColumn viewColumn, boolean fromDefaultView, Map<String, CustomField> customFieldMap);

    List<String> genericInitialSumForTimeSheetEmployee(List<String> entityIdListForGenericView);

    void otherServiceSortingAndSearchingByRedis(ViewDTO viewDTO, ViewObject viewObject);

    ApiResult<?> getViewDataByIdList(UUID viewId, List<String> idList);

    ApiResult<Boolean> deleteViewById(UUID viewId);

    List<Map<String, Object>> getRowData(UUID viewId, List<String> idList);


    Map<String, CustomFieldOptionDTO> mapViewColumnTypeConfig(List<CustomFieldOptionDTO> options);
}
