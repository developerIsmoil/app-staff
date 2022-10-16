package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.entity.view.*;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.projection.OrderIndexAndViewIdProjection;
import ai.ecma.appstaff.projection.ViewObjectAndPermission;
import ai.ecma.appstaff.repository.customField.CustomFieldRepository;
import ai.ecma.appstaff.repository.view.*;
import ai.ecma.appstaff.service.customField.CustomFieldService;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum.*;
import static ai.ecma.appstaff.enums.PermissionEnum.*;
import static ai.ecma.appstaff.enums.PermissionUserThisViewEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ViewServiceImpl implements ViewService {
    private final ViewObjectRepository viewObjectRepository;
    private final UserViewRepository userViewRepository;
    private final ViewColumnRepository viewColumnRepository;
    private final CustomFieldRepository customFieldRepository;
    private final ViewFilterRepository viewFilterRepository;
    private final ViewFilterFieldRepository viewFilterFieldRepository;
    private final ViewSortingRepository viewSortingRepository;
    private final QueryService queryService;
    private final SortAndSearchForOtherService sortAndSearchForOtherService;
    private final FavouriteViewRepository favouriteViewRepository;
    private final QueryServiceForViewData queryServiceForViewData;
    private final CustomFieldService customFieldService;
    private final ViewFilterSearchingColumnRepository viewFilterSearchingColumnRepository;
    private final ViewFilterFieldValueRepository viewFilterFieldValueRepository;
    private final FeignService feignService;
    private final QueryServiceInitialSum queryServiceInitialSum;
    private final ViewColumnOptionsService viewColumnOptionsService;
    private final ViewCheckingService viewCheckingService;

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {};

    //GET VIEW TYPES
    @Override
    public ApiResult<InitialViewTypesDTO> getViewTypes(String tableName) {

        //HOZIR TIZIMDA TURGAN USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //USHBU USER ADMINMI
        boolean isAdmin = currentUser.isAdmin();

        //TIZIM DAGI USER DA MANAGE VIEW HUQUQI BORMI
        boolean haveManagePermission = CommonUtils.havePermission(currentUser.getPermissions(), tableName);

        //PERMISSION VA VIEW OBJECT DAN IBORAT PROJECTTON QAYTARADI
        List<ViewObjectAndPermission> viewObjectAndPermissionList;

        //AGAR TIZIMDAGI USER ADMIN BO'LSA
        // 1.1 BARCHA VIEW LARNI QAYTAR
        // 1.2 AKS HOLDA BARCHA PUBLIC VIEW LAR VA O'ZI QATNASHGAN PRIVATE VIEW LAR
        if (currentUser.isAdmin())
            viewObjectAndPermissionList = viewObjectRepository.findAllPublicViewAndPrivateInPermissionForAdmin(currentUser.getId(), tableName, FULL.name());
        else
            viewObjectAndPermissionList = viewObjectRepository.findAllPublicViewAndPrivateInPermission(currentUser.getId(), tableName);

        //VIEW TURLARI VA PERMISSION LARNI FRONT END GA OTISH UCHUN MO'LJALLANGAN CLASS OCHILDI
        InitialViewTypesDTO initialViewTypesDTO = new InitialViewTypesDTO();

        //YANGI TABLE YARATA OLISH UCHUN USER DA MANAGE HUQUQI BO'LSIN YOKI ADMIN BO'LSIN
        ViewPermissionDTO viewPermissionDTO = ViewPermissionDTO.builder().canCreateView(haveManagePermission || isAdmin).build();
        initialViewTypesDTO.setPermissions(viewPermissionDTO);

        List<ViewTypesDTO> viewTypesDTOS = new ArrayList<>();

        //HAR BIRINI AYLANIB UMUMIY LISTGA QO'SHAMIZ
        for (ViewTypeEnum viewTypeEnum : ViewTypeEnum.getByOrderIndex()) {
            ViewTypesDTO tableViewTypesDTO = mapViewTypesDTO(viewTypeEnum, viewObjectAndPermissionList);

            if (tableViewTypesDTO.getDefaultView() != null && tableViewTypesDTO.getDefaultView().getId() != null)
                viewTypesDTOS.add(tableViewTypesDTO);
        }

        initialViewTypesDTO.setViewTypes(viewTypesDTOS);

        return ApiResult.successResponse(initialViewTypesDTO);
    }

    //GET BY ID
    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {

        //VIEW NI ID SI ORQALI OLADI AKS HOLDA THROW
        ViewObject viewObject = getViewObjectByIdIfNotThrow(viewId);

        //YIG'IB OLINGAN CUSTOM FIELD LARNI DB DAN OLIB KELINDI
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
        Map<String, CustomField> customFieldToHashMap = mapCustomFieldToHashMap(customFieldList);

        ViewDTO viewDTO = mapViewObjectToViewDTO(viewObject, customFieldToHashMap);

        return ApiResult.successResponse(viewDTO);
    }

    //DUPLICATE VIEW
    @Override
    public ApiResult<ViewDTO> duplicateViewById(UUID viewId) {

        ViewObject viewObject = getViewObjectByIdIfNotThrow(viewId);

        UserDTO currentUser = CommonUtils.getCurrentUser();

        //USER SHU VIEW DAN NUSHA OLOLADIMI TEKSHIRADI AKS HOLDA THROW

        checkUserCanDuplicateView(viewObject, currentUser);

        //BERILGAN VIEW_OBECT DAN NUSHA OLIB QAYTARADI
        ViewObject copyViewObject = mapNewViewObject(viewObject);

        viewObjectRepository.save(copyViewObject);

        //VIEW GA QO'SHILISHI KERAK BO'LGAN BARCHA USER LARNI ID LARINI LISTINI QAYTARADI
        Set<UUID> viewMemberIdList = getAllViewMemberIdList(currentUser.getId(), viewObject.isPublicly());

        //USER VIEW GA USERLARNI BERILGAN PERMISSION ORQALI SAQLAB QO'YADI
        saveUserListToUserView(viewObject, FULL, viewMemberIdList);

        //VIEW NI BARCHA COLUMN LARIDAN NUSXA OLIB QAYTARADI
        List<ViewColumn> viewColumnList = mapNewViewColumns(viewObject.getColumnList(), copyViewObject);

        viewColumnRepository.saveAll(viewColumnList);

        copyViewObject.setColumnList(viewColumnList);

        viewColumnRepository.saveAll(viewColumnList);

        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
        Map<String, CustomField> customFieldMap = mapCustomFieldToHashMap(customFieldList);

        //VIEW_OBJECT DAN VIEW_DTO YASAB QAYTARADI
        ViewDTO viewDTO = mapViewObjectToViewDTO(copyViewObject, customFieldMap);

        return ApiResult.successResponse(viewDTO);
    }

    //UPDATE VIEW
    @Override
    public ApiResult<ViewDTO> updateView(ViewDTO viewDTO) {

        // USERNING VIEWDAGI HUQUQI FULL VA EDIT BO'LSA FILTER, SEARCH VA SORTLARNI DB GA SAQLANADI
        //AGAR USERNING VIEWDAGI HUQUQI VIEW_ONLY BO'LSA FILTER SEARCH VA SORTLARNI DB GA SAQLAMAYMIZ
        ViewObject viewObject = viewObjectRepository.findById(viewDTO.getId()).orElseThrow(() -> RestException.restThrow("NOT.FOUND"));

        //YIG'IB OLINGAN CUSTOM FIELD LARNI DB DAN OLIB KELINDI
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
        Map<String, CustomField> customFieldMap = mapCustomFieldToHashMap(customFieldList);

        UserDTO currentUser = CommonUtils.getCurrentUser();

        boolean userIsAdmin = currentUser.isAdmin();

        boolean haveMangeViewPermissionInCurrentUser = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());

        UserView userView = userViewRepository.findByUserIdAndViewId(currentUser.getId(), viewObject.getId()).orElse(null);

        //USHBU METHOD USER VIEW NI UPDATE QILA OLADIMI TEKSHIRIB BERADI
        boolean canEdit = userCanUpdateView(userView, viewObject.isPublicly(), userIsAdmin, haveMangeViewPermissionInCurrentUser);

        //AGAR USER VIEW NI UPDATE QILA OLSA
        if (canEdit) {

            viewCheckingService.checkingViewFilterAndSearchingAndSorting(viewDTO, customFieldMap, viewObject.getTableName());

            //REQUEST DA KELGAN VIEW_DTO DAN VIEW_OBJECT YASALDI VA SAQLANDI
            viewObject = saveViewObject(viewDTO, viewObject, customFieldMap);

            ViewFilter viewFilter = mapViewFilter(viewObject, viewDTO);

            List<ViewSorting> viewSortingList = updateViewSorting(viewObject, viewDTO);

            viewObject.setViewFilterId(viewFilter.getId());
            viewObject.setSortingList(viewSortingList);

            viewFilterRepository.save(viewFilter);
            viewObjectRepository.save(viewObject);
        }

        ViewDTO res = mapViewObjectToViewDTO(viewObject, customFieldMap);

        return ApiResult.successResponse(res);
    }

    private void checkingViewColumnListValid(Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityFields,
                                             Map<String, CustomField> customFieldMap,
                                             List<ViewColumnDTO> viewColumnDTOList) {

        for (ViewColumnDTO viewColumnDTO : viewColumnDTOList) {

            if (viewColumnDTO.isCustomField()) {
                //CUSTOM_FIELD_MAP DA BERILGAN FIELD BO'LMASA XATOLIKKA OTISH KERAK SABABI YO'Q FIELD NI FILTE QILISHIMIZ NI SO'RAYAPTI
                if (customFieldMap.get(viewColumnDTO.getId()) == null) {
                    throw RestException.restThrow("Bunday column(custom_field) yo'q:" + viewColumnDTO.getId(), HttpStatus.BAD_REQUEST);
                }
            } else {

                //KELGAN FIELD ENTITY MAP DA BO'LMASA CUSTOM FIELD MAP NI ICHIDAN QIDIRIB KO'RAMIZ
                if (!entityFields.containsKey(viewColumnDTO.getId())) {
                    throw RestException.restThrow("Bunday column yo'q:" + viewColumnDTO.getId(), HttpStatus.BAD_REQUEST);
                }
            }
        }

    }

    private ViewColumn mapNewViewColumn(UUID viewObjectId, ViewColumnDTO viewColumn, String columnId) {

        ViewColumn copyViewColumn = new ViewColumn(
                viewObjectId,
                viewColumn.getName(),
                viewColumn.getOrderIndex(),
                viewColumn.isPinned(),
                viewColumn.isHidden(),
                viewColumn.getWidth(),
                viewColumn.getType(),
                Boolean.TRUE.equals(viewColumn.isCustomField()) ? UUID.fromString(viewColumn.getId()) : null
        );
        viewColumn.setId(columnId);

        return copyViewColumn;
    }

    private Map<String, ViewColumn> mapViewColumnMap(List<ViewColumn> viewColumnList) {

        Map<String, ViewColumn> result = new HashMap<>();
        for (ViewColumn viewColumn : viewColumnList) {
            result.put(viewColumn.getId().toString(), viewColumn);
        }
        return result;
    }

    private List<ViewSorting> updateViewSorting(ViewObject viewObject, ViewDTO viewDTO) {

        List<ViewSorting> viewSortingList = viewSortingRepository.findAllByViewObjectIdOrderByOrderIndex(viewObject.getId());
        Map<String, ViewSorting> mapViewSortingMap = mapViewSortingMap(viewSortingList);
        List<ViewSortingDTO> sortingDTOList = viewDTO.getSorting();

        List<ViewSorting> notSavedViewSorting = new ArrayList<>();
        List<UUID> deletedViewSortingIdList = new ArrayList<>();

        double i = 1;
        for (ViewSortingDTO viewSorting : sortingDTOList) {
            if (mapViewSortingMap.get(viewSorting.getField()) == null) {
                notSavedViewSorting.add(mapViewSortingDtoToViewSorting(viewSorting, viewObject, i));
            } else {
                ViewSorting viewSortingNotDeleted = mapViewSortingMap.get(viewSorting.getField());
                viewSortingNotDeleted.setOrderIndex(i);
                viewSortingNotDeleted.setDirection(viewSorting.getDirection());
                notSavedViewSorting.add(viewSortingNotDeleted);
                mapViewSortingMap.remove(viewSorting.getField());
            }
            i++;
        }

        for (Map.Entry<String, ViewSorting> viewSortingEntry : mapViewSortingMap.entrySet()) {
            deletedViewSortingIdList.add(viewSortingEntry.getValue().getId());
        }

        viewSortingRepository.deleteAllById(deletedViewSortingIdList);

        viewSortingRepository.saveAll(notSavedViewSorting);

        return notSavedViewSorting;
    }

    private ViewSorting mapViewSortingDtoToViewSorting(ViewSortingDTO viewSortingDTO, ViewObject viewObject, double i) {
        return new ViewSorting(
                viewObject.getId(),
                viewSortingDTO.getField(),
                i,
                viewSortingDTO.getDirection(),
                viewSortingDTO.getFieldType(),
                viewSortingDTO.isCustomField()

        );
    }

    private Map<String, ViewSorting> mapViewSortingMap(List<ViewSorting> viewSortingList) {
        Map<String, ViewSorting> result = new HashMap<>();
        for (ViewSorting viewSorting : viewSortingList) {
            result.put(viewSorting.getField(), viewSorting);
        }
        return result;
    }


    //EDIT VIEW NAME
    @Override
    public ApiResult<ViewDTO> editView(ViewEditDTO viewEditDTO) {

        //VIEW NI ID SI ORQALI OLADI AKS HOLDA THROW
        ViewObject viewObject = getViewObjectByIdIfNotThrow(viewEditDTO.getViewId());

        //QAYSI FIELD O'ZGARGANINI QAYTARADI
//        ViewChangedProperty viewChangedProperty = getChangedProperty(viewEditDTO);


        if (viewEditDTO.getViewName() != null) {
            //VIEW NI NOMINI O'ZGARTIRADI
            changeViewName(viewObject, viewEditDTO.getViewName());
        }
        if (viewEditDTO.getPublicly() != null) {
            //VIEW NI FAVOURITE QILISH YOKI UNI FAVOURITE DAN OLIB TASHLASH
            addOrRemoveFavourite(viewObject, viewEditDTO.getFavourite());
        }
        if (viewEditDTO.getFavourite() != null) {
            //VIEW NI PUBLIC YOKI PRIVATE QILISH UCHUN METHOD
            changePubliclyView(viewEditDTO.getPublicly(), viewObject);
        }

        viewObjectRepository.save(viewObject);

        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
        Map<String, CustomField> customFieldMap = mapCustomFieldToHashMap(customFieldList);

        viewObject = getViewObjectByIdIfNotThrow(viewEditDTO.getViewId());

        //VIEW_OBJECT DAN VIEW_DTO YASAB QAYTARADI
        ViewDTO viewDTO = mapViewObjectToViewDTO(viewObject, customFieldMap);

        return ApiResult.successResponse(viewDTO);
    }

    //ADD VIEW
    @Override
    public ApiResult<ViewDTO> addView(ViewAddDTO viewAddDTO) {

        UserDTO currentUser = CommonUtils.getCurrentUser();

        UUID defaultViewId = viewAddDTO.getDefaultViewId();

        ViewObject defaultViewObject = viewObjectRepository.findById(defaultViewId).orElseThrow(() -> RestException.restThrow("Default view yo'q", HttpStatus.BAD_REQUEST));

        //VIEW GA QO'SHILISHI KERAK BO'LGAN BARCHA USER LARNI ID LARINI LISTINI QAYTARADI
        Set<UUID> viewMemberIdList = getAllViewMemberIdList(currentUser.getId(), !viewAddDTO.getPersonal());

        //MANAGE VIEW HUQUQI BORMI
        boolean haveManageView = CommonUtils.havePermission(currentUser.getPermissions(), defaultViewObject.getTableName());

        //AGAR MANAGE VIEW HUQUQI BO'LMASA VA ADMIN BO'LMASA EXCEPTION GA OT
        if (!haveManageView && !currentUser.isAdmin())
            throw RestException.restThrow("Sizda view ochish uchun huquq yo'q", HttpStatus.FORBIDDEN);

        ViewObject viewObject = createDefaultView(
                viewAddDTO.getPersonal(),
                false,
                viewAddDTO.getName() == null ? defaultViewObject.getType().name() : viewAddDTO.getName(),
                defaultViewObject.getType(),
                defaultViewObject.getTableName(),
                TableMapList.ENTITY_FIELDS.get(defaultViewObject.getTableName()),
                Optional.empty());

        //USER VIEW GA USER NI BERILGAN PERMISSION ORQALI SAQLAB QO'YADI
        saveUserListToUserView(viewObject, FULL, viewMemberIdList);

        //YIG'IB OLINGAN CUSTOM FIELD LARNI DB DAN OLIB KELINDI
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        Map<String, CustomField> customFieldMap = mapCustomFieldToHashMap(customFieldList);

        ViewDTO viewDTO = mapViewObjectToViewDTO(viewObject, customFieldMap);
        return ApiResult.successResponse(viewDTO);
    }

    @Override
    public void updateDefaultView(String tableName, ViewTypeEnum viewType, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap) {

        ViewObject viewObject = viewObjectRepository.findAllByPubliclyIsTrueAndDefaultViewIsTrueAndTableNameAndType(tableName, viewType).orElseThrow(() -> RestException.restThrow(tableName + " BUNDAY NAME LI VA TYPE " + viewType + " BO'LGAN  DEFAULT VIEW_OBJECT TOPILMADI", HttpStatus.BAD_REQUEST));

        List<ViewColumn> columnList = viewObject.getColumnList();

        List<ViewColumn> savedViewColumn = new ArrayList<>();
        Map<String, ViewColumn> mapViewColumn = new HashMap<>();
        for (ViewColumn viewColumn : columnList) {
            if (viewColumn.getCustomFieldId() == null) {
                mapViewColumn.put(viewColumn.getName(), viewColumn);
            }
        }
        double orderIndex = columnList.size();
        for (Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapEntry : entityMap.entrySet()) {
            if (mapViewColumn.get(stringMapEntry.getKey()) == null) {
                savedViewColumn.add(
                        new ViewColumn(
                                viewObject.getId(),
                                stringMapEntry.getKey(),
                                orderIndex++,
                                false,
                                true,
                                getCustomFieldType(stringMapEntry.getValue().get(TYPE)),
                                null,
                                true
                        ));
            }
        }
        viewColumnRepository.saveAll(savedViewColumn);
    }

    private CustomFieldTypeEnum getCustomFieldType(String customFieldName) {
        for (CustomFieldTypeEnum value : CustomFieldTypeEnum.values()) {
            if (value.name().equals(customFieldName)) {
                return value;
            }
        }
        throw RestException.restThrow(customFieldName + " BU NAME_LI CUSTOM_FIELD_TYPE TOPILMADI", HttpStatus.BAD_REQUEST);
    }

    //DEFAULT VIEW YARATIB QO'YAMIZ
    @Override
    public ViewObject createDefaultView(boolean personal, boolean isDefault, String viewName, ViewTypeEnum viewType, String tableName, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapMap, Optional<Date> timesheetDate) {

        ViewObject viewObject = new ViewObject(
                tableName,
                viewName,
                isDefault,
                true,
                !personal,
                false,
                RowSizeEnum.SMALL,
                viewType);


        // staff uchun qo'shilgan :)
        timesheetDate.ifPresent(viewObject::setDate);

        viewObjectRepository.save(viewObject);

        //SHU TABLE DAGI BARCHA MAP DAGI COLUMN LARNI SAQLAYMIZ
        List<ViewColumn> entityViewColumnList = saveEntityColumnList(viewObject.getId(), stringMapMap);

        //TODO AGAR BIRORTA VIEW COLUMN QO'SHILSA UNI DEFAULTGA HAM QO'SHIB QO'YISH LOGIKASINI YOZ
        Double lastOrderIndexEntity = entityViewColumnList.get(entityViewColumnList.size() - 1).getOrderIndex();

        //SHU TABLE UCHUN YARATILGAN BARCHA CUSTOM_FIELD LARDAN COLUMN YARATIB SAQLAYMIZ
        List<ViewColumn> customFieldViewColumnList = saveCustomFieldColumnList(viewObject.getId(), tableName, lastOrderIndexEntity);
        viewObject.setColumnList(entityViewColumnList);
        viewObject.getColumnList().addAll(customFieldViewColumnList);
        return viewObject;
    }

    //USHBU METHOD YANGI YARATILGAN CUSTOM FIELD NI DEFAULT VIEW LARGA QO'SHIB QO'YADI
    @Override
    public ViewColumnDTO addFieldToView(ViewObject viewObject, CustomField customField) {

        //VEW_COLUMN LARNI SAQLASH UCHUN LIST OCHILDI
        List<ViewColumn> viewColumnList = new ArrayList<>();

        //BERILGAN VIEW OBJECT DA GI ENG KATTA HIDDEN = FALSE BO'LGAN
        //VIEW_COLUMN NI ORDER INDEX INI QAYTARADI
        List<OrderIndexAndViewIdProjection> orderIndexAndViewIdProjectionList = viewColumnRepository.getOrderIndexDefaultOrViewId(viewObject.getId());

        //orderIndexAndViewIdProjectionList NI HASH MAP GA O'GIRIB BERADI
        HashMap<UUID, Double> orderIndexMap = mapOrderIndexAndViewIdProjectionToMap(orderIndexAndViewIdProjectionList);

        //
        Double lastOrderIndex = orderIndexMap.get(viewObject.getId());

        //BERILGAN TABLE UCHUN OCHILGAN BARCHA DEFAULT VIEW LARNI OLAMIZ
        List<ViewObject> defaultViewObjectList = viewObjectRepository.findAllByTableNameAndPubliclyIsTrueAndDefaultViewIsTrueAndIdNot(viewObject.getTableName(), viewObject.getId());

        //VIEW GA QO'SHILISHI KERAK BO'LGAN VIEW_COLUMN
        ViewColumn viewColumn = ViewColumn.builder()
                .viewObjectId(viewObject.getId())
                .name(customField.getId().toString())
                .orderIndex(++lastOrderIndex)
                .customFieldId(customField.getId())
                .type(customField.getType())
                .hidden(false)
                .build();

        //YANGI FIELD NI LIST GA QO'SHIB QO'YAMIZ
        viewColumnList.add(viewColumn);


        //BARCHA DEFAULT VIEW LARNI AYLANIB HAR BIRIGA YANGI VIEW_COLUMN NI QO'SHIB QO'YILDI
        for (ViewObject defaultView : defaultViewObjectList) {

            Double lastOrderIndexOfDefaultView = orderIndexMap.get(defaultView.getId());

            //DEFAULT VIEW GA QO'SHISH UCHUN VIEW_COLUMN
            ViewColumn viewColumnForDefaultView = ViewColumn.builder()
                    .viewObjectId(defaultView.getId())
                    .name(customField.getId().toString())
                    .orderIndex(++lastOrderIndexOfDefaultView)
                    .customFieldId(customField.getId())
                    .type(customField.getType())
                    .hidden(true).build();

            viewColumnList.add(viewColumnForDefaultView);
        }

        //BARCHA VIEW_COLUMN LAR LISTINI DB GA SAQLAB YUBORAMIZ
        viewColumnRepository.saveAll(viewColumnList);

        ViewColumnDTO viewColumnDTO = mapViewColumnToViewColumnDTOForHeaderCustomField(viewColumn, customField);
        return viewColumnDTO;
    }


    //VIEW NI ID SI ORQALI OLADI AKS HOLDA THROW
    @Override
    public ViewObject getViewObjectByIdIfNotThrow(UUID viewId) {
        return viewObjectRepository.findById(viewId).orElseThrow(() -> RestException.restThrow(ResponseMessage.VIEW_NOT_FOUND));
    }

    //GENERIC VIEW
    @Override
    public ApiResult<GenericViewResultDTO> genericView(int page, ViewDTO viewDTO, String forGroupByColumnID, List<String> idListByOtherService) {

        //VIEW NI ID SI ORQALI OLADI AKS HOLDA THROW
        ViewObject viewObject = getViewObjectByIdIfNotThrow(viewDTO.getId());

        //CUSTOM_FIELD LIST BY TABLE NAME
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(viewObject.getTableName());

        //CUSTOM_FIELD MAP
        Map<String, CustomField> customFieldToHashMap = mapCustomFieldToHashMap(customFieldList);

        viewCheckingService.checkingViewFilterAndSearchingAndSorting(viewDTO, customFieldToHashMap, viewObject.getTableName());

        // BU YERDA REDISDAGI MALUMOTLARNI SORT VA SEARCH QILIP ID_LIST NI VIEW_DTO GA SET QILIP BERADI
        otherServiceSortingAndSearchingByRedis(viewDTO, viewObject);

        // VIEW_DTO GA HAMMAVAQT ORDER INDEX BO'YICHA SORTING QILISH I KERAK(SORTINGLARNI OXIRIGA QO'SHADI)
        defaultSortingAndFilter(viewDTO, viewObject.getTableName());

        //FILTER, SORT YOKI SEARCHING BO'LGAN HUMANLARNING IDLARI LISTINI BITTA String QILIB QAYTARADIGAN QUERY YOZIBERADI
        String myQuery = queryService.mainQuery(viewDTO, TableMapList.ENTITY_FIELDS.get(viewObject.getTableName()), ColumnKey.NAME, page, idListByOtherService);

        System.err.println(myQuery);
        List<String> idList = getExecuteGenaricQueryOrElseThrow(myQuery);

        return getGenericResultDTO(idList, myQuery, viewObject.getType(), page, null, 0L);

    }


    private ApiResult<GenericViewResultDTO> getGenericResultDTO(List<String> entityIdListForGenericView, String queryForSendFronted, ViewTypeEnum viewType, int page, String statusId, long start) {

        GenericViewResultDTO result = new GenericViewResultDTO(queryForSendFronted);

        if (viewType.equals(ViewTypeEnum.TABLE)) {
            try {
                List<Map<String, Object>> mapList = new ArrayList<>(entityIdListForGenericView.size());

                if (!entityIdListForGenericView.isEmpty()) {
                    for (String s : entityIdListForGenericView) {
                        Map<String, Object> map = objectMapper.readValue(s, typeReference);
                        mapList.add(map);
                    }
                    result.setPage(page);
                    result.setGenericResult(mapList.get(0).get("idList"));
                    result.setCount(mapList.get(0).get("count"));

                } else {
                    result.setPage(page);
                    result.setGenericResult(0);
                    result.setCount(0);

                }

                long durationTime = System.currentTimeMillis() - start;
                result.setDurationTime(durationTime);
                log.info("Generic view duration in backend {}", durationTime);

                return ApiResult.successResponse(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (viewType.equals(ViewTypeEnum.BOARD) || viewType.equals(ViewTypeEnum.LIST)) {
            try {
                List<Map<String, Object>> mapList = new ArrayList<>(entityIdListForGenericView.size());

                for (String s : entityIdListForGenericView) {
                    Map<String, Object> map = objectMapper.readValue(s, typeReference);
                    mapList.add(map);
                }
//                boardStatuses.setterStatusNoDb(mapList, viewDTO);

                result.setPage(page);
                result.setStatusId(statusId);

                result.setGenericResult(mapList.get(0).get("idList"));
                result.setCount(mapList.get(0).get("count"));

                result.setGenericResult(mapList);

                long durationTime = System.currentTimeMillis() - start;
                result.setDurationTime(durationTime);
                log.info("Generic view duration in backend {}", durationTime);
                return ApiResult.successResponse(result);
//
            } catch (Exception e) {
                e.printStackTrace();

            }

            result.setGenericResult(entityIdListForGenericView);
            result.setPage(page);
            result.setStatusId(statusId);


            long durationTime = System.currentTimeMillis() - start;
            result.setDurationTime(durationTime);
            log.info("Generic view duration in backend {}", System.currentTimeMillis() - start);
            return ApiResult.successResponse(result);

        }
        return null;
    }

    private List<String> getExecuteGenaricQueryOrElseThrow(String myQuery) {

        try {
            //DBDAN HUMANLARNING IDLARINI OLIB KELYAPDI TEPADA YOZILGAN QUERY BO'YICHA
            return viewObjectRepository.getEntityIdListForGenericView(myQuery);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_GENERIC_VIEW + " QUERY => " + myQuery);
        }
    }


    @Override
    public List<String> genericInitialSumForTimeSheetEmployee(List<String> entityIdListForGenericView) {

        String query = queryServiceInitialSum.mainQueryForTimeSheetEmployee(entityIdListForGenericView, TableMapList.ENTITY_FIELDS.get(TableNameConstant.TIMESHEET_EMPLOYEE));

        System.err.println(query);

        List<String> entityIdListForGenericViewIntialSum = viewObjectRepository.getEntityIdListForGenericView(query);

        return entityIdListForGenericViewIntialSum;

    }


    // BU YERDA REDISDAGI MALUMOTLARNI SORT VA SEARCH QILIP ID_LIST NI VIEW_DTO GA SET QILIP BERADI
    @Override
    public void otherServiceSortingAndSearchingByRedis(ViewDTO viewDTO, ViewObject viewObject) {


        // EMPLOYEE UCHUN OTHER SERVICEDAN OLIB QILINADIGAN SEARCH VA SORT UN
        if (viewObject.getTableName().equals(TableNameConstant.EMPLOYEE)) {

            for (ViewSortingDTO viewSortingDTO : viewDTO.getSorting()) {
                if (viewSortingDTO.getField().equals(ColumnKey.ROLES)) {
                    List<String> result = sortAndSearchForOtherService.sortingByRole(viewSortingDTO.getDirection());
                    viewSortingDTO.setSortingIdList(result);
                }
            }

            for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : viewDTO.getViewFilter().getSearchingColumns()) {
                if (viewFilterSearchingColumnDTO.getColumnName().equals(ColumnKey.ROLES)) {
                    List<String> result = sortAndSearchForOtherService.searchingByRoleName(viewDTO.getViewFilter().getSearch());
                    viewFilterSearchingColumnDTO.setSearchedList(result);
                }
            }
        } else if (viewObject.getTableName().equals(TableNameConstant.TIMESHEET_EMPLOYEE)) {

            for (ViewSortingDTO viewSortingDTO : viewDTO.getSorting()) {
                if (viewSortingDTO.getField().equals(ColumnKey.BRANCH_ID)) {
                    List<String> result = sortAndSearchForOtherService.sortingByBranch(viewSortingDTO.getDirection());
                    viewSortingDTO.setSortingIdList(result);
                }
            }

            for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : viewDTO.getViewFilter().getSearchingColumns()) {
                if (viewFilterSearchingColumnDTO.getColumnName().equals(ColumnKey.BRANCH_ID)) {
                    List<String> result = sortAndSearchForOtherService.searchingByBranchName(viewDTO.getViewFilter().getSearch());
                    viewFilterSearchingColumnDTO.setSearchedList(result);
                }
            }

        }


    }

    //SHARING PERMISSION
    @Override
    public ApiResult<ViewMemberHierarchy> sharingPermissions(UUID viewId) {

        //VIEW_OBJECT
        ViewObject viewObject = getViewObjectByIdIfNotThrow(viewId);

        //
        ViewMemberHierarchy viewMemberHierarchy = new ViewMemberHierarchy();

        List<ViewMemberDTO> viewMemberDTOList = new ArrayList<>();
        List<ViewMemberDTO> teamMemberDTOList = new ArrayList<>();


        //BU METHOD VIEW NING PARAMETR DA BERILGAN MEMBER_LIST VA TEAM_LIST TO'LDIRADI
        addMemberListAndTeamListOfView(viewObject, viewMemberDTOList, teamMemberDTOList);

        //
        viewMemberHierarchy.setMemberList(viewMemberDTOList);
        viewMemberHierarchy.setTeamList(teamMemberDTOList);

        return ApiResult.successResponse(viewMemberHierarchy);
    }

    //BU API GA KIMLAR MUROJAAT QILAOLADI: MANAGE_VIEW HUQUQI BORLAR VA ADMINLAR
    @Override
    public ApiResult<ViewMemberDTO> changeMemberPermission(PermissionEditDTO permissionEditDTO) {

        //VIEW NI ID SI ORQALI OLADI AKS HOLDA THROW
        ViewObject viewObject = getViewObjectByIdIfNotThrow(permissionEditDTO.getViewId());

        ViewMemberDTO viewMemberDTO = new ViewMemberDTO();

        if (viewObject.isPublicly()) {
            //PUBLIC VIEW DA MEMBER NING PERMISSION NINI O'ZGARTIRIB ViewMemberDTO YASAB QAYTARADI
            changeMemberPermissionInPublicView(viewObject, viewMemberDTO, permissionEditDTO);
        } else {
            //PRIVATE VIEW DA MEMBER NING PERMISSION NINI O'ZGARTIRIB ViewMemberDTO YASAB QAYTARADI
            changeMemberPermissionInPrivateView(viewObject, viewMemberDTO, permissionEditDTO);
        }

        return ApiResult.successResponse(viewMemberDTO);
    }

    @Override
    public ApiResult<?> getViewDataByIdList(UUID viewId, List<String> idList) {

        List<Map<String, Object>> rowData = getRowData(viewId, idList);

        return ApiResult.successResponse(rowData);
    }

    @Override
    public ApiResult<Boolean> deleteViewById(UUID viewId) {

        boolean canDeleteByUser = checkingByUserHavaPermissionForDeleteView(viewId);

        if (canDeleteByUser) {
            viewObjectRepository.deleteById(viewId);
            return ApiResult.successResponse();
        }
        throw RestException.restThrow("SIZDA BU VIEW_NI O'CHIRGA HUQUQINGIZ YO'Q", HttpStatus.BAD_REQUEST);
    }

    private boolean checkingByUserHavaPermissionForDeleteView(UUID viewId) {
        UserDTO currentUser = CommonUtils.getCurrentUser();
        ViewObject viewObject = viewObjectRepository.findById(viewId).orElseThrow(() -> RestException.restThrow("BU Id LI VIEW TOPILMADI", HttpStatus.BAD_REQUEST));

        // DEFAULT VIEW NI HECH KM O'CHIRA OLMAYDI
        if (Boolean.TRUE.equals(viewObject.isDefaultView()))
            return false;
        if (Boolean.TRUE.equals(currentUser.isAdmin()))
            return true;
        if (viewObject.isPublicly()) {
            //AGAR VIEW PUBLIC BO'LSA
            // O'Z VIEW INGIZ UCHUN MOSLASHTIRIB OLINGIZLAR
            if (viewObject.getTableName().equals(TableNameConstant.EMPLOYEE)) {
                return currentUser.getPermissions().contains(FINANCE_MANAGE_VIEW_EMPLOYEE);
            } else if (viewObject.getTableName().equals(TableNameConstant.MENTOR)) {
                return currentUser.getPermissions().contains(MANAGE_MENTOR_VIEW);
            } else if (viewObject.getTableName().equals(TableNameConstant.TIMESHEET_EMPLOYEE))
                return currentUser.getPermissions().contains(FINANCE_MANAGE_VIEW_TIMESHEET);
        } else {
            //AGAR VIEW PRIVATE BO'LSA
            if (viewObject.getTableName().equals(TableNameConstant.EMPLOYEE)) {
                return currentUser.getPermissions().contains(FINANCE_MANAGE_VIEW_EMPLOYEE) && currentUser.getId().equals(viewObject.getCreatedById());
            } else if (viewObject.getTableName().equals(TableNameConstant.MENTOR)) {
                return currentUser.getPermissions().contains(MANAGE_MENTOR_VIEW) && currentUser.getId().equals(viewObject.getCreatedById());
            } else if (viewObject.getTableName().equals(TableNameConstant.TIMESHEET_EMPLOYEE))
                return currentUser.getPermissions().contains(FINANCE_MANAGE_VIEW_TIMESHEET) && currentUser.getId().equals(viewObject.getCreatedById());
        }
        return false;
    }

    public List<Map<String, Object>> getRowData(UUID viewId, List<String> idList) {

        String mainQuery = queryServiceForViewData.mainQuery(viewId, idList);

        System.err.println(mainQuery);

        //DBDAN HUMANLARNING IDLARINI OLIB KELYAPDI TEPADA YOZILGAN QUERY BO'YICHA
        List<String> entityIdListForGenericView = viewObjectRepository.getEntityForGenericViewDataRow(mainQuery);

        try {
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (String oneRow : entityIdListForGenericView) {
                Map<String, Object> map = new ObjectMapper().readValue(oneRow, new TypeReference<>() {
                });

                dataList.add(map);
            }
            return dataList;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public Map<String, CustomFieldOptionDTO> mapViewColumnTypeConfig(List<CustomFieldOptionDTO> options) {
        Map<String, CustomFieldOptionDTO> result = new HashMap<>();
        for (CustomFieldOptionDTO option : options) {
            result.put(option.getId(), option);
        }
        return result;
    }


    //VIEW NI FAVOURITE QILISH YOKI UNI FAVOURITE DAN OLIB TASHLASH
    private void addOrRemoveFavourite(ViewObject viewObject, boolean favourite) {
        UserDTO currentUser = CommonUtils.getCurrentUser();
//        ViewDTO viewDTO = new ViewDTO();
//        viewDTO.setId(viewObject.getId());
//        viewDTO.setFavourite(favourite);
        if (favourite) {
            boolean exists = favouriteViewRepository.existsByViewIdAndUserId(viewObject.getId(), currentUser.getId());
            if (exists)
                return;
            FavouriteView favouriteView = new FavouriteView(viewObject.getId(), currentUser.getId());
            favouriteViewRepository.save(favouriteView);
        } else {
            favouriteViewRepository.deleteByViewIdAndUserId(viewObject.getId(), currentUser.getId());
        }
    }

    //VIEW NI PUBLIC YOKI PRIVATE QILISH UCHUN METHOD
    private void changePubliclyView(boolean publicly, ViewObject viewObject) {
        //TIZIMDAGI USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //DEFAULT_VIEW GA HECH KIM TEGISHI MUMKIN EMAS
        if (viewObject.isDefaultView())
            throw RestException.restThrow(ResponseMessage.YOU_CAN_NOT_CHANGE_DEFAULT_VIEW);

        viewObject.setPublicly(publicly);

        //ADMIN BO'LSA QILA OLADI
        if (!currentUser.isAdmin()) {
            boolean haveManageViewPermission = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());
            //AGAR CURRENT_USER ADMIN BO'LMASA U OWNER BO'LSIN VA MANAGE VIEW HUQUQI BO'LSIN
            if (!currentUser.getId().equals(viewObject.getCreatedById()) && !haveManageViewPermission)
                throw RestException.restThrow(ResponseMessage.YOU_CAN_NOT_CHANGE_VIEW_PUBLICLY, HttpStatus.FORBIDDEN);
        }

        //AGAR PUBLIC QILSA HAMMA USER LARNI CHIQARIB TASHLA
        if (publicly) {
            //AGAR VIEW NI PUBLIC QILINSA BARCHA USER_VIEW REMOVED=TRUE QILIB QO'YILADI
            userViewRepository.updateUserViewRemovedTrue(viewObject.getId());
        } else {//AGAR PRIVATE QILSA ESKI USER LAR KO'RINIB QOLSIN
            //BARCHA ADMINLAR VA TIZIMDAGI USER VA AGAR BO'LSA VIEW NI OWNERINI PRIVATE VIEW GA QO'SHAMIZ
            Set<UUID> memberIdList = getAllViewMemberIdList(currentUser.getId(), false);
            if (viewObject.getCreatedById() != null)
                memberIdList.add(viewObject.getCreatedById());
            saveUserListToUserView(viewObject, FULL, memberIdList);
            //AGAR VIEW PRIVATE QILINSA BARCHA REMOVED=TRUE BO'LGAN USER LARNI QAYTA TIKLAB FALSE QO'YADI
            userViewRepository.updateUserViewRemovedFalse(viewObject.getId());
        }

//        viewObjectRepository.save(viewObject);

//        //todo nima qaytarish ni o'ylab ko'r
//        ViewDTO viewDTO = new ViewDTO();
//        viewDTO.setId(viewObject.getId());
//        viewDTO.setPublicly(viewObject.isPublicly());
//        return viewDTO;
    }

    //VIEW NI NOMINI O'ZGARTIRADI
    private void changeViewName(ViewObject viewObject, String name) {

        UserDTO currentUser = CommonUtils.getCurrentUser();

        //SHU USER VIEW NI NOMINI O'ZGARTIRA OLADIMI
        checkUserCanEditNameView(viewObject, currentUser);

        //YANGI NOMNI O'RNATAMIZ
        viewObject.setName(name);

//        viewObjectRepository.save(viewObject);

    }


    //QAYSI FIELD O'ZGARGANINI QAYTARADI
    private ViewChangedProperty getChangedProperty(ViewEditDTO viewEditDTO) {
        if (viewEditDTO.getViewName() != null)
            return ViewChangedProperty.NAME;
        if (viewEditDTO.getPublicly() != null)
            return ViewChangedProperty.PUBLICLY;
        if (viewEditDTO.getFavourite() != null)
            return ViewChangedProperty.FAVOURITE;
        throw RestException.restThrow(ResponseMessage.ERROR_CHANGED_PROPERTY_NOT_FOUND);
    }

    //AUTH SERVICE DAN BARCHA ADMINLAR LISTINI QAYTARADI
    //todo AUTH SERVICE DAN BARCHA ADMINLAR LISTINI QAYTARADI
    private List<UserDTO> getAllAdminListFromAuthService() {
        return new ArrayList<>();//todo yoz feign ga
    }

    //USHBU METHOD USER VIEW NI UPDATE QILA OLADIMI TEKSHIRIB BERADI
    private boolean userCanUpdateView(UserView userView, boolean viewIsPublic, boolean userIsAdmin,
                                      boolean haveManageViewInUser) {
        //USER SHU VIEW NI UPDATE QILA OLADIMI
        boolean canEdit;
        //USER DA FULL YOKI EDIT HUQUQI BORMI
        boolean haveFullOrEditPermissionInUser = userView != null && (FULL.equals(userView.getPermission()) || EDIT.equals(userView.getPermission()));
        //AGAR VIEW PUBLIC BO'LSA
        if (viewIsPublic)
            canEdit = userIsAdmin || haveManageViewInUser || haveFullOrEditPermissionInUser;
        else//AGAR VIEW PRIVATE
            canEdit = userIsAdmin || haveFullOrEditPermissionInUser;
        return canEdit;
    }

    private void defaultSortingAndFilter(ViewDTO viewDTO, String tableName) {

        sortingAndFilterAllView(viewDTO);

        switch (tableName) {
            case TableNameConstant.MENTOR:
                defaultFilterForMentorView(viewDTO);

        }
    }

    private void defaultFilterForMentorView(ViewDTO viewDTO) {

        ViewFilterDTO viewFilter = viewDTO.getViewFilter();
        if (viewFilter == null)
            viewFilter = new ViewFilterDTO();

        List<FilterFieldDTO> filterFields = viewFilter.getFilterFields();
        if (filterFields == null || filterFields.isEmpty())
            filterFields = new ArrayList<>();

        List<UserDTO> mentors = feignService.safeGetAllMentors();
        List<String> idList = mentors.stream().map(userDTO -> userDTO.getId().toString()).collect(Collectors.toList());

        FilterFieldDTO filterField = new FilterFieldDTO(
                CompareOperatorTypeEnum.EQ,
                1d,
                ColumnKey.USER_ID,
                false,
                CustomFieldTypeEnum.SHORT_TEXT,
                new FilterFieldValueDTO()
        );
        filterField.setIdListForFilter(idList);
        filterFields.add(filterField);
        viewFilter.setFilterFields(filterFields);
        viewDTO.setViewFilter(viewFilter);

    }

    private void sortingAndFilterAllView(ViewDTO viewDTO) {

        // VIEW_DTO GA HAMMAVAQT ORDER INDEX BO'YICHA SORTING QILISH I KERAK(SORTINGLARNI OXIRIGA QO'SHADI)
        List<ViewSortingDTO> sorting = viewDTO.getSorting();
        if (sorting == null || sorting.isEmpty())
            sorting = new ArrayList<>();


        ViewSortingDTO orderByOrderIndex = new ViewSortingDTO(
                ColumnKey.CREATED_AT,
                1d,
                -1,
                CustomFieldTypeEnum.DATE,
                null,
                false
        );
        sorting.add(orderByOrderIndex);
        viewDTO.setSorting(sorting);


        // VIEW_DTO GA HAMMAVAQT DELETE=FALSE NI QO'SHIB BERADI
        ViewFilterDTO viewFilter = viewDTO.getViewFilter();
        if (viewFilter == null)
            viewFilter = new ViewFilterDTO();

        List<FilterFieldDTO> filterFields = viewFilter.getFilterFields();
        if (filterFields == null || filterFields.isEmpty())
            filterFields = new ArrayList<>();

        FilterFieldDTO deleteIsFalse = new FilterFieldDTO(
                CompareOperatorTypeEnum.NOT,
                1d,
                ColumnKey.DELETED,
                false,
                CustomFieldTypeEnum.CHECKBOX,
                new FilterFieldValueDTO()
        );
        filterFields.add(deleteIsFalse);
        viewFilter.setFilterFields(filterFields);
        viewDTO.setViewFilter(viewFilter);
    }


    //VIEW GA QO'SHILISHI KERAK BO'LGAN BARCHA USER LARNI ID LARINI LISTINI QAYTARADI
    private Set<UUID> getAllViewMemberIdList(UUID userId, boolean isPublic) {
        Set<UUID> viewMemberIdList = new HashSet<>();
        viewMemberIdList.add(userId);

        //AGAR VIEW PRIVATE BO'LSA BARCHA ADMINLARNI VIEW GA FULL QILIB QO'SHIB QO'YISH KERAK
        if (!isPublic) {
            //AUTH SERVICE DAN BARCHA ADMINLAR LISTINI QAYTARADI
            List<UserDTO> adminList = getAllAdminListFromAuthService();
            for (UserDTO admin : adminList)
                viewMemberIdList.add(admin.getId());
        }
        return viewMemberIdList;
    }

    //PRIVATE VIEW DA MEMBER NING PERMISSION NINI O'ZGARTIRIB ViewMemberDTO YASAB QAYTARADI
    private void changeMemberPermissionInPrivateView(ViewObject viewObject, ViewMemberDTO
            viewMemberDTO, PermissionEditDTO permissionEditDTO) {

        //PRIVATE VIEW DA HAMMA HUQUQLARGA TEGISH MUMKIN ADMIN NIKI DAN BOSHQA

        if (permissionEditDTO.getAddMember() == null || permissionEditDTO.getAddMember()) {
            changeMemberPermissionOrAddMemberToMemberList(viewObject, viewMemberDTO, permissionEditDTO);
        } else {
            removeMemberInMemberList(viewObject, viewMemberDTO, permissionEditDTO);
        }

    }

    private void removeMemberInMemberList(ViewObject viewObject, ViewMemberDTO viewMemberDTO, PermissionEditDTO
            permissionEditDTO) {
        //TIZIMDA TURGAN USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //MEMBER NI ID ORQALI AUTH SERVICE DAN OLIB KELADI
        UserDTO member = feignService.getUserById(permissionEditDTO.getUserId());

        //USHBU METHOD USER_VIEW QAYTARADI:
        //1.AGAR PermissionEditDTO NING AddMember => null BO'LSA USER_VIEW ANIQ DB DA BO'LISHI VA
        //permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
        //2.AGAR PermissionEditDTO NING AddMember => TRUE BO'LSA DB DA BUNDAY USER_VIEW
        //BO'LMASLIGI KERAK VA permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
        //3.AGAR PermissionEditDTO NING AddMember => FALSE BO'LSA USER_VIEW ANIQ DB DA BO'LISHI KERAK AKS HOLDA THROW
        UserView memberUserView = getUserViewByUserIdAndViewIdAndCheck(permissionEditDTO, member.getId(), viewObject.getId());

        //TIZIMDAGI USER DA MANAGE_VIEW HUQUQI BORMI
        boolean haveManageViewPermissionInCurrentUser = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());

        //AGAR MEMBER GA FULL BERMOQCHI BO'LINSA MEMBERDA MANAGE_VIEW HUQUQI BO'LSIN
        boolean haveManageViewPermissionInMember = CommonUtils.havePermission(member.getPermissions(), viewObject.getTableName());

        //MEMBER DA O'ZI FINANCE_VIEW_HUMAN HUQUQI BORMI
        boolean haveViewHumanPermissionInMember = CommonUtils.havePermission(member, new PermissionEnum[]{FINANCE_VIEW_HUMAN});

        //TIZIMDAGI USER SHU VIEW NI OWNER IMI
        boolean currentUserIsOwner = currentUser.getId().equals(viewObject.getCreatedById());

        //AGAR MANAGE_VIEW HUQUQI BO'LMASA VA ADMIN BO'LMASA YOKI MEMBER ADMIN BO'LSA FORBIDDIN
        if ((!haveManageViewPermissionInCurrentUser && !currentUser.isAdmin()) || member.isAdmin())
            throw RestException.restThrow(ResponseMessage.YOU_HAVE_NOT_MANAGE_MEMBER_PERMISSION, HttpStatus.FORBIDDEN);

        //AGAR MEMBER DA HUMAN NI VIEW QILISH HUQUQI BO'LMASA BAD_REQUEST
        if (!haveViewHumanPermissionInMember)
            throw RestException.restThrow(ResponseMessage.HAVE_NOT_FINANCE_VIEW_HUMAN_PERMISSION_IN_MEMBER);


        //SHU MEMBERNI AVVALGI PERMISSIONI. AGAR BU MEMBER USER_VIEW DA BO'LMASA VIEW_ONLY AKS HOLDA USER_VIEW DAGI GA TENG
        PermissionUserThisViewEnum memberPermission = memberUserView.getPermission();

        //AGAR MEMBER NING AVVALGI HUQUQI FULL BO'LSA CURRENT USER OWNER YOKI ADMIN BO'LSIN
        //BOSHQA HAR QANDAY HUQUQLARDAGI MEMBER LARNI FULL MEMBER REMOVE QILA OLADI
        if (FULL.equals(memberPermission) && (!currentUser.isAdmin() || !currentUserIsOwner))
            throw RestException.restThrow(ResponseMessage.YOU_HAVE_NOT_CHANGE_MEMBER_PERMISSION, HttpStatus.FORBIDDEN);

        //MEMBER O'CHIRILDI
        userViewRepository.delete(memberUserView);

        //USHBU METHOD SHU USER TAYINLANISHI MUMKIN BO'LGAN BARCHA HUQUQLARNI LISTINI QAYTARADI
        List<CanReceivePermissionDTO> memberCanOtherPermissionList = getMemberCanOtherPermissionList(haveManageViewPermissionInMember);

        //MEMBER NI SHAKLLANTIRAMIZ
        viewMemberDTO.setFirstName(member.getFirstName());
        viewMemberDTO.setLastName(member.getLastName());
        viewMemberDTO.setUserId(member.getId());
        viewMemberDTO.setPhotoId(member.getPhotoId());
        viewMemberDTO.setPermission(VIEW_ONLY);//DEFAULT DA VIEW_ONLY TURSIN TEAM_LIST GA QAYTGANDA FARQI YO'Q
        viewMemberDTO.setCanOtherPermissions(memberCanOtherPermissionList);
        viewMemberDTO.setDisable(false);//REMOVE QILA OLDIMI ALBATTA YANA QAYTA EDIT QILA OLADI
    }

    private void changeMemberPermissionOrAddMemberToMemberList(ViewObject viewObject, ViewMemberDTO
            viewMemberDTO, PermissionEditDTO permissionEditDTO) {

        //TIZIMDA TURGAN USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //MEMBER NI ID ORQALI AUTH SERVICE DAN OLIB KELADI
        UserDTO member = feignService.getUserById(permissionEditDTO.getUserId());

        //USHBU METHOD USER_VIEW QAYTARADI:
        //1.AGAR PermissionEditDTO NING AddMember => null BO'LSA USER_VIEW ANIQ DB DA BO'LISHI VA
        //permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
        //2.AGAR PermissionEditDTO NING AddMember => TRUE BO'LSA DB DA BUNDAY USER_VIEW
        //BO'LMASLIGI KERAK VA permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
        //3.AGAR PermissionEditDTO NING AddMember => FALSE BO'LSA USER_VIEW ANIQ DB DA BO'LISHI KERAK AKS HOLDA THROW
        UserView memberUserView = getUserViewByUserIdAndViewIdAndCheck(permissionEditDTO, member.getId(), viewObject.getId());

        //CURRENT USER NING USER VIEW SI AGAR BUNDAY USER_VIEW BO'LMASA
        Optional<UserView> optionalCurrentUserView = userViewRepository.findByUserIdAndViewId(currentUser.getId(), viewObject.getId());

        //TIZIMDAGI USER DA MANAGE_VIEW HUQUQI BORMI
        boolean haveManageViewPermissionInCurrentUser = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());

        //AGAR MEMBER GA FULL BERMOQCHI BO'LINSA MEMBERDA MANAGE_VIEW HUQUQI BO'LSIN
        boolean haveManageViewPermissionInMember = CommonUtils.havePermission(member.getPermissions(), viewObject.getTableName());

        //MEMBER DA O'ZI FINANCE_VIEW_HUMAN HUQUQI BORMI
        boolean haveViewHumanPermissionInMember = CommonUtils.havePermission(member, new PermissionEnum[]{FINANCE_VIEW_HUMAN});

        //TIZIMDAGI USER SHU VIEW NI OWNER IMI
        boolean currentUserIsOwner = currentUser.getId().equals(viewObject.getCreatedById());

        //AGAR MANAGE_VIEW HUQUQI BO'LMASA VA ADMIN BO'LMASA YOKI MEMBER ADMIN BO'LSA FORBIDDIN
        if ((!haveManageViewPermissionInCurrentUser && !currentUser.isAdmin()) || member.isAdmin())
            throw RestException.restThrow(ResponseMessage.YOU_HAVE_NOT_MANAGE_MEMBER_PERMISSION, HttpStatus.FORBIDDEN);

        //AGAR MEMBER DA HUMAN NI VIEW QILISH HUQUQI BO'LMASA BAD_REQUEST
        if (!haveViewHumanPermissionInMember)
            throw RestException.restThrow(ResponseMessage.HAVE_NOT_FINANCE_VIEW_HUMAN_PERMISSION_IN_MEMBER);

        //MEMBER O'TISHI KERAK BO'LGAN PERMISSION
        PermissionUserThisViewEnum memberAfterPermission = permissionEditDTO.getPermission();

        //SHU MEMBERNI AVVALGI PERMISSIONI. AGAR BU MEMBER USER_VIEW DA BO'LMASA VIEW_ONLY AKS HOLDA USER_VIEW DAGI GA TENG
        PermissionUserThisViewEnum memberBeforePermission = memberUserView.getPermission();

        //AGAR MEMBER GA FULL BERMOQCHI BO'LSA NIMALAR TEKSHIRILISHI
        if (memberAfterPermission.equals(FULL)) {

            //AGAR MEMBER DA MANAGE VIEW HUQUQI BO'LMASA THROW
            if (!haveManageViewPermissionInMember)
                throw RestException.restThrow(ResponseMessage.MEMBER_HAVE_NOT_MANAGE_VIEW_PERMISSION);

            //AGAR ADMIN BO'LMASA USER_VIEW DA ANIQ BO'LISHI KERAK VA UNDA FULL HUQUQI BO'LISHI KERAK
            if (!currentUser.isAdmin() && (optionalCurrentUserView.isEmpty() || !optionalCurrentUserView.get().getPermission().equals(FULL)))
                throw RestException.restThrow(ResponseMessage.YOU_HAVE_NOT_CHANGE_MEMBER_PERMISSION, HttpStatus.FORBIDDEN);

        } else {//FULL BO'LMASA YA'NI EDIT, CHANGE_DATA, VIEW_ONLY KABILARDA BIR XIL SHART TEKSHIRILADI

            //AGAR MEMBER NING AVVALGI HUQUQI FULL BO'LSA VA UNI EDITGA TUSHIRMOQCHI BO'LSA CURRENT USER OWNER YOKI ADMIN BO'LSIN
            if (FULL.equals(memberBeforePermission) && (!currentUser.isAdmin() || !currentUserIsOwner))
                throw RestException.restThrow(ResponseMessage.YOU_HAVE_NOT_CHANGE_MEMBER_PERMISSION, HttpStatus.FORBIDDEN);
        }

        memberUserView.setPermission(permissionEditDTO.getPermission());
        memberUserView.setTableName(viewObject.getTableName());

        //USHBU METHOD SHU USER TAYINLANISHI MUMKIN BO'LGAN BARCHA HUQUQLARNI LISTINI QAYTARADI
        List<CanReceivePermissionDTO> memberCanOtherPermissionList = getMemberCanOtherPermissionList(haveManageViewPermissionInMember);

        //AGAR MEMBER NI FULL GA KO'TARILGAN BO'LSA ENDI UNGA FAQAT ADMIN YOKI VIEW OWNER UNI O'ZGARTIRA OLADI YA'NI DISABLE FALSE BO'LADI
        boolean disable = memberAfterPermission.equals(FULL) && (!currentUser.isAdmin() || !currentUserIsOwner);

        //MEMBER NI SHAKLLANTIRAMIZ
        viewMemberDTO.setFirstName(member.getFirstName());
        viewMemberDTO.setLastName(member.getLastName());
        viewMemberDTO.setUserId(member.getId());
        viewMemberDTO.setPhotoId(member.getPhotoId());
        viewMemberDTO.setPermission(memberAfterPermission);
        viewMemberDTO.setCanOtherPermissions(memberCanOtherPermissionList);
        viewMemberDTO.setDisable(disable);
    }

    //PUBLIC VIEW DA MEMBER NING PERMISSION NINI O'ZGARTIRIB ViewMemberDTO YASAB QAYTARADI
    private void changeMemberPermissionInPublicView(ViewObject viewObject, ViewMemberDTO
            viewMemberDTO, PermissionEditDTO permissionEditDTO) {

        //PUBLIC VIEW DA FULL LARGA TEGINISH MUMKIN EMAS HATTO ADMIN HAM

        //PUBLIC VIEW DA MEMBER QO'SHISH IMKONI YO'Q
        if (permissionEditDTO.getAddMember() != null)
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_ADD_OR_DELETE);

        //TIZIMDA TURGAN USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //MEMBER NI ID ORQALI AUTH SERVICE DAN OLIB KELADI
        UserDTO member = feignService.getUserById(permissionEditDTO.getUserId());

        //TIZIMDAGI USER DA MANAGE_VIEW HUQUQI BORMI
        boolean haveManageViewPermissionInCurrentUser = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());

        //MEMBER DA MANAGE VIEW HUQUQI BORMI
        boolean haveManageViewPermissionInMember = CommonUtils.havePermission(member.getPermissions(), viewObject.getTableName());

        //MEMBER O'ZI FINANCE_VIEW_HUMAN HUQUQI BORMI
        boolean haveViewHumanPermissionInMember = CommonUtils.havePermission(member, new PermissionEnum[]{FINANCE_VIEW_HUMAN});

        //AGAR MEMBER DA HUMAN NI VIEW QILISH HUQUQI BO'LMASA BAD_REQUEST
        if (!haveViewHumanPermissionInMember)
            throw RestException.restThrow(ResponseMessage.ERROR_NOT_PERMISSION_GET_HUMAN_LIST);

        //AGAR MANAGE_VIEW HUQUQI BO'LMASA VA ADMIN BO'LMASA  YOKI MEMBER DA MANAGE VIEW HUQUQI BO'LSA FORBIDDIN
        if ((!haveManageViewPermissionInCurrentUser && !currentUser.isAdmin()) || haveManageViewPermissionInMember)
            throw RestException.restThrow(ResponseMessage.ERROR_THIS_USER_HAVE_MANAGE_VIEW_PERMISSION, HttpStatus.FORBIDDEN);

        //SHU MEMBER NI USER_VIEW SI
        Optional<UserView> optionalMemberView = userViewRepository.findByUserIdAndViewId(member.getId(), viewObject.getId());

        //MEMBER O'TISHI KERAK BO'LGAN PERMISSION
        PermissionUserThisViewEnum memberAfterPermission = permissionEditDTO.getPermission();

        //SHU MEMBERNI AVVALGI PERMISSIONI. AGAR BU MEMBER USER_VIEW DA BO'LMASA VIEW_ONLY AKS HOLDA USER_VIEW DAGI GA TENG
        PermissionUserThisViewEnum memberBeforePermission = optionalMemberView.isEmpty() ? VIEW_ONLY : optionalMemberView.get().getPermission();

        //PARAMETR DA KELGAN BEFORE VA AFTER PERMISSION LAR DAN BIRORTASI FULL GA TENG BO'LSA THROW QILADI
        checkAfterAndBeforePermissionNotFull(memberBeforePermission, memberAfterPermission);

        //AGAR OPTIONAL_USER_VIEW BO'SH BO'LSA YANGISINI OCHADI YOKI BORIGA QIYMATLARNI SET QILADI
        UserView userView = optionalMemberView.orElseGet(UserView::new);

        userView.setViewId(viewObject.getId());
        userView.setView(viewObject);
        userView.setUserId(member.getId());
        userView.setPermission(memberAfterPermission);
        userView.setTableName(viewObject.getTableName());

        userViewRepository.save(userView);

        //BU MEMBER NI FULL HUQUQIDAN TASHQARI BARCHA HUQUQLARNI BERSA BO'LADI
        List<CanReceivePermissionDTO> memberCanOtherPermissionList = getMemberCanOtherPermissionList(false);

        //MEMBER NI SHAKLLANTIRAMIZ
        viewMemberDTO.setFirstName(member.getFirstName());
        viewMemberDTO.setLastName(member.getLastName());
        viewMemberDTO.setUserId(member.getId());
        viewMemberDTO.setPhotoId(member.getPhotoId());
        viewMemberDTO.setPermission(permissionEditDTO.getPermission());
        viewMemberDTO.setCanOtherPermissions(memberCanOtherPermissionList);
        viewMemberDTO.setDisable(false);//CURRENT_USER SHU MEMBERGA TEGINA OLADI
    }

    //PARAMETR DA KELGAN BEFORE VA AFTER PERMISSION LAR DAN BIRORTASI FULL GA TENG BO'LSA THROW QILADI
    private void checkAfterAndBeforePermissionNotFull(PermissionUserThisViewEnum
                                                              memberBeforePermission, PermissionUserThisViewEnum memberAfterPermission) {
        if (memberBeforePermission.equals(FULL) || memberAfterPermission.equals(FULL))
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_GIVE_FULL_PERMISSION_PUBLIC_VIEW);
    }

    //USHBU METHOD USER_VIEW QAYTARADI:
    //1.AGAR PermissionEditDTO NING AddMember => null BO'LSA USER_VIEW ANIQ DB DA BO'LISHI VA
    //permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
    //2.AGAR PermissionEditDTO NING AddMember => TRUE BO'LSA DB DA BUNDAY USER_VIEW
    //BO'LMASLIGI KERAK VA permissionEditDTO.getPermission() NULL BO'LMASLIGI KERAK AKS HOLDA THROW
    //3.AGAR PermissionEditDTO NING AddMember => FALSE BO'LSA USER_VIEW ANIQ DB DA BO'LISHI KERAK AKS HOLDA THROW
    private UserView getUserViewByUserIdAndViewIdAndCheck(PermissionEditDTO permissionEditDTO, UUID userId, UUID
            viewId) {
        Optional<UserView> optionalUserView = userViewRepository.findByUserIdAndViewId(userId, viewId);

        //SHUNCHAKI O'ZGARTIRMOQCHI BO'LSA
        if (permissionEditDTO.getAddMember() == null) {
            if (permissionEditDTO.getPermission() == null)
                throw RestException.restThrow(ResponseMessage.MEMBER_PERMISSION_REQUIRED);
            return optionalUserView.orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_USER_VIEW));

            //YANGI MEMBER QO'SHMOQCHI BO'LSA
        } else if (permissionEditDTO.getAddMember()) {
            if (optionalUserView.isPresent())
                throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_USER_VIEW);
            if (permissionEditDTO.getPermission() == null)
                throw RestException.restThrow(ResponseMessage.MEMBER_PERMISSION_REQUIRED);
            return new UserView(userId, viewId);

            //MEMBER NI O'CHIRMOQCHI BO'LSA
        } else {
            return optionalUserView.orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_USER_VIEW));
        }
    }

    //BU METHOD VIEW NING PARAMETR DA BERILGAN MEMBER_LIST VA TEAM_LIST TO'LDIRADI
    private void addMemberListAndTeamListOfView(ViewObject
                                                        viewObject, List<ViewMemberDTO> viewMemberDTOList, List<ViewMemberDTO> teamMemberDTOList) {

        //TIZIMDAGI USER
        UserDTO currentUser = CommonUtils.getCurrentUser();

        //AGAR MANAGE VIEW HUQUQI BO'LMASA XATOLIKKA TUSHADI
        boolean haveManageViewPermissionCurrentUser = CommonUtils.havePermission(currentUser.getPermissions(), viewObject.getTableName());
        if (!haveManageViewPermissionCurrentUser)
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_DONT_HAVE_PERMISSION_GET_VIEW_LIST, HttpStatus.FORBIDDEN);

        //SHU VIEW NI BARCHA USER_VIEW LARINI DB DAN OLAMIZ
        List<UserView> userViewList = userViewRepository.findAllByViewId(viewObject.getId());

        //USER_VIEW LIST NI HashMap  GA PARSE QILIB BERADI
        HashMap<UUID, UserView> userViewMap = makeUserViewListToMap(userViewList);

        //VIEW HUQUQI BOR BARCHA USERLAR
        List<UserDTO> viewMemberFromAuthService = getAllViewMemberFromAuthService(Collections.singletonList(FINANCE_VIEW_HUMAN));

        //TIZIMDAGI USER VIEW NI OWNERIMI
        boolean currentUserIsOwner = currentUser.getId().equals(viewObject.getId());

        //BU METHOD TIZIMDA TURGAN USER USHUN viewMemberDTO YASAB QAYTARADI
        ViewMemberDTO currentViewMemberDTO = getViewMemberDTOCurrentUser(currentUser);
        viewMemberDTOList.add(currentViewMemberDTO);

        //AUTH SERVICE DAN QAYTGAN BARCHA MEMBERLARNI AYLANIB TEAM VA MEMBER LARNI YIG'IB OLAMIZ
        for (UserDTO member : viewMemberFromAuthService) {

            //TIZIMDAGI USER VA MEMBER BITTA ODAM BO'LSA KEYINGI SIKL GA O'TKAZAMIZ SABABI TIZIMDAGI USER NI SIKL GA KIRMASDAN YASAB OLGANMIZ
            if (currentUser.getId().equals(member.getId()))
                continue;

            //MEMBER ADMINMI
            boolean memberIsAdmin = member.isAdmin();

            //MEMBER DA MANAGE VIEW HUQUQI BORMI
            boolean memberHaveManageViewPermission = CommonUtils.havePermission(member.getPermissions(), viewObject.getTableName());

            //MEMBER VIEW NI OWNERIMI
            boolean memberIsOwner = member.getId().equals(viewObject.getCreatedById());

            //MEMBER NING USER VIEW SI
            UserView userView = userViewMap.get(member.getId());

            //USHBU METHOD MEMBER NING VIEW DAGI HUQUQINI ANIQLAB QAYTARADI
            PermissionUserThisViewEnum memberPermissionInView = getMemberPermissionInView(userView, memberHaveManageViewPermission);

            //USHBU METHOD SHU USER TAYINLANISHI MUMKIN BO'LGAN BARCHA HUQUQLARNI LISTINI QAYTARADI
            List<CanReceivePermissionDTO> canReceivePermissionDTOList = getMemberCanOtherPermissionList(memberHaveManageViewPermission);

            //MEMBER NI SHAKLLANTIRAMIZ
            ViewMemberDTO viewMemberDTO = ViewMemberDTO.builder()
                    .firstName(member.getFirstName())
                    .lastName(member.getLastName())
                    .userId(member.getId())
                    .photoId(member.getPhotoId())
                    .permission(memberPermissionInView)
                    .canOtherPermissions(canReceivePermissionDTOList)
                    .build();

            //PUBLIC VIEW
            if (viewObject.isPublicly()) {
                //AGAR MEMBER DA ADMIN YOKI OWNER YOKI MANAGE VIEW HUQUQI BO'LSA BU USER USHBU MEMBER GA TEGINA OLMAYDI
                boolean disable = memberIsAdmin || memberHaveManageViewPermission || memberIsOwner;

                viewMemberDTO.setHaveSwitchAddOrRemoveMember(false);
                viewMemberDTO.setDisable(disable);
                viewMemberDTOList.add(viewMemberDTO);
            } else {//PRIVATE VIEW
                //PRIVATE VIEW DA OWNER GA FAQAT ADMIN TEGA OLADI
                boolean editOwnerPermission = !(memberIsOwner && currentUser.isAdmin());
                //PRIVATE VIEW DA MANAGE_VIEW HUQUQI BOR ODAMGA OWNER VA ADMIN TEGA OLADI
                boolean editFullMemberPermission = !(memberHaveManageViewPermission && (currentUserIsOwner || currentUser.isAdmin()));
                //DISABLE NI TRUE BO'LISHI UCHUN memberIsAdmin YOKI editOwnerPermission ...
                boolean disable = memberIsAdmin || editOwnerPermission || editFullMemberPermission;

                viewMemberDTO.setHaveSwitchAddOrRemoveMember(true);
                viewMemberDTO.setDisable(disable);

                //AGAR USER_VIEW DA BO'LSA MEMBER GA AKS HOLDA TEAM GA QO'SHILADI
                if (userView != null)
                    viewMemberDTOList.add(viewMemberDTO);
                else
                    teamMemberDTOList.add(viewMemberDTO);
            }
        }
    }

    //BU METHOD TIZIMDA TURGAN USER USHUN viewMemberDTO YASAB QAYTARADI
    private ViewMemberDTO getViewMemberDTOCurrentUser(UserDTO currentUser) {
        //MEMBER NI SHAKLLANTIRAMIZ
        return ViewMemberDTO.builder()
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .userId(currentUser.getId())
                .photoId(currentUser.getPhotoId())
                .permission(FULL)
                .disable(true)
                .canOtherPermissions(null)//DISABLE BO'LSA HUQUQNI TEKSHIRISHGA HOJAT YO'Q
                .build();
    }

    //USHBU METHOD SHU USER TAYINLANISHI MUMKIN BO'LGAN BARCHA HUQUQLARNI LISTINI QAYTARADI
    private List<CanReceivePermissionDTO> getMemberCanOtherPermissionList(boolean memberHaveManageViewPermission) {
        List<CanReceivePermissionDTO> canReceivePermissionDTOList = new ArrayList<>();
        for (PermissionUserThisViewEnum permissionUserThisViewEnum : PermissionUserThisViewEnum.values()) {
            CanReceivePermissionDTO canReceivePermissionDTO = new CanReceivePermissionDTO(
                    permissionUserThisViewEnum,
                    permissionUserThisViewEnum.getDescription(),
                    permissionUserThisViewEnum.equals(FULL) && !memberHaveManageViewPermission //AGAR FULL BERMOQCHI BO'LSA UNDA MEMBERDA HAM FULL BO'LISHI KERAK
            );
            canReceivePermissionDTOList.add(canReceivePermissionDTO);
        }
        return canReceivePermissionDTOList;
    }

    //USHBU METHOD MEMBER NING VIEW DAGI HUQUQINI ANIQLAB QAYTARADI
    private PermissionUserThisViewEnum getMemberPermissionInView(UserView userView,
                                                                 boolean memberHaveManageViewPermission) {
        //AGAR USER_VIEW NULL BO'LMASA O'ZINI HUQUQINI QAYTARAMIZ
        if (userView != null)
            return userView.getPermission();
        //AGAR USER_VIEW NULL BO'LSA LEKIN UNDA MANAGE_VIEW HUQUQI BO'LSA FULL QAYTRAMIZ
        if (memberHaveManageViewPermission)
            return FULL;
        //AKS HOLDA VIEW_ONLY QAYTADI
        return VIEW_ONLY;
    }


    // mock data: todo AUTH SERVICE DAN FINANCE_VIEW_HUMAN DEGAN HUQUQI BOR BO'LGAN BARCHA USERLAR LISTINI OLIB KELADI
    private List<UserDTO> getAllViewMemberFromAuthService(List<PermissionEnum> permissionEnumList) {

        return feignService.getAllViewMemberFromAuthService();


    }

    //USER_VIEW LIST NI HashMap<UUID, UserView> GA PARSE QILIB BERADI
    private HashMap<UUID, UserView> makeUserViewListToMap(List<UserView> userViewList) {
        HashMap<UUID, UserView> userViewHashMap = new HashMap<>();
        for (UserView userView : userViewList)
            userViewHashMap.put(userView.getUserId(), userView);
        return userViewHashMap;
    }


    //VIEW NI BARCHA COLUMN LARIDAN NUSXA OLIB QAYTARADI
    private List<ViewColumn> mapNewViewColumns(List<ViewColumn> columnList, ViewObject view) {
        List<ViewColumn> result = new ArrayList<>();
        for (ViewColumn viewColumn : columnList) {
            ViewColumn copyViewColumn = new ViewColumn(
                    view.getId(),
                    viewColumn.getName(),
                    viewColumn.getOrderIndex(),
                    viewColumn.isPinned(),
                    viewColumn.isHidden(),
                    viewColumn.getWidth(),
                    viewColumn.getType(),
                    viewColumn.getCustomFieldId()
            );
            copyViewColumn.setViewObject(view);

            result.add(copyViewColumn);
        }
        return result;
    }


    //BERILGAN VIEW_OBECT DAN NUSHA OLIB QAYTARADI
    private ViewObject mapNewViewObject(ViewObject viewObject) {

        return new ViewObject(
                viewObject.getTableName(),
                viewObject.getName(),
                false,
                viewObject.isAutoSave(),
                viewObject.isPublicly(),
                viewObject.isShared(),
                viewObject.getRowSize(),
                viewObject.getType());
    }

    /**
     * VIEW LAR LISTI VA VIEW TURI BERILSA LIST ICHIDAN TURI BERILGAN TURGA TEGISHLI VIEW
     * LARNI OLIB ViewTypesDTO NING ViewDTO LAR LISTIGA YIGIB ViewTypesDTO NI QAYTARADI
     *
     * @param type
     * @param viewObjectAndPermissionList
     * @return
     */
    private ViewTypesDTO mapViewTypesDTO(ViewTypeEnum
                                                 type, List<ViewObjectAndPermission> viewObjectAndPermissionList) {

        //
        if (viewObjectAndPermissionList == null || viewObjectAndPermissionList.isEmpty())
            throw RestException.restThrow(ResponseMessage.VIEW_NOT_FOUND);

        UserDTO currentUser = CommonUtils.getCurrentUser();

        //TIZIMDAGI USER ADMINMI
        boolean isAdmin = currentUser.isAdmin();

        //USER DA MANAGE_VIEW HUQUQI BORMI TEKSHIRADI
        boolean haveManageView = CommonUtils.havePermission(currentUser.getPermissions(), viewObjectAndPermissionList.get(0).getTableName());

        ViewTypesDTO viewTypesDTO = new ViewTypesDTO();

        //BERILGAN TURNI O'RNATAMIZ
        viewTypesDTO.setName(type);

        //ViewTypesDTO GA List<ViewDTO>  LARINI YIG'ISH UCHUN LIST
        List<ViewDTO> viewDTOList = new ArrayList<>();

        for (ViewObjectAndPermission viewObjectAndPermission : viewObjectAndPermissionList) {

            //VIEW_OBJECT DAN ID VA NAME DAN IBORAT ViewDTO YASAB QAYTARAMIZ
            ViewDTO viewDTO = mapViewDTOForViewType(viewObjectAndPermission);

            //DEFAULT VA TURI BERILGAN TURGA TENG VIEW NI DEFAULT VIEW GA SET QILAMIZ
            if (viewObjectAndPermission.getType().equals(type) && viewObjectAndPermission.getDefaultView()) {

                //USER NING SHU VIEW DAGI HUQUQI, EDIT, DELETE, PUBLIC
                ViewPermissionDTO viewPermissionDTO;

                //AGAR haveManageView HUQUQI BO'LSA DEFAULT VIEW NI NOMINI O'ZGARTIRA OLSIN
                if (haveManageView || isAdmin) {
                    viewPermissionDTO = ViewPermissionDTO.builder()
                            .canEditViewName(true)
                            .canShare(true)
                            .canChangePublicly(false)//DEFAULT VIEW NI PUBLICLY SINI O'ZGARTIRIB BO'LMAYDI
                            .canManageUser(true)
                            .canDeleteView(false)
                            .canResetViewDefault(true)
                            .canAutoSave(true)
                            .canExportView(true)
                            .build();
                } else { //AKS HOLDA HECH NARSANI O'ZGARTIRA OLMAYDI
                    PermissionUserThisViewEnum permission = viewObjectAndPermission.getPermission();

                    boolean canExportAndResetView = permission != null && permission.equals(EDIT);
                    boolean canAutoSave = permission != null && permission.equals(CHANGE_DATA);

                    viewPermissionDTO = ViewPermissionDTO.builder()
                            .canEditViewName(false)
                            .canShare(false)
                            .canChangePublicly(false)//DEFAULT VIEW NI PUBLICLY SINI O'ZGARTIRIB BO'LMAYDI
                            .canManageUser(false)
                            .canDeleteView(false)
                            .canResetViewDefault(canExportAndResetView)
                            .canAutoSave(canAutoSave)
                            .canExportView(canExportAndResetView)
                            .build();
                }
                viewDTO.setPermissionsUser(viewPermissionDTO);

                viewTypesDTO.setDefaultView(viewDTO);

                //TURI BERILGAN TURGA TENG VA DEFAULT BO'LMAGAN VIEW LARNI LISTGA QO'SHAMIZ
            } else if (viewObjectAndPermission.getType().equals(type) && !viewObjectAndPermission.getDefaultView()) {

                //USER NING SHU VIEW DAGI HUQUQI, EDIT, DELETE, PUBLIC
                ViewPermissionDTO viewPermissionDTO;

                viewDTOList.add(viewDTO);

                //USER IS OWNER. AGAR USER SHU VIEW NI YARATGAN USER YOKI ADMIN BO'LSA
                if (currentUser.getId().equals(viewObjectAndPermission.getCreatedById()) || isAdmin) {
                    viewPermissionDTO = ViewPermissionDTO.builder()
                            .canEditViewName(true)
                            .canShare(true)
                            .canChangePublicly(true)
                            .canManageUser(true)
                            .canDeleteView(true)
                            .canResetViewDefault(true)
                            .canAutoSave(true)
                            .canExportView(true)
                            .build();
                    viewDTO.setPermissionsUser(viewPermissionDTO);
                    continue;
                }

                //BU METHOD BARCHA DEFAULT BO'LMAGAN VIEW LARNI AYLANIB SHU USER NING SHU VIEW GA NISBATAN HUQUQINI TEKSHIRIB QAYTARADI
                viewPermissionDTO = getViewObjectPermission(isAdmin, haveManageView, viewObjectAndPermission);

                viewDTO.setPermissionsUser(viewPermissionDTO);
            }

        }

        //YIG'IB OLGAN viewDTOList NI viewTypesDTO NING Views LARIGA O'RNATAMIZ
        viewTypesDTO.setViews(viewDTOList);

        return viewTypesDTO;
    }

    //VIEW_OBJECT DAN VIEW_DTO YASAB QAYTARADI
    private ViewDTO mapViewObjectToViewDTO(ViewObject viewObject, Map<String, CustomField> customFieldMap) {

        //TIZIMDA TURGAN USER
        UserDTO userDTO = CommonUtils.getCurrentUser();

        //BO'LSA UserView AKS HOLDA NULL
        UserView userView = userViewRepository.findByUserIdAndViewId(userDTO.getId(), viewObject.getId()).orElse(null);

        //USHBU METHOD USER SHU VIEW NI KO'RISHGA HUQUQI BORMI TEKSHIRADI VA VIEW_DTO GA MAP QILIB QAYTARADI
        ViewDTO viewDTO = mapToViewObjectToViewDTO(userView, userDTO, viewObject, customFieldMap);
        return viewDTO;
    }

    //VIEW NI FAQAT VIEW_TYPES UCHUN MOSLAB YASAB BERADI
    private ViewDTO mapViewDTOForViewType(ViewObjectAndPermission viewObject) {
        //BU CONSTRUCTOR VIEW_DTO NI VIEW TYPES UCHUN BERILGANDA FAQAT ID, NAME NI SET QILADI QOLGANINI NULL QILADI
        return ViewDTO.builder()
                .id(viewObject.getId())
                .name(viewObject.getName())
                .defaultView(viewObject.getDefaultView())
                .publicly(viewObject.getPublicly())
                .favourite(viewObject.getFavourite())
                .build();
    }


    /**
     * BU METHOD BARCHA DEFAULT BO'LMAGAN VIEW LARNI AYLANIB SHU USER NING SHU VIEW GA NISBATAN HUQUQINI TEKSHIRIB QAYTARADI
     */
    private ViewPermissionDTO getViewObjectPermission(boolean isAdmin,
                                                      boolean haveManageView, ViewObjectAndPermission viewObjectAndPermission) {

        ViewPermissionDTO viewPermissionDTO;

        //BU METHOD SHU USER NI USER VIEW DAN QIDIRADI AGAR TOPSA USER VIEW DAGI HUQUQINI QAYTARADI AKS HOLDA NULL
        PermissionUserThisViewEnum permissionUser = viewObjectAndPermission.getPermission();

        PermissionUserThisViewEnum permission = viewObjectAndPermission.getPermission();
        boolean canExportAndResetView = permission != null && permission.equals(EDIT);
        boolean canAutoSave = permission != null && permission.equals(CHANGE_DATA);

        //PUBLIC. AGAR VIEW PUBLIC BO'LSA
        if (viewObjectAndPermission.getPublicly()) {

            //AGAR VIEW PUBLIC BO'LSA VA USERDA MANAGE VIEW HUQUQI BO'LSA
            if (haveManageView || isAdmin)
                viewPermissionDTO = ViewPermissionDTO.builder()
                        .canEditViewName(true)
                        .canShare(true)
                        .canChangePublicly(true)
                        .canManageUser(true)
                        .canDeleteView(true)
                        .canResetViewDefault(true)
                        .canAutoSave(true)
                        .canExportView(true)
                        .build();
            else {//AGAR haveManageView HUQUQI BO'LMASA YOKI ADMIN BO'LMASA HAMMASI FALSE

                viewPermissionDTO = ViewPermissionDTO.builder()
                        .canEditViewName(false)
                        .canShare(false)
                        .canChangePublicly(false)
                        .canManageUser(false)
                        .canDeleteView(false)
                        .canResetViewDefault(canExportAndResetView)
                        .canAutoSave(canAutoSave)
                        .canExportView(canExportAndResetView)
                        .build();
            }
            //PRIVATE. AGAR VIEW PRIVATE BO'LSA
        } else {

            //todo qara tekshir
            //USER ADMIN EMAS VA (permissionUser NULL YOKI VIEW_ONLY YOKI EDIT GA TENG BO'LSA UNI HAMMA HUQUQLARNI FALSE BERAMIZ_
            if (isAdmin) {//USER ADMIN
                viewPermissionDTO = ViewPermissionDTO.builder()
                        .canEditViewName(true)
                        .canShare(true)
                        .canChangePublicly(true)
                        .canManageUser(true)
                        .canDeleteView(true)
                        .canResetViewDefault(true)
                        .canAutoSave(true)
                        .canExportView(true)
                        .build();

            } else {//ADMIN BO'LMASA

                viewPermissionDTO = ViewPermissionDTO.builder()
                        .canEditViewName(false)
                        .canShare(false)
                        .canChangePublicly(false)
                        .canManageUser(false)
                        .canDeleteView(false)
                        .canResetViewDefault(canExportAndResetView)
                        .canAutoSave(canAutoSave)
                        .canExportView(canExportAndResetView)
                        .build();
            }
        }

        return viewPermissionDTO;
    }

    //ViewObject NI ViewFilterDTO NI YASAB BERADI, ICHIDAGI FILTER NI OLIB
    public ViewFilterDTO mapViewFilterDTO(ViewFilter viewFilter) {

        if (viewFilter == null)
            return new ViewFilterDTO();

        List<FilterFieldDTO> filterFieldDTOList = mapToFieldFilterDTO(viewFilter);
        //todo searchung column

        Set<ViewFilterSearchingColumnDTO> viewFilterSearchingColumnDTOList = mapViewFilterSearchingColumnDTOList(viewFilter.getViewFilterSearchingColumns());

        ViewFilterDTO viewFilterDTO = new ViewFilterDTO(viewFilter.getId(), viewFilter.getFilterOperator(), viewFilter.getSearch(), filterFieldDTOList);

        viewFilterDTO.setSearchingColumns(viewFilterSearchingColumnDTOList);

        return viewFilterDTO;

    }

    //FilterFieldValue NI FilterFieldValueDTO GA PARSE QILADI
    public FilterFieldValueDTO mapToFilterFieldValueDTO(ViewFilterFieldValue viewFilterFieldValue) {
        return new FilterFieldValueDTO(
                viewFilterFieldValue.getDateFilterType(),
                viewFilterFieldValue.getDateCompareOperatorType(),
                viewFilterFieldValue.getStarDate() == null ? null : viewFilterFieldValue.getStarDate().getTime(),
                viewFilterFieldValue.isStarDateTime(),
                viewFilterFieldValue.getEndDate() == null ? null : viewFilterFieldValue.getEndDate().getTime(),
                viewFilterFieldValue.isEndDateTime(),
                viewFilterFieldValue.getDateXValue(),
                viewFilterFieldValue.getMinValue(),
                viewFilterFieldValue.getMaxValue(),
                viewFilterFieldValue.getSelectedOptions(),
                viewFilterFieldValue.getSearchingValue());
    }

    //ViewFilter NI -> List<FilterFieldDTO> NI YASAB QAYTARADI
    public List<FilterFieldDTO> mapToFieldFilterDTO(ViewFilter viewFilter) {

        //AGAR NULL BO;LSA BO'SH LIST QAYTSIN
        if (viewFilter == null) return new ArrayList<>();

        List<ViewFilterField> viewFilterFieldList = viewFilter.getFields();

        List<FilterFieldDTO> filterFieldDTOList = new ArrayList<>();

        //FILTER_FIELD LARNI AYLANIB ULARDAN FilterFieldDTO LAR LISTINI YASAB QAYTARAMIZ
        for (ViewFilterField viewFilterField : viewFilterFieldList) {

            //FilterFieldValue NI FilterFieldValueDTO GA PARSE QILADI
            FilterFieldValueDTO filterFieldValueDTO = viewFilterField.getViewFilterFieldValue() == null ? null : mapToFilterFieldValueDTO(viewFilterField.getViewFilterFieldValue());

            FilterFieldDTO filterFieldDTO = new FilterFieldDTO(
                    viewFilterField.getCompareOperatorType(),
                    viewFilterField.getOrderIndex(),
                    viewFilterField.getField(),
                    Boolean.TRUE.equals(viewFilterField.getCustomField()),
                    viewFilterField.getFieldType(),
                    filterFieldValueDTO);

            filterFieldDTOList.add(filterFieldDTO);
        }

        return filterFieldDTOList;
    }

    //VIEW NI ICHIDAGI BARCHA COLUMN LARNI ViewColumnDTO GA O'GIRIB QAYTARADI
    public List<ViewColumnDTO> mapViewColumnDTOList(List<ViewColumn> viewColumnList, ViewObject
            viewObject, Map<String, CustomField> customFieldMap) {

        //RESPONSE UCHUN VIEW_COLUMN_DTO ARRAY OCHIB OLDIK
        List<ViewColumnDTO> viewColumnDTOList = new ArrayList<>();

        List<String> columnNameList = new ArrayList<>();

        //BARCHA VIEW_COLUMN LARNI AYLANIB
        if (viewColumnList != null)
            for (ViewColumn viewColumn : viewColumnList) {

                boolean check = checkingDeletedCustomField(customFieldMap, viewColumn);
                if (check) {
                    //VIEW COLUMN NI ViewColumnDTO GA PARSE QILADI
                    ViewColumnDTO viewColumnDTO = mapViewColumnToViewColumnDTOForHeader(
                            viewObject.getTableName(),
                            viewColumn,
                            false,
                            customFieldMap);
                    //ViewColumnDTO NI UMUMIY LIST GA QO'SHIB QO'YAMIZ
                    viewColumnDTOList.add(viewColumnDTO);

                    columnNameList.add(viewColumn.getName());
                }
            }

        if (!viewObject.isDefaultView()) {
            //USHBU VIEW COLUMN TEGISHLI BO'LGAN VIEW OBJECTNING DEFAULT VIEW LARI OLINYAPTI
            List<ViewColumn> defaultViewColumnList = viewColumnRepository.findAllByViewObject_DefaultViewIsTrueAndViewObject_TableNameAndViewObject_TypeAndNameNotIn(viewObject.getTableName(), viewObject.getType(), columnNameList);
            //BARCHA VIEW_COLUMNLARNI AYLANIB
            for (ViewColumn viewColumn : defaultViewColumnList) {

                //VIEW COLUMN NI ViewColumnDTO GA PARSE QILADI
                ViewColumnDTO viewColumnDTO = mapViewColumnToViewColumnDTOForHeader(
                        viewObject.getTableName(),
                        viewColumn,
                        true,
                        customFieldMap);

                //ViewColumnDTO NI UMUMIY LIST GA QO'SHIB QO'YAMIZ
                viewColumnDTOList.add(viewColumnDTO);
            }
        }

        assert viewColumnList != null;
        viewColumnList = sortingViewColumnListByPinnedAndOrderIndex(viewColumnList);

        return viewColumnDTOList;
    }

    // BU METHOD AVVAL PINNED BOYICHA KEYIN ORDER INDEX BO'YICHA SORT QILIB BERADI
    private List<ViewColumn> sortingViewColumnListByPinnedAndOrderIndex(List<ViewColumn> viewColumnList) {

        viewColumnList.sort(Comparator.comparing(ViewColumn::isPinned).reversed().thenComparing(ViewColumn::getOrderIndex).thenComparing(ViewColumn::getName));

        return viewColumnList;
    }


    // CUUSTOM_FIELD OCHIRIB YUBORILGAN BOLSA LEKIN VIEWDAN U CUSTOM FIELD OCHMAGAN BOLSA YERDA CHECK QILINADI
    //YANI OCHIB KETGAN CUSTOM FIELD LAR SHU YERDA QOLADI. VIEWGA QOSHILMAYDI OCHIB KETGAN CUSTOM FIELDLAR
    private boolean checkingDeletedCustomField(Map<String, CustomField> customFieldMap, ViewColumn viewColumn) {

        boolean isCustomField = viewColumn.getCustomFieldId() != null;

        if (isCustomField) {
            for (String customFieldId : customFieldMap.keySet()) {
                if (viewColumn.getCustomFieldId().toString().equals(customFieldId))
                    return true;
            }
            return false;
        }
        return true;
    }


    //CUSTOM_FIELD  LAR LISTINI MAP GA O'GIRIB QAYTARADI
    @Override
    public Map<String, CustomField> mapCustomFieldToHashMap(List<CustomField> customFieldList) {
        Map<String, CustomField> customFieldMap = new HashMap<>();
        for (CustomField customField : customFieldList)
            customFieldMap.put(String.valueOf(customField.getId()), customField);
        return customFieldMap;
    }

    //VIEW COLUMN NI ViewColumnDTO GA PARSE QILADI
    @Override
    public ViewColumnDTO mapViewColumnToViewColumnDTOForHeader(String tableName, ViewColumn viewColumn,
                                                               boolean fromDefaultView, Map<String, CustomField> customFieldMap) {
        //USHBU ViewColumn TURI CUSTOM FIELD MI
        boolean isCustomField = viewColumn.getCustomFieldId() != null;

        //COLUMN NOMI
        String name;
        if (isCustomField) {
            name = customFieldMap.get(viewColumn.getCustomFieldId().toString()).getName();
        } else {
            name = viewColumn.getName();
        }

        //COLUMN NI SORT SEARCH FILTER QILSA BO'LADIMI SHU BOOLEAN LARNI QAYTARADI
        SortFilterSearchableDTO sortableAndFilterableAndSearchable = getSortableAndFilterableAndSearchable(tableName, viewColumn);

        //CLIENT UCHUN ViewColumnDTO YASAB OLAMIZ
        ViewColumnDTO.ViewColumnDTOBuilder viewColumnDTOBuilder = ViewColumnDTO.builder()
                .id(isCustomField ? viewColumn.getCustomFieldId().toString() : viewColumn.getName())
                .name(name)
                .pinned(!fromDefaultView && viewColumn.isPinned())
                .hidden(isHiddenViewColumn(viewColumn, sortableAndFilterableAndSearchable, fromDefaultView))
                .orderIndex(viewColumn.getOrderIndex())
                .width(fromDefaultView ? null : viewColumn.getWidth())
                .type(viewColumn.getType())
                .enabled(checkViewColumnEnabledWithPermission(name, viewColumn))
                .customField(isCustomField)
                .sortable(sortableAndFilterableAndSearchable.isSortable())
                .searchable(sortableAndFilterableAndSearchable.isSearchable())
                .filterable(sortableAndFilterableAndSearchable.isFilterable())
                .root(sortableAndFilterableAndSearchable.isRoot())
                .showColumn(sortableAndFilterableAndSearchable.getShowColumn());

        //AGAR TUR CUSTOM FIELD BO'LSA
        if (isCustomField) {

            //customFieldList NI AYLANIB AYNAN BIZ QIDIRGAN ID LI CUSTOM FIELD BORMI TEKSHIRAMIZ
            CustomField customField = customFieldMap.get(String.valueOf(viewColumn.getCustomFieldId()));

            //AGAR OPTIONAL NI ICHI BO'SH BO'LMASA
            if (customField != null) {
                //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
                CustomFiledTypeConfigDTO customFieldTypeConfig = customFieldService.mapCustomFieldTypeConfigFromCustomField(customField);
                viewColumnDTOBuilder.typeConfig(customFieldTypeConfig);
            }
        } else {
            //AGAR ENTITY NI O'ZINI FIELD LARI BO'LSA ULAR UCHUN MAXSUS YOZILGAN JOYDAN TYPE_CONFIG NI OLADI
            CustomFiledTypeConfigDTO customFiledTypeConfigDTO = viewColumnOptionsService.mapCustomFieldTypeConfigFromEntityColumn(viewColumn, tableName);
            viewColumnDTOBuilder.typeConfig(customFiledTypeConfigDTO);
        }


        return viewColumnDTOBuilder.build();
    }

    private boolean checkViewColumnEnabledWithPermission(String name, ViewColumn viewColumn) {

        switch (name) {
            case ColumnKey.BONUS:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT
                });
            case ColumnKey.PREMIUM:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT
                });
            case ColumnKey.ADVANCE_SALARY:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT
                });
            case ColumnKey.RETENTION_SALARY:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT
                });
            case ColumnKey.ADDITION_SALARY:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT
                });
            case ColumnKey.TAX_AMOUNT:
                return CommonUtils.havePermission(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT
                });
            default:
                return viewColumn.isEnabled();
        }
    }

    private boolean isHiddenViewColumn(ViewColumn viewColumn, SortFilterSearchableDTO
            sortableAndFilterableAndSearchable, boolean fromDefaultView) {

        if (Boolean.TRUE.equals(sortableAndFilterableAndSearchable.isRoot())) {
            return false;
        }
        if (Boolean.TRUE.equals(fromDefaultView)) {
            return true;
        }
        return viewColumn.isHidden();
    }


    //COLUMN NI SORT SEARCH FILTER QILSA BO'LADIMI SHU BOOLEAN LARNI QAYTARADI
    private SortFilterSearchableDTO getSortableAndFilterableAndSearchable(String tableName, ViewColumn viewColumn) {

        SortFilterSearchableDTO.SortFilterSearchableDTOBuilder builder = SortFilterSearchableDTO.builder()
                .root(false)
                .showColumn(true);

        //AGAR COLUMN CUSTOM FIELD BO'LSA
        if (viewColumn.getCustomFieldId() != null) {

            if (
                    viewColumn.getType().equals(CustomFieldTypeEnum.DROPDOWN) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.LABELS) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.SHORT_TEXT) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.LONG_TEXT) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.CHECKBOX) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.EMAIL) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.ENUM_LABELS) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.PHONE) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.ENUM_DROPDOWN)

            ) {

                return builder.searchable(true).sortable(true).filterable(true).showColumn(true).build();

            } else if (
                    viewColumn.getType().equals(CustomFieldTypeEnum.DATE) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.DATE_TIME) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.NUMBER) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.SPECIAL_LABEL) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.RATING) ||
                            viewColumn.getType().equals(CustomFieldTypeEnum.MONEY)
            ) {

                return builder.searchable(false).sortable(true).filterable(true).showColumn(true).build();

            } else if (viewColumn.getType().equals(CustomFieldTypeEnum.FILES)) {

                return builder.searchable(false).sortable(false).filterable(true).showColumn(true).build();

            } else {
                return builder
                        .filterable(false)
                        .searchable(false)
                        .sortable(false)
                        .showColumn(true)
                        .build();
            }
        }

        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> tableMap = TableMapList.ENTITY_FIELDS.get(tableName);

        //AGAR TABLE NING MAPI TOPILMASA
        if (tableMap == null) throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_TABLE_MAP);

        //TABLE NING FIELD LARIDAN IBORAT MAP LARI
        Map<EntityColumnMapValuesKeyEnum, String> fieldMap = tableMap.get(viewColumn.getName());

        String searchableString = fieldMap.get(SEARCHABLE);
        String filterableString = fieldMap.get(FILTERABLE);
        String sortableString = fieldMap.get(SORTABLE);
        String rootColumn = fieldMap.get(ROOT);
        String showingColumn = fieldMap.get(SHOW_COLUMN);

        boolean searchable = searchableString == null || searchableString.equals(RestConstants.YES);
        boolean filterable = filterableString == null || filterableString.equals(RestConstants.YES);
        boolean sortable = sortableString == null || sortableString.equals(RestConstants.YES);
        boolean root = !(rootColumn == null || rootColumn.equals(RestConstants.NO));
        boolean showColumn = !Objects.equals(RestConstants.NO, showingColumn);


        //BARCHA BOOLEAN LARNI SET QILIB QAYTARAMIZ
        return builder
                .sortable(sortable)
                .filterable(filterable)
                .searchable(searchable)
                .root(root)
                .showColumn(showColumn)
                .build();
    }

    private ViewColumnDTO mapViewColumnToViewColumnDTOForHeaderCustomField(ViewColumn viewColumn, CustomField
            customField) {

        //CLIENT UCHUN ViewColumnDTO YASAB OLAMIZ
        ViewColumnDTO.ViewColumnDTOBuilder viewColumnDTOBuilder = ViewColumnDTO.builder()
                .id(viewColumn.getCustomFieldId().toString())
                .name(customField.getName())
                .pinned(!viewColumn.isPinned())
                .hidden(viewColumn.isHidden())
                .orderIndex(viewColumn.getOrderIndex())
                .width(viewColumn.getWidth())
                .customField(true);

        //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
        CustomFiledTypeConfigDTO customFieldTypeConfig = customFieldService.mapCustomFieldTypeConfigFromCustomField(customField);
        viewColumnDTOBuilder.typeConfig(customFieldTypeConfig);

        return viewColumnDTOBuilder.build();
    }

    //VIEW NI VIEW DTO GA PARSE QILADI
    public ViewDTO mapToViewObjectToViewDTO(UserView userView, UserDTO userDTO, ViewObject
            viewObject, Map<String, CustomField> customFieldMap) {


        //ViewObject NI ViewFilterDTO NI YASAB BERADI, ICHIDAGI FILTER NI OLIB
        ViewFilterDTO viewFilterDTO = mapViewFilterDTO(viewObject.getViewFilter());

        //VIEW NI ICHIDAGI BARCHA COLUMN LARNI ViewColumnDTO GA O'GIRIB QAYTARADI
        List<ViewColumnDTO> viewColumnDTOList = mapViewColumnDTOList(viewObject.getColumnList(), viewObject, customFieldMap);

        viewColumnDTOList = checkingViewColumnList(viewColumnDTOList, userView, userDTO, viewObject);

        //List<ViewSorting> NI -> List<ViewSortingDTO> GA PARSE QILADI
        List<ViewSortingDTO> viewSortingDTOList = mapViewSortingToDTOList(viewObject.getSortingList());

        boolean favourite = favouriteViewRepository.existsByViewIdAndUserId(viewObject.getId(), userDTO.getId());


        ViewDTO viewDTO = ViewDTO.builder()
                .id(viewObject.getId())
                .name(viewObject.getName())
                .autoSave(viewObject.isAutoSave())
                .defaultView(viewObject.isDefaultView())
                .type(viewObject.getType())
                .columns(viewColumnDTOList)
                .sorting(viewSortingDTOList)
                .publicly(viewObject.isPublicly())
                .rowSize(viewObject.getRowSize())
                .viewFilter(viewFilterDTO)
                .favourite(favourite)
                .build();

        // BU USERDA VIEW NI MANAGE QILISH HUQUQI BORMI?BU HUQUQ BORLIGINI HAR BITTA SERVICE OZI TTEKSHIRIB BERADI
        // MASALAN HAMAN SERVICE USERDA HUMAN_VIEW_MANAGE HUQUQINI BORLIGINI TEKSHIRADI
        boolean haveManageView = CommonUtils.havePermission(userDTO.getPermissions(), viewObject.getTableName());

        // VIEW OBJECTGA PERMISSIONLARNI QOSHIB BERIB YUBORAMZ.
        // AGAR VIEW STANDART_VIEW BOLSA(DEFAULT) QANDAY PERMISSIONLARNI BERISHNI
        // AGAR VIEW PUBLIC_VEW BOLSA QANDAY PERMISSIONLARNI BERISHNI KELISHIB SHU YERDA PERMISSIONS GA ADD QILISH KK
        setViewObjectPermission(viewObject, viewDTO, userView, haveManageView, userDTO.isAdmin());

        return viewDTO;
    }

    /**
     * USERNI PERMISSIONIGA QARAB UNGA COLUMNLARNI QAYTARADI <br>
     * 1.0 USER ADMIN BOLSA BARCHA COLUMNLAR QAYTADI <br>
     * 2.0 VIEW PUBLIC <br>
     * 2.1  USERDA SHU PAGE GA MANAGE HUQUQI BO'LSA  BARCHA COLUMNLAR QAYTADI <br>
     * 2.2  USERDA FULL YOKI EDIT HUQUQI BO'LSA  BARCHA COLUMNLAR QAYTADI <br>
     * 2.3  USERDA CHANGE_DATA YOKI VIEW_ONLY YOKI HECH QANDAY HUQUQI BOLMASA HIDDEN=FALSE COLUMNLARNI QAYTARAMZ <br>
     * 3.0 VIEW PRIVATE BOLSA <br>
     * 3.1 USERDA FULL YOKI EDIT HUQUQI BO'LSA  BARCHA COLUMNLAR QAYTADI <br>
     * 3.2 USERDA CHANGE_DATA YOKI VIEW_ONLY HIDDEN=FALSE COLUMNLARNI QAYTARAMZ <br>
     * 3.3 USERNI BU VIEWNI HUQUQI BO'LMASA THROW GA OTAMZ <br>
     **/
    private List<ViewColumnDTO> checkingViewColumnList(List<ViewColumnDTO> viewColumnDTOList, UserView
            userView, UserDTO userDTO, ViewObject viewObject) {

        if (userDTO.isAdmin()) {
            return viewColumnDTOList;
        }
        Set<Map.Entry<String, Map<String, Map<EntityColumnMapValuesKeyEnum, String>>>> entries = TableMapList.ENTITY_FIELDS.entrySet();
        if (entries.stream().noneMatch(stringMapEntry -> Objects.equals(stringMapEntry.getKey(), viewObject.getTableName()))) {
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_NOT_FOUND_THIS_NAME);
        }

        if (viewObject.isPublicly()) {
            if (userDTO.getPermissions().contains(PermissionEnum.FINANCE_MANAGE_VIEW_EMPLOYEE.name())) {
                return viewColumnDTOList;
            } else {
                if (userView != null) {
                    if (userView.getPermission().equals(EDIT) || userView.getPermission().equals(FULL)) {
                        return viewColumnDTOList;
                    }
                }
                return viewColumnDTOList.stream().filter(viewColumnDTO -> !viewColumnDTO.isHidden()).collect(Collectors.toList());
            }
        } else {
            //PRIVATE  VIEW BO'LSA
            if (userView != null) {
                if (userView.getPermission().equals(EDIT) || userView.getPermission().equals(FULL)) {
                    return viewColumnDTOList;
                }
                return viewColumnDTOList.stream().filter(viewColumnDTO -> !viewColumnDTO.isHidden()).collect(Collectors.toList());
            } else {
                throw RestException.restThrow(ResponseMessage.ERROR_YOU_CANNOT_GET_VIEW_NOT_PERMISSION, HttpStatus.BAD_REQUEST);
            }
        }
    }

    private List<ViewColumn> returnHiddenTrueViewColumnList(List<ViewColumn> viewColumnList) {
        List<ViewColumn> result = new ArrayList<>();
        for (ViewColumn viewColumn : viewColumnList) {
            if (!viewColumn.isHidden())
                result.add(viewColumn);
        }
        return result;
    }


    // VIEW OBJECTGA PERMISSIONLARNI QOSHIB BERIB YUBORAMZ.
    // AGAR VIEW STANDART_VIEW BOLSA(DEFAULT) QANDAY PERMISSIONLARNI BERISHNI
    // AGAR VIEW PUBLIC_VEW BOLSA QANDAY PERMISSIONLARNI BERISHNI KELISHIB SHU YERDA PERMISSIONS GA ADD QILISH KK
    private void setViewObjectPermission(ViewObject viewObject, ViewDTO viewDTO, UserView userView,
                                         boolean haveManageView, boolean admin) {

        //AGAR VIEW PUBLIC BO'LSA BU USER NI USERVIEW DAN TEKSHIRAMIZ BO'LSA O
        if (!viewObject.isPublicly() && userView == null && !admin)
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_CANNOT_GET_VIEW, HttpStatus.FORBIDDEN);

        //USER NI SHU VIEW DAGI HUQUQLARINI QAYTARADI
        ViewPermissionDTO viewPermission = getUserPermissionThisView(viewObject, userView, haveManageView, admin);

        viewDTO.setPermissionsUser(viewPermission);
    }


    //USER NI SHU VIEW DAGI HUQUQLARINI QAYTARADI
    private ViewPermissionDTO getUserPermissionThisView(ViewObject viewObject, UserView userView, boolean haveManageView, boolean admin) {

        //AGAR VIEW PUBLIC BO'LSA BU USER NI USERVIEW DAN TEKSHIRAMIZ BO'LSA O
        if (!viewObject.isPublicly() && userView == null && !admin)
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_CANNOT_GET_VIEW, HttpStatus.FORBIDDEN);

        ViewPermissionDTO viewPermission;
        //VIEW_DTO GA setPermissionUserThisViewEnum NI USER VIEW DAN OLIB BIRIKTIRADI. AGAR USER_VIEW DA EDIT YOKI
        //FULL HUQUQI BO'LSA FIELD, CUSTOM FIELD LARNI QO'SHISH VA O'ZGARTIRISH HUQUQLARINI BERADI
        if (viewObject.isPublicly())
            viewPermission = setFieldPermissionToViewDTOForPublicView(userView, haveManageView, admin);
        else
            viewPermission = setFieldPermissionToViewDTOForPrivate(userView, admin);

        return viewPermission;
    }


    //VIEW_DTO GA setPermissionUserThisViewEnum NI USER VIEW DAN OLIB BIRIKTIRADI. AGAR USER_VIEW DA EDIT YOKI
    //FULL HUQUQI BO'LSA FIELD, CUSTOM FIELD LARNI QO'SHISH VA O'ZGARTIRISH HUQUQLARINI BERADI
    public ViewPermissionDTO setFieldPermissionToViewDTOForPrivate(UserView userView, boolean admin) {

        ViewPermissionDTO.ViewPermissionDTOBuilder viewPermissionDTOBuilder = ViewPermissionDTO.builder()
                .canManageColumn(true)
                .canSearch(true)
                .canFilter(true);

        if (!admin) {
            if (userView == null || userView.getPermission().equals(VIEW_ONLY))
                return viewPermissionDTOBuilder.build();

            if (userView.getPermission().equals(CHANGE_DATA))
                return viewPermissionDTOBuilder
                        .canEnterData(true)
                        .canSearch(true)
                        .canFilter(true)
                        .canUpdateView(true)
                        .canShowColumn(true)
                        .build();
        }

        return viewPermissionDTOBuilder
                .canEnterData(true)
                .canShowColumn(true)
                .canManageCustomField(true)
                .canUpdateView(true)
                .build();

    }


    //VIEW_DTO GA setPermissionUserThisViewEnum NI USER VIEW DAN OLIB BIRIKTIRADI. AGAR USER_VIEW DA EDIT YOKI
    //FULL HUQUQI BO'LSA FIELD, CUSTOM FIELD LARNI QO'SHISH VA O'ZGARTIRISH HUQUQLARINI BERADI
    public ViewPermissionDTO setFieldPermissionToViewDTOForPublicView(UserView userView, boolean haveManageView,
                                                                      boolean admin) {

        ViewPermissionDTO.ViewPermissionDTOBuilder viewPermissionDTOBuilder = ViewPermissionDTO.builder()
                .canManageColumn(true)
                .canSearch(true)
                .canUpdateView(false)
                .canFilter(true)
                .canEnterData(true)
                .canCreateView(true)
                .canManageCustomField(true);

        //AGAR ADMIN BO'LMASA VA MANAGE HUQUQI BO'LMASA IF GA TUSHADI AKS HOLDA PAST GA TUSHIB KETADI
        if (!admin && !haveManageView) {
            if ((userView == null || userView.getPermission().equals(VIEW_ONLY)))
                return viewPermissionDTOBuilder
                        .build();

            if (userView.getPermission().equals(CHANGE_DATA))
                return viewPermissionDTOBuilder
                        .canEnterData(true)
                        .build();
        }

        return viewPermissionDTOBuilder
                .canEnterData(true)
                .canShowColumn(true)
                .canManageCustomField(true)
                .canUpdateView(true)
                .build();
    }


    //BARCHA SHU TABLE UCHUN YARATILGAN CUSTOM FIELD LAR DAN COLUMN YARATIB SAQLAB QO'YADI
    private List<ViewColumn> saveCustomFieldColumnList(UUID viewObjectId, String tableName, Double
            lastOrderIndexEntity) {

        List<ViewColumn> viewColumnList = new ArrayList<>();

        //SHU TABLE NAME GA ALOQADOR BARCHA CUSTOM FIELD LARNI ORDER_INDEX BO'YICHA OLIB KELADI
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(tableName);

        for (CustomField customField : customFieldList) {
            ViewColumn viewColumn = new ViewColumn(
                    viewObjectId,
                    customField.getName(),
                    lastOrderIndexEntity,
                    false,
                    false,
                    customField.getType(),
                    customField.getId(),
                    true//CUSTOM FIELD LARGA TEGISH MUMKIN DOIM
            );

            viewColumnList.add(viewColumn);
        }

        return viewColumnRepository.saveAll(viewColumnList);
    }

    //LEAD NI COLUMN LARINI YARATIB QAYTARADI
    private List<ViewColumn> saveEntityColumnList(UUID viewObjectId, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapMap) {

        List<ViewColumn> viewColumnList = new ArrayList<>();

        Double orderIndex = 1d;

        for (Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapEntry : stringMapMap.entrySet()) {
            String name = stringMapEntry.getKey();
            Map<EntityColumnMapValuesKeyEnum, String> linkedMap = stringMapEntry.getValue();

            CustomFieldTypeEnum type = CustomFieldTypeEnum.valueOf(linkedMap.get(TYPE));

            //SHU COLUMN GA TEGINIB BO'LADIMI
            boolean enabled = RestConstants.YES.equals(linkedMap.get(ENABLED)) || linkedMap.get(ENABLED) == null;

            ViewColumn viewColumn = new ViewColumn(
                    viewObjectId,
                    name,
                    orderIndex,
                    false,
                    false,
                    type,
                    null,
                    enabled
            );
            viewColumnList.add(viewColumn);
            orderIndex++;
        }

        viewColumnRepository.saveAll(viewColumnList);
        return viewColumnList;
    }

    //
    public ViewObject saveViewObject(ViewDTO viewDTO, ViewObject
            viewObject, Map<String, CustomField> customFieldMap) {

        viewObject.setAutoSave(Boolean.TRUE.equals(viewDTO.getAutoSave()));

        viewObject.setRowSize(viewDTO.getRowSize());

//        List<ViewColumn> viewColumns = mapViewColumnListFromViewDTO(viewObject, viewDTO, customFieldMap);
        List<ViewColumn> viewColumns = mapViewColumnListFromViewDTO2(viewObject, viewDTO, customFieldMap);

        viewColumnRepository.saveAll(viewColumns);

        viewObject.setColumnList(viewColumns);

        return viewObjectRepository.save(viewObject);
    }

    public List<ViewColumn> mapViewColumnListFromViewDTO2(ViewObject viewObject, ViewDTO
            viewDTO, Map<String, CustomField> customFieldMap) {

        List<ViewColumnDTO> viewColumnDTOList = viewDTO.getColumns();
        Map<String, ViewColumn> viewColumnMap = mapViewColumnMapToViewColumn(viewObject.getColumnList());
        List<ViewColumn> viewColumnList = new ArrayList<>();
        List<ViewColumn> hiddenViewColumnList = new ArrayList<>();

        double size = viewDTO.getColumns().size();
        double i = 1;

        for (ViewColumnDTO viewColumnDTO : viewColumnDTOList) {
            ViewColumn viewColumn = viewColumnMap.get(viewColumnDTO.getId());

            if (viewColumn != null) {
                viewColumn.setEnabled(viewColumnDTO.isEnabled());
                viewColumn.setHidden(viewColumnDTO.isHidden());
                viewColumn.setWidth(viewColumnDTO.getWidth());
                viewColumn.setPinned(viewColumnDTO.isPinned());
            } else {
                //AGAR YANGI VIEW_COLUMN BO'LSA;
                viewColumn = ViewColumn.builder()
                        .viewObjectId(viewObject.getId())
                        .name(viewColumnDTO.getId())
                        .pinned(viewColumnDTO.isPinned())
                        .hidden(viewColumnDTO.isHidden())
                        .width(viewColumnDTO.getWidth())
                        .type(viewColumnDTO.getType())
                        .customFieldId(viewColumnDTO.isCustomField() ? UUID.fromString(viewColumnDTO.getId()) : null)
                        .build();

            }

            if (viewColumnDTO.isHidden()) {
                viewColumn.setOrderIndex(++size);
                hiddenViewColumnList.add(viewColumn);
            } else {
                viewColumn.setOrderIndex(i++);
                viewColumnList.add(viewColumn);
            }
        }
        viewColumnList.addAll(hiddenViewColumnList);
        return viewColumnList;
    }

    private Map<String, ViewColumn> mapViewColumnMapToViewColumn(List<ViewColumn> viewColumnList) {
        Map<String, ViewColumn> result = new LinkedHashMap<>();
        for (ViewColumn viewColumn : viewColumnList) {
            result.put(viewColumn.getCustomFieldId() != null ? viewColumn.getCustomFieldId().toString() : viewColumn.getName(), viewColumn);
        }
        return result;
    }

    public List<ViewColumn> mapViewColumnListFromViewDTO(ViewObject viewObject, ViewDTO
            viewDTO, Map<String, CustomField> customFieldMap) {

        if (viewObject.getId() != null)
            viewColumnRepository.deleteAllByViewObjectId(viewObject.getId());


        List<ViewColumn> hiddenViewColumnList = new ArrayList<>();

        List<ViewColumn> viewColumnList = new ArrayList<>();


        int size = viewDTO.getColumns().size();
        int i = 1;

        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityFields = TableMapList.ENTITY_FIELDS.get(viewObject.getTableName());

        //todo BUILDDER QIL UKAM
        if (!viewDTO.getColumns().isEmpty()) {
            for (ViewColumnDTO viewColumnDTO : viewDTO.getColumns()) {
                CustomField customField = null;

                Map<EntityColumnMapValuesKeyEnum, String> entityColumnMapValuesKeyEnumStringMap = entityFields.get(viewColumnDTO.getId());
                if (entityColumnMapValuesKeyEnumStringMap == null) {
                    customField = customFieldMap.get(viewColumnDTO.getId());
                    if (customField == null)
                        throw RestException.restThrow("Bunday column yo'q: " + viewColumnDTO.getId(), HttpStatus.BAD_REQUEST);
                }

                ViewColumn viewColumn = ViewColumn.builder()
                        .viewObjectId(viewObject.getId())
                        .name(customField != null ? customField.getName() : viewColumnDTO.getId())
                        .pinned(viewColumnDTO.isPinned())
                        .hidden(viewColumnDTO.isHidden())
                        .width(viewColumnDTO.getWidth())
                        .type(viewColumnDTO.getType())
                        .customFieldId(customField != null ? customField.getId() : null)
                        .build();

                if (viewColumnDTO.isHidden()) {
                    viewColumn.setOrderIndex((double) ++size);
                    hiddenViewColumnList.add(viewColumn);
                } else {
                    viewColumn.setOrderIndex((double) i++);
                    viewColumnList.add(viewColumn);
                }
            }
            viewColumnList.addAll(hiddenViewColumnList);
        }
        return viewColumnList;
    }

    public ViewFilter mapViewFilter(ViewObject viewObject, ViewDTO viewDTO) {

        //DB GA SAQLASH UCHUN YANGI VIEW_FILTER OCHILYAPTI
        ViewFilter viewFilter = new ViewFilter();

        if (viewObject.getViewFilter() != null) {
            viewFilter = viewObject.getViewFilter();
        }

        //OPERATOR NI SET QILAMIZ AND YOKI OR
        viewFilter.setFilterOperator(viewDTO.getViewFilter().getFilterOperator());

        if (viewFilter.getSearch() == null || viewFilter.getSearch().isEmpty() || viewFilter.getSearch().isBlank()) {

            viewFilter.setSearch(null);
        } else {
            viewFilter.setSearch(viewDTO.getViewFilter().getSearch());
        }

        //VIEW_FILTER GA TEGISHLI BO'LGAN FILTER_FIELD LARNI SAQLAYAPMIZ
        viewFilter = viewFilterRepository.save(viewFilter);

        List<ViewFilterSearchingColumn> viewFilterSearchingColumns = updateViewFilterSearchingColumn(viewDTO.getViewFilter().getSearchingColumns(), viewFilter);

        viewFilter.setViewFilterSearchingColumns(viewFilterSearchingColumns);

//        mapToFilterFieldList(viewFilter, viewDTO);
//
        updateViewFieldList(viewFilter, viewDTO);

        return viewFilter;
    }

    //GGGGG
    private void updateViewFieldList(ViewFilter viewFilter, ViewDTO viewDTO) {
        List<ViewFilterField> viewFilterFieldList = viewFilter.getFields();
        List<FilterFieldDTO> newViewFilterFieldList = viewDTO.getViewFilter().getFilterFields();

        List<ViewFilterField> savedViewFilterField = new ArrayList<>();
        List<ViewFilterFieldValue> savedViewFilterFieldValue = new ArrayList<>();
        List<UUID> removeViewFilterFieldIdList = new ArrayList<>();

        boolean find;
        double orderIndex = 1;
        for (FilterFieldDTO filterFieldDTO : newViewFilterFieldList) {
            find = false;
            for (ViewFilterField viewFilterField : viewFilterFieldList) {
                if (filterFieldDTO.getField().equals(viewFilterField.getField())) {
                    find = true;
                    updateViewFilterFieldAndAddList(viewFilterField, filterFieldDTO, savedViewFilterFieldValue, savedViewFilterField);
                    viewFilterFieldList.remove(viewFilterField);
                    viewFilterField.setOrderIndex(orderIndex++);
                    viewFilterFieldRepository.save(viewFilterField);
                    break;
                }
            }
            if (!find) {
                ViewFilterField viewFilterField = new ViewFilterField(
                        viewFilter.getId(),
                        filterFieldDTO.getCompareOperatorType(),
                        orderIndex++,
                        filterFieldDTO.getField(),
                        filterFieldDTO.getFieldType(),
                        filterFieldDTO.isCustomField()
                );
                viewFilterField = viewFilterFieldRepository.save(viewFilterField);

                if (filterFieldDTO.getValue() != null) {
                    ViewFilterFieldValue viewFilterFieldValue = mapFilterFieldValue(viewFilterField, filterFieldDTO.getValue());

                    viewFilterField.setViewFilterFieldValue(viewFilterFieldValue);
                    savedViewFilterFieldValue.add(viewFilterFieldValue);
                }
                savedViewFilterField.add(viewFilterField);
            }
        }
        for (ViewFilterField viewFilterField : viewFilterFieldList) {
            removeViewFilterFieldIdList.add(viewFilterField.getId());
        }

        //TODO  YETIMCHA BO'LIB QOLGAN VIEW_FILTER_FIELD_VALUE LARNI LISTINI OCHIB TASHLAYDIGAN ANNATASIYA QOYISH KERAK
        if (!removeViewFilterFieldIdList.isEmpty()) {
            viewFilterFieldValueRepository.deleteAllByViewFilterFieldIdIn(removeViewFilterFieldIdList);
            viewFilterFieldRepository.deleteAllById(removeViewFilterFieldIdList);
        }


        viewFilterFieldValueRepository.saveAll(savedViewFilterFieldValue);

        viewFilter.setFields(savedViewFilterField);
    }

    private void updateViewFilterFieldAndAddList(ViewFilterField viewFilterField, FilterFieldDTO
            filterFieldDTO, List<ViewFilterFieldValue> savedViewFilterFieldValue, List<ViewFilterField> savedViewFilterField) {
        viewFilterField.setCompareOperatorType(filterFieldDTO.getCompareOperatorType());
        ViewFilterFieldValue viewFilterFieldValue = viewFilterField.getViewFilterFieldValue();

        ViewFilterFieldValue newViewFilterFieldValue;
        if (viewFilterFieldValue != null) {
            newViewFilterFieldValue = mapFilterFieldValueUpdate(viewFilterFieldValue, viewFilterField, filterFieldDTO.getValue());
        } else {
            newViewFilterFieldValue = mapFilterFieldValue(viewFilterField, filterFieldDTO.getValue());
        }
        viewFilterField.setViewFilterFieldValue(newViewFilterFieldValue);

        savedViewFilterFieldValue.add(newViewFilterFieldValue);
        savedViewFilterField.add(viewFilterField);
    }


    // BU METHOD DB YOQ BOLGAN YANGI COLUMNLARNI SAQLAB, KERAKSIZ COLUMNLARNI O'CHIRADI
    private List<ViewFilterSearchingColumn> updateViewFilterSearchingColumn
    (Set<ViewFilterSearchingColumnDTO> searchingColumns, ViewFilter viewFilter) {

        List<ViewFilterSearchingColumn> viewFilterSearchingColumnList = viewFilterSearchingColumnRepository.findAllByViewFilterId(viewFilter.getId());
        List<UUID> deletedViewSearchingColumnIdList = new ArrayList<>();

        Map<String, ViewFilterSearchingColumn> viewFilterSearchingColumnMapInDB = makeViewSearchingColumnMap(viewFilterSearchingColumnList);
        List<ViewFilterSearchingColumn> notSavedViewFilterSearchingColumnDTOList = new ArrayList<>();

        List<ViewFilterSearchingColumn> result = new ArrayList<>();

        //SEARCHING BO'SH KELSA SEARCHING COLUMN LARNI DB DAN TOZALAYMIZ
        if (viewFilter.getSearch() == null || viewFilter.getSearch().isEmpty() || viewFilter.getSearch().isBlank()) {
            viewFilterSearchingColumnRepository.findAllByViewFilterId(viewFilter.getId());
            return null;
        }

        for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : searchingColumns) {

            ViewFilterSearchingColumn viewFilterSearchingColumn = viewFilterSearchingColumnMapInDB.get(viewFilterSearchingColumnDTO.getColumnName());
            if (viewFilterSearchingColumn == null) {
                notSavedViewFilterSearchingColumnDTOList.add(mapViewFilterSearchingColumn(viewFilterSearchingColumnDTO, viewFilter));
            } else {
                result.add(viewFilterSearchingColumnMapInDB.get(viewFilterSearchingColumnDTO.getColumnName()));
                viewFilterSearchingColumnMapInDB.remove(viewFilterSearchingColumnDTO.getColumnName());
            }
        }
        for (Map.Entry<String, ViewFilterSearchingColumn> filterSearchingColumnEntry : viewFilterSearchingColumnMapInDB.entrySet()) {
            deletedViewSearchingColumnIdList.add(filterSearchingColumnEntry.getValue().getId());
        }

        viewFilterSearchingColumnRepository.deleteAllById(deletedViewSearchingColumnIdList);
        viewFilterSearchingColumnRepository.saveAll(notSavedViewFilterSearchingColumnDTOList);

        result.addAll(notSavedViewFilterSearchingColumnDTOList);
        return result;
    }

    private ViewFilterSearchingColumn mapViewFilterSearchingColumn(ViewFilterSearchingColumnDTO
                                                                           viewFilterSearchingColumnDTO, ViewFilter viewFilter) {
        return new ViewFilterSearchingColumn(
                viewFilterSearchingColumnDTO.getColumnType(),
                viewFilterSearchingColumnDTO.getColumnName(),
                viewFilter,
                viewFilterSearchingColumnDTO.isCustomField()
        );
    }

    private Map<String, ViewFilterSearchingColumn> makeViewSearchingColumnMap
            (List<ViewFilterSearchingColumn> viewFilterSearchingColumnList) {
        Map<String, ViewFilterSearchingColumn> result = new HashMap<>();
        for (ViewFilterSearchingColumn viewFilterSearchingColumn : viewFilterSearchingColumnList)
            result.put(viewFilterSearchingColumn.getColumnName(), viewFilterSearchingColumn);
        return result;
    }

    //Set<ViewFilterSearchingColumnDTO> LARNI ViewFilter GA SET QILIB BERADI
    public List<ViewFilterSearchingColumn> mapViewFilterSearchingColumnList
    (Set<ViewFilterSearchingColumnDTO> searchingColumnDTOList, ViewFilter viewFilter) {

        List<ViewFilterSearchingColumn> result = new ArrayList<>();

        for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : searchingColumnDTOList) {
            ViewFilterSearchingColumn viewFilterSearchingColumn = new ViewFilterSearchingColumn();
            viewFilterSearchingColumn.setColumnName(viewFilterSearchingColumnDTO.getColumnName());
            viewFilterSearchingColumn.setColumnType(viewFilterSearchingColumnDTO.getColumnType());
            viewFilterSearchingColumn.setViewFilter(viewFilter);
            viewFilterSearchingColumn.setCustomField(viewFilterSearchingColumnDTO.isCustomField());
            result.add(viewFilterSearchingColumn);
        }
        return result;
    }

    //todo yoz e ahil bo'laylik
    public Set<ViewFilterSearchingColumnDTO> mapViewFilterSearchingColumnDTOList
    (List<ViewFilterSearchingColumn> searchingColumnList) {

        Set<ViewFilterSearchingColumnDTO> result = new HashSet<>();
        if (searchingColumnList != null && !searchingColumnList.isEmpty()) {
            for (ViewFilterSearchingColumn viewFilterSearchingColumn : searchingColumnList) {
                ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO = new ViewFilterSearchingColumnDTO();
                viewFilterSearchingColumnDTO.setColumnName(viewFilterSearchingColumn.getColumnName());
                viewFilterSearchingColumnDTO.setColumnType(viewFilterSearchingColumn.getColumnType());
                viewFilterSearchingColumnDTO.setCustomField(Boolean.TRUE.equals(viewFilterSearchingColumn.getCustomField()));
                result.add(viewFilterSearchingColumnDTO);
            }
        }
        return result;
    }


//    //FILTER FIELD LARNI SAQLAYAPMIZ
//    public void mapToFilterFieldList(ViewFilter viewFilter, ViewDTO viewDTO) {
//
//        //USHBU VIEW FILTERGA TEGISHLI BARCHA FILTER FIELDLARNI O'CHIRAMIZ QACHONKI VIEW_FILTER DA ID BO'LSA
//        if (viewFilter.getId() != null) {
//            viewFilterFieldValueRepository.deleteAllByViewFilterFieldIdIn(viewFilter.getFields().stream().map(AbsUUIDUserAuditEntity::getId).collect(Collectors.toList()));
//            viewFilterFieldRepository.deleteAllByViewFilterId(viewFilter.getId());
//        }
//
//        List<FilterFieldDTO> fieldDTOList = viewDTO.getViewFilter().getFilterFields();
//
//        List<ViewFilterField> viewFilterFields = new ArrayList<>();
//
//        double orderIndex = 0;
//        List<ViewFilterFieldValue> viewFilterFieldValueList = new ArrayList<>();
//        if (!fieldDTOList.isEmpty()) {
//
//            for (FilterFieldDTO filterFieldDTO : fieldDTOList) {
//                ViewFilterField viewFilterField = new ViewFilterField(
//                        viewFilter.getId(),
//                        filterFieldDTO.getCompareOperatorType(),
//                        filterFieldDTO.getOrderIndex(),
//                        filterFieldDTO.getField(),
//                        filterFieldDTO.getFieldType(),
//                        filterFieldDTO.isCustomField()
//                );
//
//                viewFilterField.setOrderIndex(orderIndex++);
//
//                viewFilterField = viewFilterFieldRepository.save(viewFilterField);
//
//
//                if (filterFieldDTO.getValue() != null) {
//
//                    //FILTERLAR UCHUN VALUELARNI SET QILISH
//                    ViewFilterFieldValue viewFilterFieldValue = mapFilterFieldValue(viewFilterField, filterFieldDTO.getValue());
//
//                    viewFilterFieldValueList.add(viewFilterFieldValue);
//                    viewFilterField.setViewFilterFieldValue(viewFilterFieldValue);
//                }
//
//                viewFilterFields.add(viewFilterField);
//
//            }
//        }
//        viewFilterFieldValueRepository.saveAll(viewFilterFieldValueList);
//        viewFilter.setFields(viewFilterFields);
//    }

    //FILTERLAR UCHUN VALUELARNI SET QILISH
    public ViewFilterFieldValue mapFilterFieldValue(ViewFilterField viewFilterField, FilterFieldValueDTO
            filterFieldValueDTO) {

        ViewFilterFieldValue viewFilterFieldValue = new ViewFilterFieldValue();
        viewFilterFieldValue.setViewFilterFieldId(viewFilterField.getId());
        viewFilterFieldValue.setViewFilterField(viewFilterField);
//        viewFilterFieldValue.getViewFilterField().setViewFilterId(viewFilterField.getViewFilterId());
        //DATE BILAN FILTER QILINYAPTI
        if (filterFieldValueDTO != null) {
            if (filterFieldValueDTO.getDateCompareOperatorType() != null) {
                viewFilterFieldValue.setDateFilterType(filterFieldValueDTO.getDateFilterType());
                viewFilterFieldValue.setDateCompareOperatorType(filterFieldValueDTO.getDateCompareOperatorType());
                if (filterFieldValueDTO.getStarDate() != null)
                    viewFilterFieldValue.setStarDate(new Timestamp(filterFieldValueDTO.getStarDate()));
                viewFilterFieldValue.setStarDateTime(filterFieldValueDTO.isStarDateTime());
                if (filterFieldValueDTO.getEndDate() != null)
                    viewFilterFieldValue.setEndDate(new Timestamp(filterFieldValueDTO.getEndDate()));
                viewFilterFieldValue.setEndDateTime(filterFieldValueDTO.isEndDateTime());
                viewFilterFieldValue.setDateXValue(filterFieldValueDTO.getDateXValue());

            } else if (filterFieldValueDTO.getMinValue() != null) {

                viewFilterFieldValue.setMinValue(filterFieldValueDTO.getMinValue());
                viewFilterFieldValue.setMaxValue(filterFieldValueDTO.getMaxValue());

            } else if (filterFieldValueDTO.getOptionsSelected() != null) {
                viewFilterFieldValue.setSelectedOptions(filterFieldValueDTO.getOptionsSelected());
            } else if (filterFieldValueDTO.getSearchingValue() != null) {
                viewFilterFieldValue.setSearchingValue(filterFieldValueDTO.getSearchingValue());

            } else throw RestException.restThrow("Yo'q narsani berma", HttpStatus.BAD_REQUEST);

            return viewFilterFieldValue;
        }
        return viewFilterFieldValue;
    }

    //FILTERLAR UCHUN VALUELARNI SET QILISH
    public ViewFilterFieldValue mapFilterFieldValueUpdate(ViewFilterFieldValue
                                                                  viewFilterFieldValue, ViewFilterField viewFilterField, FilterFieldValueDTO filterFieldValueDTO) {

        viewFilterFieldValue.setViewFilterFieldId(viewFilterField.getId());
        viewFilterFieldValue.setViewFilterField(viewFilterField);

        //DATE BILAN FILTER QILINYAPTI
        if (filterFieldValueDTO.getDateCompareOperatorType() != null) {
            viewFilterFieldValue.setDateFilterType(filterFieldValueDTO.getDateFilterType());
            viewFilterFieldValue.setDateCompareOperatorType(filterFieldValueDTO.getDateCompareOperatorType());
            if (filterFieldValueDTO.getStarDate() != null)
                viewFilterFieldValue.setStarDate(new Timestamp(filterFieldValueDTO.getStarDate()));
            viewFilterFieldValue.setStarDateTime(filterFieldValueDTO.isStarDateTime());
            if (filterFieldValueDTO.getEndDate() != null)
                viewFilterFieldValue.setEndDate(new Timestamp(filterFieldValueDTO.getEndDate()));
            viewFilterFieldValue.setEndDateTime(filterFieldValueDTO.isEndDateTime());
            viewFilterFieldValue.setDateXValue(filterFieldValueDTO.getDateXValue());

        } else if (filterFieldValueDTO.getMinValue() != null) {

            viewFilterFieldValue.setMinValue(filterFieldValueDTO.getMinValue());
            viewFilterFieldValue.setMaxValue(filterFieldValueDTO.getMaxValue());

        } else if (filterFieldValueDTO.getOptionsSelected() != null) {
            viewFilterFieldValue.setSelectedOptions(filterFieldValueDTO.getOptionsSelected());
        } else if (filterFieldValueDTO.getSearchingValue() != null) {
            viewFilterFieldValue.setSearchingValue(filterFieldValueDTO.getSearchingValue());

        } else throw RestException.restThrow("Yo'q narsani berma", HttpStatus.BAD_REQUEST);

        return viewFilterFieldValue;
    }

    private HashMap<UUID, Double> mapOrderIndexAndViewIdProjectionToMap
            (List<OrderIndexAndViewIdProjection> orderIndexAndViewIdProjectionList) {
        HashMap<UUID, Double> viewIdAndOrderIndexMap = new HashMap<>();
        for (OrderIndexAndViewIdProjection orderIndexAndViewIdProjection : orderIndexAndViewIdProjectionList)
            viewIdAndOrderIndexMap.put(orderIndexAndViewIdProjection.getViewId(), orderIndexAndViewIdProjection.getOrderIndex());
        return viewIdAndOrderIndexMap;
    }

    //USER VIEW GA USER NI BERILGAN PERMISSION ORQALI SAQLAB QO'YADI
    private void saveUserListToUserView(ViewObject viewObject,
                                        PermissionUserThisViewEnum permission,
                                        Set<UUID> userList) {
        Set<UserView> userViewList = new HashSet<>();

        userViewRepository.updateUserViewRemovedTrueWithoutAdmin(viewObject.getId(), userList);

        List<UUID> adminIdListFromUserViewByViewId = userViewRepository.getUserIdByViewId(viewObject.getId());

        adminIdListFromUserViewByViewId.forEach(userList::remove);

        for (UUID userId : userList) {
            UserView userView = UserView.builder()
                    .viewId(viewObject.getId())
                    .view(viewObject)
                    .permission(permission)
                    .userId(userId)
                    .removed(false)
                    .build();
            userViewList.add(userView);
        }

        userViewRepository.saveAll(userViewList);
    }

    //USER SHU VIEW DAN NUSHA OLOLADIMI TEKSHIRADI AKS HOLDA THROW
    private void checkUserCanDuplicateView(ViewObject viewObject, UserDTO userDTO) {

        //AGAR PRIVATE VIEW BO'LSA
        if (!viewObject.isPublicly()) {
            if (userDTO.isAdmin() || userDTO.getId().equals(viewObject.getCreatedById())) return;
            UserView userView = userViewRepository.findByUserIdAndViewId(userDTO.getId(), viewObject.getId()).orElseThrow(
                    () -> RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN));
            if (!userView.getPermission().equals(FULL))
                throw RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN);

            //PUBLIC
        } else {
            boolean havePermission = CommonUtils.havePermission(userDTO.getPermissions(), viewObject.getTableName());
            if (!havePermission)
                throw RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN);
        }
    }

    //SHU USER VIEW NI NOMINI O'ZGARTIRA OLADIMI
    private void checkUserCanEditNameView(ViewObject viewObject, UserDTO userDTO) {
        if (viewObject.isPublicly()) {
            boolean havePermission = CommonUtils.havePermission(userDTO.getPermissions(), viewObject.getTableName());
            if (userDTO.isAdmin() || userDTO.getId().equals(viewObject.getId()) || havePermission) return;
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN);
        } else {
            UserView userView = userViewRepository.findByUserIdAndViewId(userDTO.getId(), viewObject.getId()).orElseThrow(
                    () -> RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN));
            if (userView.getPermission().equals(FULL)) return;
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW, HttpStatus.FORBIDDEN);
        }
    }


    //=============VIEW SORTING NI MAPQILADIGAN METHODLAR=========>>>>>>>>>

    public ViewSortingDTO mapViewSortingToDTO(ViewSorting viewSorting) {
        if (viewSorting == null) return null;

        ViewSortingDTO viewSortingDTO = new ViewSortingDTO();

        viewSortingDTO.setField(viewSorting.getField());
        viewSortingDTO.setOrderIndex(viewSorting.getOrderIndex());
        viewSortingDTO.setDirection(viewSorting.getDirection());
        viewSortingDTO.setFieldType(viewSorting.getFieldType());
        viewSortingDTO.setCustomField(Boolean.TRUE.equals(viewSorting.getCustomField()));

        return viewSortingDTO;
    }

    public List<ViewSortingDTO> mapViewSortingToDTOList(List<ViewSorting> viewSortingList) {
        if (viewSortingList == null)
            return null;

        return viewSortingList.stream()
                .map(this::mapViewSortingToDTO)
                .sorted((o1, o2) -> o1.getOrderIndex().compareTo(o2.getOrderIndex()))
                .collect(Collectors.toList());

    }
}
