package ai.ecma.appstaff.service.customField;

import ai.ecma.appstaff.entity.customField.*;
 
import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.OptionActionDTO;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.customField.*;
import ai.ecma.appstaff.payload.view.ViewColumnDTO;
import ai.ecma.appstaff.repository.customField.*;
import ai.ecma.appstaff.repository.view.ViewColumnRepository;
import ai.ecma.appstaff.service.view.ViewColumnOptionsService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomFieldServiceImpl implements CustomFieldService {
    private final CustomFieldRepository customFieldRepository;
    private final CustomFieldDropDownRepository customFieldDropDownRepository;
    private final CustomFieldLabelRepository customFieldLabelRepository;
    private final CustomFieldRatingRepository customFieldRatingRepository;
    private final CustomFieldValueRepository customFieldValueRepository;
    private final ViewService viewService;
    private final ViewColumnRepository viewColumnRepository;
    private final ViewColumnOptionsService viewColumnOptionsService;

    public CustomFieldServiceImpl(CustomFieldRepository customFieldRepository,
                                  CustomFieldDropDownRepository customFieldDropDownRepository,
                                  CustomFieldLabelRepository customFieldLabelRepository,
                                  CustomFieldRatingRepository customFieldRatingRepository,
                                  CustomFieldValueRepository customFieldValueRepository,
                                  @Lazy ViewService viewService, ViewColumnRepository viewColumnRepository, ViewColumnOptionsService viewColumnOptionsService) {
        this.customFieldRepository = customFieldRepository;
        this.customFieldDropDownRepository = customFieldDropDownRepository;
        this.customFieldLabelRepository = customFieldLabelRepository;
        this.customFieldRatingRepository = customFieldRatingRepository;
        this.customFieldValueRepository = customFieldValueRepository;
        this.viewService = viewService;
        this.viewColumnRepository = viewColumnRepository;
        this.viewColumnOptionsService = viewColumnOptionsService;
    }

    //ADD CUSTOM FIELD
    @Transactional
    @Override
    public ApiResult<ViewColumnDTO> addCustomField(CustomFieldAddDTO customFieldAddDTO) {
        // log.info("_________class CustomFieldServiceImpl => addCustomField: {}", customFieldAddDTO);
        //MIJOZDAN OLINGAN VIEW ID ORQALI BIZ VIEW_OBJECTNI OLYAPMIZ
        //VIEW_OBJECT ORQALI BIZ QO'SHILAYOTGAN CUSTOM FIELD QAYSI TABLE GA TEGISHLI EKANLIGINI BILISH UCHUN
        ViewObject viewObject = viewService.getViewObjectByIdIfNotThrow(customFieldAddDTO.getViewId());

        //CUSTOM FIELD NI NAME VA TABLE_NAME MAVJUDLIGINI TEKSHIRAMIZ.
        //AGAR QO'SHILAYOTGAN CUSTOM FIELD NAME AVVAL USHBU TABLE DA MAVJUD BO'LSA THROW QILAMIZ
        checkExistCustomFieldNameAndTableName(customFieldAddDTO.getName(), viewObject.getTableName());

        //CustomFieldAddDTO ni -> CustomField GA PARSE QILIB BERADI
        CustomField customField = mapCustomFieldAddDTOToCustomField(customFieldAddDTO);

        //TABLE NAME GA LEAD ENTITY NOMI NI SET QILAMIZ
        customField.setTableName(viewObject.getTableName());

        customFieldRepository.save(customField);


        CustomFieldDTO customFieldDTO = new CustomFieldDTO();

        //CustomField NI -> CustomFieldDTO GA PARSE QILIB BERADI
        mapCustomFielDTOCustomFieldDTO(customField, customFieldDTO);

        CustomFieldTypeEnum customFieldType = customFieldAddDTO.getType();
        // log.info("_________class CustomFieldServiceImpl => addCustomField =>  customFieldAddDTO.getType(): {}", customFieldType);

        //AGAR CUSTOM FIELD TURI DROPDOWN BO'LSA UNI OPTION LARINI SAQLAB QO'YAMIZ
        if (CustomFieldTypeEnum.DROPDOWN.equals(customFieldType)) {
            saveCustomFieldDropDown(customField, customFieldAddDTO, customFieldDTO);

            //AGAR CUSTOM FIELD TURI LABELS BO'LSA UNI OPTION LARINI SAQLAB QO'YAMIZ
        } else if (CustomFieldTypeEnum.LABELS.equals(customFieldType)) {
            saveCustomFieldLabels(customField, customFieldAddDTO);

            //AGAR CUSTOM FIELD TURI RATING BO'LSA RATING NI SAQLAB QO'YAMIZ
        } else if (CustomFieldTypeEnum.RATING.equals(customFieldType)) {
            saveCustomFieldRating(customField, customFieldAddDTO);
        }

        //USHBU METHOD YANGI YARATILGAN CUSTOM FIELD NI DEFAULT VIEW LARGA QO'SHIB QO'YADI
        ViewColumnDTO viewColumnDTO = viewService.addFieldToView(viewObject, customField);
        // log.info("_________class CustomFieldServiceImpl => addCustomField =>  viewColumnDTO: {}", viewColumnDTO);

        return ApiResult.successResponse(viewColumnDTO);
    }


    //TABLE NAME VA OWNER ID BERILSA SHU TABLE NAME VA OWNER ID LI CUSTOM FIELD DTO NI VALUE BILAN BIRGA QAYTARADI
    @Override
    public List<CustomFieldDTO> getCustomFieldByTableNameAndOwnerId(String tableName, String ownerId) {
        // log.info("_________class CustomFieldServiceImpl => getCustomFieldByTableNameAndOwnerId =>  tableName: {}, ownerId: {}", tableName, ownerId);

        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(tableName);

        //AGAR BIRORTA CUSTOM FIELD BO'LMASA BO'SH LIST QAYTADI
        if (customFieldList.isEmpty())
            return new ArrayList<>();

        //CUSTOM FIELD ID LARINI YIG'IB OLDIK
        List<UUID> customFieldIdList = customFieldList.stream().map(CustomField::getId).collect(Collectors.toList());

        //List<CustomField> NI -> List<CustomFieldDTO> GA PARSE QILADI
        List<CustomFieldDTO> customFieldDTOList = mapCustomFieldListToDTOList(customFieldList);

        //CUSTOM FIELD LARNING QIYMATLARINI QAYTARADI
        List<CustomFieldValue> customFieldValueList = customFieldValueRepository.findAllByOwnerAndCustomFieldIds(ownerId, customFieldIdList);

        //CUSTOM_FIELD_ID AND  CUSTOM_FIELD_VALUE
        Map<UUID, CustomFieldValue> customFieldValueMap = makeHashMapCustomFieldIdAndValue(customFieldValueList);

        for (CustomFieldDTO customFieldDTO : customFieldDTOList) {

            //VALUE LAR  MAPINI ICHIDAN BIZDA TEGISHLI VALUE NI OLAMIZ
            CustomFieldValue customFieldValue = customFieldValueMap.get(customFieldDTO.getId());

            //AGAR TUR LABELS BO'LSA
            if (CustomFieldTypeEnum.LABELS.equals(customFieldDTO.getType())) {

                //AGAR CUSTOM_FIELD_VALUE NULL BO'LMASA
                if (customFieldValue != null) {
                    String[] selectedLabels = getSelectedLabels(customFieldValue.getValue());
                    customFieldDTO.setValue(selectedLabels);
                }
                continue;
            }

            if (customFieldValue != null) {
                customFieldDTO.setValue(customFieldValue.getValue());
            }
        }
        // log.info("_________class CustomFieldServiceImpl => getCustomFieldByTableNameAndOwnerId =>  customFieldDTOList: {}", customFieldDTOList);

        return customFieldDTOList;

    }

    @Override
    public ApiResult<Boolean> deleteCustomField(UUID customFieldId) {
        // log.info("_________class CustomFieldServiceImpl => deleteCustomField =>  customFieldId: {}", customFieldId);

        UserDTO currentUser = CommonUtils.getCurrentUser();

        boolean havePermission = CommonUtils.havePermission(currentUser, new PermissionEnum[]{PermissionEnum.FINANCE_MANAGE_FINANCE_VIEW_HUMAN});

        if (!havePermission) throw RestException.restThrow(ResponseMessage.ERROR_HAVE_NOT_PERMISSION);

        CustomField customField = customFieldRepository.findById(customFieldId).orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_NOT_FOUND));

        // log.info("_________class CustomFieldServiceImpl => deleteCustomField =>  customField: {}", customField);
        customFieldRepository.delete(customField);

        return ApiResult.successResponse();
    }


    @Override
    public ApiResult<ViewColumnDTO> editCustomField(CustomFieldEditDTO customFieldEditDTO) {
        // log.info("_________class CustomFieldServiceImpl => editCustomField =>  customFieldEditDTO: {}", customFieldEditDTO);

        CustomField customField = customFieldRepository.findById(customFieldEditDTO.getId()).orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_NOT_FOUND, HttpStatus.BAD_REQUEST));

        customField.setName(customFieldEditDTO.getName());

        CustomFieldTypeEnum customFieldType = customField.getType();
        // log.info("_________class CustomFieldServiceImpl => editCustomField =>  customFieldType: {}", customFieldType);


        if (CustomFieldTypeEnum.LABELS.equals(customFieldType)) {
            editCustomFieldLabel(customField, customFieldEditDTO);
        } else if (CustomFieldTypeEnum.DROPDOWN.equals(customFieldType)) {
            editCustomFieldDropDown(customField, customFieldEditDTO);
        } else if (CustomFieldTypeEnum.RATING.equals(customFieldType)) {
            editCustomFieldRating(customField, customFieldEditDTO);
        }
        customFieldRepository.save(customField);

        ViewColumn viewColumn = viewColumnRepository.findByViewObjectIdAndCustomFieldId(customFieldEditDTO.getViewId(), customField.getId()).orElseThrow(() -> RestException.restThrow(ResponseMessage.VIEW_COLUMN_NOT_FOUND));
        // log.info("_________class CustomFieldServiceImpl => editCustomField =>  viewColumn: {}", viewColumn);

        Map<String, CustomField> customFieldMap = new HashMap<>();
        customFieldMap.put(customField.getId().toString(), customField);

        ViewColumnDTO viewColumnDTO = viewService.mapViewColumnToViewColumnDTOForHeader(viewColumn.getViewObject().getTableName(), viewColumn, false, customFieldMap);
        // log.info("_________class CustomFieldServiceImpl => editCustomField =>  viewColumnDTO: {}", viewColumnDTO);

        return ApiResult.successResponse(viewColumnDTO);
    }

    @Override
    //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
    public CustomFiledTypeConfigDTO mapCustomFieldTypeConfigFromCustomField(CustomField customField) {
        // log.info("_________class CustomFieldServiceImpl => mapCustomFieldTypeConfigFromCustomField =>  customField: {}", customField);

        CustomFiledTypeConfigDTO typeConfigDTO = new CustomFiledTypeConfigDTO();

        CustomFieldTypeEnum customFieldType = customField.getType();
        // log.info("_________class CustomFieldServiceImpl => mapCustomFieldTypeConfigFromCustomField =>  viewColumn: {}", customFieldType);

        //DROPDOWN BO'LSA
        if (CustomFieldTypeEnum.DROPDOWN.equals(customFieldType)) {

            List<CustomFieldDropDown> dropDownList = customField.getDropDowns();
            List<CustomFieldOptionDTO> customFieldOptionDTOList = this.mapDropdownListOptionDTOList(dropDownList, customField.getId().toString());
            typeConfigDTO.setOptions(customFieldOptionDTOList);

            OptionActionDTO action = new OptionActionDTO(CommonUtils.urlBuilderForViewColumnEdit(), true, true, true);
            typeConfigDTO.setAction(action);

            //LABELS BO'LSA
        } else if (CustomFieldTypeEnum.LABELS.equals(customFieldType)) {

            List<CustomFieldLabel> labels = customField.getLabels();
            List<CustomFieldOptionDTO> customFieldOptionDTOList = mapLabelListOptionDTOList(labels, customField.getId().toString());
            typeConfigDTO.setOptions(customFieldOptionDTOList);

            OptionActionDTO action = new OptionActionDTO(CommonUtils.urlBuilderForViewColumnEdit(), true, true, true);
            typeConfigDTO.setAction(action);
            //RATING BO'LSA
        } else if (CustomFieldTypeEnum.RATING.equals(customFieldType)) {

            CustomFieldRating customFieldRating = customField.getCustomFieldRating();

            //RATING UCHUN CONFIG YASAB QAYTARADI
            RatingConfigDTO ratingConfigDTO = mapRatingToConfigDTO(customFieldRating);
            typeConfigDTO.setRatingConfig(ratingConfigDTO);
        } else if (CustomFieldTypeEnum.CALL.equals(customFieldType)) {

            //TURI CALL BO'LSA SHU CALL NING TYPE CONFIGINI QAYTARADI
            CallConfigDTO callConfigDTO = mapCallConfig(customField.getTableName());
            typeConfigDTO.setCallConfig(callConfigDTO);

            //SPECIAL_LABEL BO'LSA
        } else if (CustomFieldTypeEnum.SPECIAL_LABEL.equals(customFieldType)) {

            //TURI SPECIAL_LABEL BO'LSA SHU SPECIAL_LABEL NING TYPE CONFIGINI QAYTARADI
            SpecialLabelConfigDTO specialLabelConfig = mapSuperLabelConfig(customField.getTableName());
            typeConfigDTO.setSpecialLabelConfig(specialLabelConfig);

            //CALL_DURATION BO'LSA
        } else if (CustomFieldTypeEnum.CALL_DURATION.equals(customFieldType)) {

            //TURI CALL_DURATION BO'LSA SHU CALL_DURATION NING TYPE CONFIGINI QAYTARADI
            CallDurationConfigDTO callDurationConfig = mapCallDurationConfig(customField.getTableName());
            typeConfigDTO.setCallDurationConfig(callDurationConfig);

        } else if (CustomFieldTypeEnum.FILES.equals(customFieldType)) {

            //TUR FILE BO'LSA SHAPKA UCHUN URL BERAMIZ
            AttachmentConfigDTO attachmentConfigDTO = mapAttachmentConfigDTO();
            typeConfigDTO.setAttachmentConfig(attachmentConfigDTO);

        } else if (CustomFieldTypeEnum.MESSAGE.equals(customFieldType)) {
            // TODO: 05/02/22 YOZISH KERAK YANA
            //MESSAGE UCHUN CONFIG YASAB QAYTARADI
            MessageConfigDTO messageConfigDTO = mapMessageConfigDTO();
            typeConfigDTO.setMessageConfig(messageConfigDTO);

        } else if (CustomFieldTypeEnum.CALL_TYPE.equals(customFieldType)) {

            //CALL TYPE UCHUN OPTION LARNI QAYTARADI
            List<CallTypeOptionDTO> callTypeOptionDTOList = mapCallTypeOptions();
            typeConfigDTO.setCallTypeOptions(callTypeOptionDTOList);

        } else if (CustomFieldTypeEnum.CALL_STATUS.equals(customFieldType)) {

            //CALL STATUS LAR UCHUN OPTION LARNI QAYTARADI
            List<CallStatusOptionDTO> callStatusOptionDTOList = mapCallStatusOptions();
            typeConfigDTO.setCallStatusOptions(callStatusOptionDTOList);
        }

        // log.info("_________class CustomFieldServiceImpl => mapCustomFieldTypeConfigFromCustomField =>  typeConfigDTO: {}", typeConfigDTO);

        return typeConfigDTO;
    }


    private void editCustomFieldRating(CustomField customField, CustomFieldEditDTO customFieldEditDTO) {
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldRating =>  customField: {}, customFieldEditDTO: {}", customField, customFieldEditDTO);

        CustomFieldRating customFieldRating = customField.getCustomFieldRating();

        //CUSTOM FIELD TYPE CONFIG NI NULL EMASLIGINI TEKSHIRADI
        checkTypeConfigNotNull(customFieldEditDTO.getTypeConfig());

        //CUSTOM FIELD NING RATING I BO'SH EMASMI TEKSHIRADI
        checkRatingNotNull(customFieldEditDTO.getTypeConfig().getRatingConfig());

        //RATING DTO SI
        RatingConfigDTO ratingConfigDTO = customFieldEditDTO.getTypeConfig().getRatingConfig();

        customFieldRating.setCount(ratingConfigDTO.getCount());
        customFieldRating.setCodePoint(ratingConfigDTO.getCodePoint());

        customFieldRatingRepository.save(customFieldRating);
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldRating =>  customFieldRating: {}", customFieldRating);
    }

    private void editCustomFieldDropDown(CustomField customField, CustomFieldEditDTO customFieldEditDTO) {
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldDropDown =>  customField: {}, customFieldEditDTO: {}", customField, customFieldEditDTO);

        List<CustomFieldDropDown> dropDowns = customField.getDropDowns();
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldLabel =>  dropDowns: {}", dropDowns);

        //CUSTOM FIELD TYPE CONFIG VA OPTION LAR NULL BO'LSA THROW
        checkCustomFieldTypeOrOptionIsNull(customFieldEditDTO);
        //
        List<CustomFieldOptionDTO> options = customFieldEditDTO.getTypeConfig().getOptions();
        List<UUID> hasDropDownIdList = new ArrayList<>();

        //
        List<CustomFieldDropDown> customFieldDropDowns = new ArrayList<>();

        for (CustomFieldOptionDTO option : options) {
            for (CustomFieldDropDown dropDown : dropDowns) {

                //AGAR OPTION DA ID BO'LMASA DEMAK YANGISI YARATILYAPTI
                if (option.getId() == null) {
                    CustomFieldDropDown customFieldDropDown = new CustomFieldDropDown(
                            option.getName(),
                            option.getColorCode(),
                            customField
                    );
                    customFieldDropDowns.add(customFieldDropDown);

                } else if (dropDown.getId().toString().equals(option.getId())) {
                    hasDropDownIdList.add(dropDown.getId());
                    dropDown.setColorCode(option.getColorCode());
                    dropDown.setName(option.getName());

                    customFieldDropDowns.add(dropDown);
                }
            }
        }
        customFieldDropDownRepository.deleteAllByCustomFieldIdAndIdNotIn(customField.getId(), hasDropDownIdList);
        customFieldDropDownRepository.saveAll(customFieldDropDowns);
    }

    private void editCustomFieldLabel(CustomField customField, CustomFieldEditDTO customFieldEditDTO) {
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldLabel =>  customField: {}, customFieldEditDTO: {}", customField, customFieldEditDTO);

        List<CustomFieldLabel> labels = customField.getLabels();
        // log.info("_________class CustomFieldServiceImpl => editCustomFieldLabel =>  labels: {}", labels);

        //CUSTOM FIELD TYPE CONFIG VA OPTION LAR NULL BO'LSA THROW
        checkCustomFieldTypeOrOptionIsNull(customFieldEditDTO);

        List<CustomFieldOptionDTO> options = customFieldEditDTO.getTypeConfig().getOptions();

        List<CustomFieldLabel> customFieldLabels = new ArrayList<>();
        List<UUID> hasLabelIdList = new ArrayList<>();

        for (CustomFieldOptionDTO option : options) {
            for (CustomFieldLabel label : labels) {
                if (option.getId() == null) {
                    CustomFieldLabel customFieldLabel = new CustomFieldLabel();
                    customFieldLabel.setColorCode(option.getColorCode());
                    customFieldLabel.setLabel(option.getName());
                    customFieldLabel.setCustomFieldId(customField.getId());
                    customFieldLabels.add(customFieldLabel);

                } else if (label.getId().toString().equals(option.getId())) {
                    hasLabelIdList.add(label.getId());
                    label.setColorCode(option.getColorCode());
                    label.setLabel(option.getName());
                    label.setCustomFieldId(customField.getId());

                    customFieldLabels.add(label);

                }
            }
        }
        customFieldLabelRepository.deleteAllByCustomFieldIdAndIdNotIn(customField.getId(), hasLabelIdList);
        customFieldLabelRepository.saveAll(customFieldLabels);

    }


    @Override
    public ApiResult<CustomFiledTypeConfigDTO> editViewColumn(CustomFiledTypeConfigDTO typeConfig, UUID customFieldId, String customFieldName) {
        // log.info("_________class CustomFieldServiceImpl => editViewColumn =>  typeConfig: {}, customFieldId: {}, customFieldName: {}", typeConfig, customFieldId, customFieldName);

        CustomField customField = customFieldRepository.findById(customFieldId).orElseThrow(() -> RestException.restThrow(ResponseMessage.CUSTOM_FIELD_NOT_FOUND));
        // log.info("_________class CustomFieldServiceImpl => editViewColumn =>  customField: {}", customField);

        Map<String, CustomFieldOptionDTO> typeConfigMap = viewService.mapViewColumnTypeConfig(typeConfig.getOptions());

        if (customFieldName != null && !customFieldName.isBlank()) {
            customField.setName(customFieldName);
        }

        if (customField.getType().equals(CustomFieldTypeEnum.DROPDOWN)) {

            List<CustomFieldDropDown> dropDowns = customField.getDropDowns();  //db

            List<CustomFieldDropDown> savedDropDown = new ArrayList<>();
            List<UUID> deleteDropDownIdList = new ArrayList<>();

            for (CustomFieldDropDown dropDown : dropDowns) {

                CustomFieldOptionDTO customFieldOptionDTO = typeConfigMap.get(dropDown.getId().toString());

                if (customFieldOptionDTO != null) {
                    dropDown.setName(customFieldOptionDTO.getName());
                    dropDown.setColorCode(customFieldOptionDTO.getColorCode());
                    savedDropDown.add(dropDown);
                } else {
                    deleteDropDownIdList.add(dropDown.getId());
                }
                typeConfigMap.remove(dropDown.getId().toString());
            }
            // new options
            for (Map.Entry<String, CustomFieldOptionDTO> customFieldOptionDTOEntry : typeConfigMap.entrySet()) {
                CustomFieldOptionDTO value = customFieldOptionDTOEntry.getValue();
                CustomFieldDropDown newCustomFieldDropDown = new CustomFieldDropDown();
                newCustomFieldDropDown.setName(value.getName());
                newCustomFieldDropDown.setColorCode(value.getColorCode());
                newCustomFieldDropDown.setCustomFieldId(customField.getId());
                newCustomFieldDropDown.setCustomField(customField);

                savedDropDown.add(newCustomFieldDropDown);
            }

            customField.setDropDowns(savedDropDown);
            customFieldRepository.save(customField);
//            customFieldDropDownRepository.saveAll(savedDropDown);  CASCAD SAQLAYDI
            customFieldDropDownRepository.deleteAllById(deleteDropDownIdList);

        } else if (customField.getType().equals(CustomFieldTypeEnum.LABELS)) {

            List<CustomFieldLabel> labels = customField.getLabels();  //db

            List<CustomFieldLabel> savedLabel = new ArrayList<>();
            List<UUID> deleteLabelIdList = new ArrayList<>();

            for (CustomFieldLabel label : labels) {

                CustomFieldOptionDTO customFieldOptionDTO = typeConfigMap.get(label.getId().toString());

                if (customFieldOptionDTO != null) {
                    label.setLabel(customFieldOptionDTO.getName());
                    label.setColorCode(customFieldOptionDTO.getColorCode());
                    savedLabel.add(label);
                } else {
                    deleteLabelIdList.add(label.getId());
                }
                typeConfigMap.remove(label.getId().toString());
            }
            // new options
            for (Map.Entry<String, CustomFieldOptionDTO> customFieldOptionDTOEntry : typeConfigMap.entrySet()) {
                CustomFieldOptionDTO value = customFieldOptionDTOEntry.getValue();
                CustomFieldLabel newCustomFieldLabel = new CustomFieldLabel();
                newCustomFieldLabel.setLabel(value.getName());
                newCustomFieldLabel.setColorCode(value.getColorCode());
                newCustomFieldLabel.setCustomFieldId(customField.getId());
                newCustomFieldLabel.setCustomField(customField);

                savedLabel.add(newCustomFieldLabel);
            }
            customField.setLabels(savedLabel);

            customFieldRepository.save(customField);
//            customFieldLabelRepository.saveAll(savedLabel);   CASCAD SAQLAYDI
            customFieldLabelRepository.deleteAllById(deleteLabelIdList);
        }

        CustomFiledTypeConfigDTO customFieldTypeConfig = mapCustomFieldTypeConfigFromCustomField(customField);
        // log.info("_________class CustomFieldServiceImpl => editViewColumn =>  customFieldTypeConfig: {}", customFieldTypeConfig);

        return ApiResult.successResponse(customFieldTypeConfig);
    }


    //CUSTOM_FIELD_ID AND  CUSTOM_FIELD_VALUE
    private Map<UUID, CustomFieldValue> makeHashMapCustomFieldIdAndValue(List<CustomFieldValue> customFieldValueList) {
        return customFieldValueList.stream().collect(Collectors.toMap(
                CustomFieldValue::getCustomFieldId,
                customFieldValue -> customFieldValue)
        );
    }

    //STRING QILIB SAQLANGAN ARRAYNI
    private String[] getSelectedLabels(String value) {
        String arrayString = CommonUtils.replaceBracket(value);
        return arrayString.split(",");
    }

    //CUSTOM FIELD NI NAME VA TABLE_NAME AVVAL YARATILANMI TEKSHIRADI
    private void checkExistCustomFieldNameAndTableName(String name, String tableName) {
        // log.info("_________class CustomFieldServiceImpl => checkExistCustomFieldNameAndTableName =>  name: {}, tableName: {} ", name, tableName);

        //SHU NOMLI CUSTOM FIELD SHU TABLE UCHUN YARATILGANMI YO'QMI TEKSHIRADI
        boolean exist = customFieldRepository.existsByNameAndTableName(name, tableName);
        if (exist)
            throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_CUSTOM_FIELD);
    }

    //CUSTOM FIELD DROPDOWN UCHUN OPTIONLARNI SAQLAYDI
    private void saveCustomFieldDropDown(CustomField customField, CustomFieldAddDTO customFieldAddDTO, CustomFieldDTO customFieldDTO) {
        // log.info("_________class CustomFieldServiceImpl => saveCustomFieldDropDown =>  customField: {}, customFieldAddDTO: {}, customFieldDTO: {} ", customField, customFieldAddDTO, customFieldDTO);

        //CUSTOM FIELD TYPE CONFIG NI NULL EMASLIGINI TEKSHIRADI
        checkTypeConfigNotNull(customFieldAddDTO.getTypeConfig());

        //CUSTOM FIELD NING OPTIONS LARI BO'SH EMASLIGINI TEKSHIRADI
        checkOptionsNotEmpty(customFieldAddDTO.getTypeConfig().getOptions());

        //List<CustomFieldOptionDTO> ni -> List<CustomFieldDropDown> PARSE QILIB BERADI
        List<CustomFieldDropDown> customFieldDropDownList = mapCustomFieldOptionDTOToCustomFieldDropDownList(customFieldAddDTO.getTypeConfig().getOptions(), customField.getId());

        customFieldDropDownRepository.saveAll(customFieldDropDownList);

        customField.setDropDowns(customFieldDropDownList);
    }

    //CUSTOM FIELD TURI RATING BO'LSA SHU RATING NI SAQLAB BERADI
    private void saveCustomFieldRating(CustomField customField, CustomFieldAddDTO customFieldAddDTO) {
        // log.info("_________class CustomFieldServiceImpl => saveCustomFieldRating =>  customField: {}, customFieldAddDTO: {}", customField, customFieldAddDTO);

        //CUSTOM FIELD TYPE CONFIG NI NULL EMASLIGINI TEKSHIRADI
        checkTypeConfigNotNull(customFieldAddDTO.getTypeConfig());

        //CUSTOM FIELD NING RATING I BO'SH EMASMI TEKSHIRADI
        checkRatingNotNull(customFieldAddDTO.getTypeConfig().getRatingConfig());

        //CustomFiledTypeConfigDTO NI -> CustomFieldRating GA PARSE QILIB BERADI
        CustomFieldRating customFieldRating = mapCustomFiledTypeConfigDTOToCustomFieldRating(customFieldAddDTO.getTypeConfig().getRatingConfig(), customField.getId());

        customFieldRatingRepository.save(customFieldRating);

        customField.setCustomFieldRating(customFieldRating);
    }

    //CUSTOM FIELD LABEL LARINI UCHUN OPTIONLARNI SAQLAB BERADI
    private void saveCustomFieldLabels(CustomField customField, CustomFieldAddDTO customFieldAddDTO) {
        // log.info("_________class CustomFieldServiceImpl => saveCustomFieldLabels =>  customField: {}, customFieldAddDTO: {}, ", customField, customFieldAddDTO);

        //CUSTOM FIELD TYPE CONFIG NI NULL EMASLIGINI TEKSHIRADI
        checkTypeConfigNotNull(customFieldAddDTO.getTypeConfig());

        //CUSTOM FIELD NING OPTIONS LARI BO'SH EMASLIGINI TEKSHIRADI
        checkOptionsNotEmpty(customFieldAddDTO.getTypeConfig().getOptions());

        //List<CustomFieldOptionDTO> NI -> List<CustomFieldLabel> GA PARSE QILADI
        List<CustomFieldLabel> customFieldLabels = mapOptionDTOListToCustomFieldLabelList(customFieldAddDTO.getTypeConfig().getOptions(), customField.getId());

        customFieldLabelRepository.saveAll(customFieldLabels);

        customField.setLabels(customFieldLabels);
    }

    //CUSTOM FIELD TYPE CONFIG NI NULL EMASLIGINI TEKSHIRADI
    private void checkTypeConfigNotNull(CustomFiledTypeConfigDTO customFiledTypeConfigDTO) {
        // log.info("_________class CustomFieldServiceImpl => checkTypeConfigNotNull =>  customFiledTypeConfigDTO: {} ", customFiledTypeConfigDTO);

        if (customFiledTypeConfigDTO == null)
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_TYPE_CONFIG_REQUIRED, HttpStatus.BAD_REQUEST);
    }

    //CUSTOM FIELD NING OPTIONS LARI BO'SH EMASLIGINI TEKSHIRADI
    private void checkOptionsNotEmpty(List<CustomFieldOptionDTO> options) {
        if (options == null || options.isEmpty())
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_OPTIONS_REQUIRED, HttpStatus.BAD_REQUEST);
    }

    //CUSTOM FIELD NING RATING I BO'SH EMASMI TEKSHIRADI
    private void checkRatingNotNull(RatingConfigDTO ratingConfigDTO) {
        if (ratingConfigDTO.getCount() == null || ratingConfigDTO.getCodePoint() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_RATING_REQUIRED, HttpStatus.BAD_REQUEST);
    }

    //OBJECT BO'SH BO'LMASLIGINI TEKSHIRADI
    private void checkObjectNotNull(Object object) {
        if (object == null)
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_RATING_REQUIRED, HttpStatus.BAD_REQUEST);
    }


    //================CUSTOM FIELD MAP QILUVCHI METHOD LAR =========>>>>>>>>>>>>>

    public CustomField mapCustomFieldAddDTOToCustomField(CustomFieldAddDTO customFieldAddDTO) {
        if (customFieldAddDTO == null) {
            return null;
        }

        CustomField customField = new CustomField();

        customField.setName(customFieldAddDTO.getName());
        customField.setRequired(customFieldAddDTO.isRequired());
        customField.setType(customFieldAddDTO.getType());

        return customField;
    }

    public CustomFieldDTO mapCustomFieldTODTO(CustomField customField) {
        if (customField == null) {
            return null;
        }

        CustomFieldDTO customFieldDTO = new CustomFieldDTO();

        customFieldDTO.setId(customField.getId());
        customFieldDTO.setName(customField.getName());
        customFieldDTO.setType(customField.getType());
        customFieldDTO.setRequired(customField.isRequired());

        customFieldDTO.setTypeConfig(mapCustomFieldTypeConfigFromCustomField(customField));

        return customFieldDTO;
    }

    public List<CustomFieldDTO> mapCustomFieldListToDTOList(List<CustomField> customFieldList) {
        if (customFieldList == null) {
            return null;
        }

        List<CustomFieldDTO> list = new ArrayList<>(customFieldList.size());
        for (CustomField customField : customFieldList) {
            list.add(mapCustomFieldTODTO(customField));
        }

        return list;
    }

    public void mapCustomFielDTOCustomFieldDTO(CustomField customField, CustomFieldDTO customFieldDTO) {
        if (customField == null) {
            return;
        }
        customFieldDTO.setId(customField.getId());
        customFieldDTO.setName(customField.getName());
        customFieldDTO.setType(customField.getType());
        customFieldDTO.setRequired(customField.isRequired());
    }


    //CALL STATUS LAR UCHUN OPTION LARNI QAYTARADI
    private List<CallStatusOptionDTO> mapCallStatusOptions() {

        List<CallStatusOptionDTO> callStatusOptionDTOList = new ArrayList<>();

        for (CallStatusEnum callStatusEnum : CallStatusEnum.values()) {
            CallStatusOptionDTO callStatusOptionDTO = CallStatusOptionDTO.builder()
                    .id(callStatusEnum.name())
                    .name(callStatusEnum.name())
                    .colorCode(callStatusEnum.getColorCode())
                    .build();
            callStatusOptionDTOList.add(callStatusOptionDTO);
        }
        return callStatusOptionDTOList;
    }

    //CALL TYPE UCHUN OPTION LARNI QAYTARADI
    private List<CallTypeOptionDTO> mapCallTypeOptions() {

        List<CallTypeOptionDTO> callTypeOptionDTOList = new ArrayList<>();

        //BARCHA CALL TYPE ENUMLARNI AYLANIB AYLANIB callTypeOptionDTOList NI QAYTARADI
        for (CallTypeEnum callTypeEnum : CallTypeEnum.values()) {
            //CallTypeEnum DAN CallTypeOptionDTO YARATILADI
            CallTypeOptionDTO callTypeOptionDTO = CallTypeOptionDTO.builder()
                    .id(callTypeEnum.name())
                    .name(callTypeEnum.name())
                    .build();
            callTypeOptionDTOList.add(callTypeOptionDTO);
        }
        return callTypeOptionDTOList;
    }

    //MESSAGE UCHUN CONFIG YASAB QAYTARADI
    private MessageConfigDTO mapMessageConfigDTO() {
        MessageConfigDTO messageConfigDTO = MessageConfigDTO.builder()
                .url(RestConstants.CALL_CENTER_MESSAGE_URL)
                .build();
        return messageConfigDTO;
    }

    //ATTACHMENT UCHUN TYPE_CONFIG YASAB QAYTARADI
    private AttachmentConfigDTO mapAttachmentConfigDTO() {
        AttachmentConfigDTO attachmentConfigDTO = AttachmentConfigDTO.builder()
                .url(RestConstants.ATTACHMENT_SERVICE_FILE_GET_URL)
                .build();
        return attachmentConfigDTO;
    }


    //TURI CALL_DURATION BO'LSA SHU CALL_DURATION NING TYPE CONFIGINI QAYTARADI
    private CallDurationConfigDTO mapCallDurationConfig(String tableName) {
        //SHU TABLE NING MAP MA'LUMOTLARI
        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> tableFields = TableMapList.ENTITY_FIELDS.get(tableName);

        //CALL TURINI MA'LUMOTLARI
        Map<EntityColumnMapValuesKeyEnum, String> callTypeData = tableFields.get(CustomFieldTypeEnum.CALL);

        //QAYSI API GA SO'ROV KETADI
        String requestUrl = callTypeData.get(EntityColumnMapValuesKeyEnum.REQUEST_URL);

        //CALL UCHUN CONFIG YARATILADI
        return CallDurationConfigDTO.builder()
                .url(requestUrl)
                .build();
    }

    //TURI CALL BO'LSA SHU CALL NING TYPE CONFIGINI QAYTARADI
    private CallConfigDTO mapCallConfig(String tableName) {
        //SHU TABLE NING MAP MA'LUMOTLARI
        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> tableFields = TableMapList.ENTITY_FIELDS.get(tableName);

        //CALL TURINI MA'LUMOTLARI
        Map<EntityColumnMapValuesKeyEnum, String> callTypeData = tableFields.get(CustomFieldTypeEnum.CALL);

        //QAYSI API GA SO'ROV KETADI
        String requestUrl = callTypeData.get(EntityColumnMapValuesKeyEnum.REQUEST_URL);

        //CALL UCHUN CONFIG YARATILADI
        return CallConfigDTO.builder()
                .url(requestUrl)
                .build();
    }

    //TURI SPECIAL_LABEL BO'LSA SHU SPECIAL_LABEL NING TYPE CONFIGINI QAYTARADI
    private SpecialLabelConfigDTO mapSuperLabelConfig(String tableName) {
        //SHU TABLE NING MAP MA'LUMOTLARI
        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> tableFields = TableMapList.ENTITY_FIELDS.get(tableName);

        //CALL TURINI MA'LUMOTLARI
        Map<EntityColumnMapValuesKeyEnum, String> callTypeData = tableFields.get(CustomFieldTypeEnum.CALL);

        //QAYSI API GA SO'ROV KETADI
        String requestUrl = callTypeData.get(EntityColumnMapValuesKeyEnum.REQUEST_URL);

        //CALL UCHUN CONFIG YARATILADI
        return SpecialLabelConfigDTO.builder()
                .url(requestUrl)
                .build();
    }


    //==============DROPDOWN NI METHOD LARI=======>>>>


    //DROPDOWN LAR UCHUN OPTION LAR NI QAYTARADI
    private List<CustomFieldOptionDTO> mapDropdownListOptionDTOList(List<CustomFieldDropDown> dropDownList, String customFieldId) {
        return dropDownList.stream().map(dropDown -> mapDropdownToOptionDTO(dropDown, customFieldId)).collect(Collectors.toList());
    }

    //LABEL UCHUN OPTION DTO QAYTARADI
    private CustomFieldOptionDTO mapDropdownToOptionDTO(CustomFieldDropDown dropDown, String customFieldId) {
        return CustomFieldOptionDTO.builder()
                .orderIndex(dropDown.getOrderIndex())
                .name(dropDown.getName())
                .colorCode(dropDown.getColorCode())
                .id(dropDown.getId().toString())
                .build();
    }

    public CustomFieldDropDown mapCustomFieldOptionDTOToCustomFieldDropDown(CustomFieldOptionDTO customFieldOptionDTO, UUID customFieldId) {
        if (customFieldOptionDTO == null) {
            return null;
        }

        CustomFieldDropDown customFieldDropDown = new CustomFieldDropDown();

        customFieldDropDown.setName(customFieldOptionDTO.getName());
        customFieldDropDown.setColorCode(customFieldOptionDTO.getColorCode());
        customFieldDropDown.setOrderIndex(customFieldOptionDTO.getOrderIndex());

        customFieldDropDown.setCustomFieldId(customFieldId);

        return customFieldDropDown;
    }

    public List<CustomFieldDropDown> mapCustomFieldOptionDTOToCustomFieldDropDownList(List<CustomFieldOptionDTO> customFieldOptionDTOList, UUID customFieldId) {
        if (customFieldOptionDTOList == null) {
            return null;
        }
        List<CustomFieldDropDown> list = new ArrayList<>(customFieldOptionDTOList.size());
        for (CustomFieldOptionDTO customFieldOptionDTO : customFieldOptionDTOList) {
            list.add(mapCustomFieldOptionDTOToCustomFieldDropDown(customFieldOptionDTO, customFieldId));
        }
        return list;
    }
    //=============DROPDOWN NI METHOD LARI TUGADI========


    //============RATING UCHUN METHOD LAR============
    public CustomFieldRating mapCustomFiledTypeConfigDTOToCustomFieldRating(RatingConfigDTO ratingConfigDTO, UUID customFieldId) {
        if (ratingConfigDTO == null) {
            return null;
        }

        CustomFieldRating customFieldRating = new CustomFieldRating();
        customFieldRating.setCodePoint(ratingConfigDTO.getCodePoint());
        customFieldRating.setCount(ratingConfigDTO.getCount());
        customFieldRating.setCustomFieldId(customFieldId);

        return customFieldRating;
    }

    //RATING UCHUN CONFIG YASAB QAYTARADI
    private RatingConfigDTO mapRatingToConfigDTO(CustomFieldRating customFieldRating) {
        RatingConfigDTO ratingConfigDTO = null;
        //RATING NULL BO'LMASIN
        if (customFieldRating != null) {
            ratingConfigDTO = RatingConfigDTO.builder()
                    .count(customFieldRating.getCount())
                    .codePoint(customFieldRating.getCodePoint())
                    .build();
        }
        return ratingConfigDTO;
    }
    //============RATING UCHUN METHOD LAR============


    //===========LABEL UCHUN========>>>>>>>>>>>
    public CustomFieldLabel mapOptionDTOToCustomFieldLabel(CustomFieldOptionDTO customFieldOptionDTO, UUID customFieldId) {
        if (customFieldOptionDTO == null) {
            return null;
        }

        CustomFieldLabel customFieldLabel = new CustomFieldLabel();

        customFieldLabel.setLabel(customFieldOptionDTO.getName());
        customFieldLabel.setColorCode(customFieldOptionDTO.getColorCode());
        customFieldLabel.setOrderIndex(customFieldOptionDTO.getOrderIndex());
        customFieldLabel.setCustomFieldId(customFieldId);

        return customFieldLabel;
    }

    public List<CustomFieldLabel> mapOptionDTOListToCustomFieldLabelList(List<CustomFieldOptionDTO> customFieldOptionDTOList, UUID customFieldId) {
        return customFieldOptionDTOList.stream().map(customFieldOptionDTO -> mapOptionDTOToCustomFieldLabel(customFieldOptionDTO, customFieldId)).collect(Collectors.toList());
    }


    //LABEL LARNI OPTIONLARINI CONFIGINI QAYTARADI
    private List<CustomFieldOptionDTO> mapLabelListOptionDTOList(List<CustomFieldLabel> labels, String customFieldId) {
        return labels.stream().map(this::mapLabelToOptionDTO).collect(Collectors.toList());
    }

    //LABEL UCHUN OPTION DTO QAYTARADI
    private CustomFieldOptionDTO mapLabelToOptionDTO(CustomFieldLabel label) {
        return CustomFieldOptionDTO.builder()
                .orderIndex(label.getOrderIndex())
                .name(label.getLabel())
                .colorCode(label.getColorCode())
                .id(label.getId().toString())
                .build();
    }

    private void checkCustomFieldTypeOrOptionIsNull(CustomFieldEditDTO customFieldEditDTO) {
        if (customFieldEditDTO.getTypeConfig() == null || customFieldEditDTO.getTypeConfig().getOptions() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_OPTIONS_REQUIRED);
    }

    //===========LABEL UCHUN========>>>>>>>>>>>
}
