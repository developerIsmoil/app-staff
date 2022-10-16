package ai.ecma.appstaff.service.customField;

import ai.ecma.appstaff.entity.customField.*;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;
import ai.ecma.appstaff.repository.customField.CustomFieldRepository;
import ai.ecma.appstaff.repository.customField.CustomFieldValueRepository;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ai.ecma.appstaff.enums.CustomFieldTypeEnum.*;


@Service
@RequiredArgsConstructor
public class CustomFieldValueServiceImpl implements CustomFieldValueService {
    private final CustomFieldValueRepository customFieldValueRepository;
    private final CustomFieldRepository customFieldRepository;


    //ONE VALUE
    @Override
    public ApiResult<CustomFieldValueDTO> addCustomFieldValue(CustomFieldValueDTO customFieldValueDTO) {

        boolean haveValue = customFieldValueDTO.getValue() != null;

        //ID ORQALI CUSTOM FIELD NIQIDIRADI AKS HOLDA THROW
        CustomField customField = findByIdCustomFieldOrElseThrow(customFieldValueDTO.getCustomFieldId());

        if (haveValue)
            //CUSTOM FIELD LARNI HAR BIR TURINI TEKSHIRIB CHIQADI, LABEL VA DROPDOWN DA SHU OPTIONLAR BORMI, DATE NI TIMESTAMP GA PARSE QILIB KO'RADI,
            //RATING NI SHU QIYMATDAN OSHIB KETMAGANMI TEKSHIRADI
            checkCustomFieldOptionAndSimpleTypes(customFieldValueDTO, customField);

        //BERILGAN OWNER ID VA CUSTOM_FIELD_ID GA TENG CUSTOM_FIELD_VALUE NI QAYTARADI. AKS HOLDA YANGI YARATADI
        CustomFieldValue customFieldValue = customFieldValueRepository.findFirstByOwnerIdAndCustomFieldId(customFieldValueDTO.getOwnerId(), customField.getId()).orElse(new CustomFieldValue());
        customFieldValue.setCustomField(customField);
        customFieldValue.setCustomFieldId(customField.getId());
        customFieldValue.setValue(haveValue ? customFieldValueDTO.getValue().toString() : null);
        customFieldValue.setOwnerId(customFieldValueDTO.getOwnerId());

        customFieldValueRepository.save(customFieldValue);

        return ApiResult.successResponse(customFieldValueDTO);
    }

    //LIST VALUE
    @Override
    public ApiResult<List<CustomFieldValueDTO>> addCustomFieldValueList(List<CustomFieldValueDTO> customFieldValueDTOList) {

        //customFieldValueDTOList LAR ICHIDAN BIZGA KERAK BO'LMAGAN VALUE NULL GA TENG BO'LGAN ELEMENTLARNI O'CHIRIB YUBORAMIZ
        customFieldValueDTOList.removeIf(customFieldValueDTO -> customFieldValueDTO.getValue() == null);

        //CUSTOM_FIELD_ID LARNI KETMA-KETLIK NI QAT'IY RAVISHDA SAQLASH. BUNDA CUSTOM_FIELD_ID_LIST OWNER_ID_LIST
        // KETMA-KETLIGI BIR-BIRI BILAN SINXRON. MAQSAD CUSTOM_FIELD_VALUE NI QIDIRGANDA
        //customFieldIdLinkedList VA ownerIdLinkedList NI IN QILIB BERIB YUBORISH.
        List<UUID> customFieldIdLinkedList = new LinkedList<>();

        //CUSTOM_FIELD_VALUE BIRIKTIRILISHI KERAK BO'LGAN OWNER_ID LAR LISTI
        List<String> ownerIdLinkedList = new LinkedList<>();

        //CUSTOM_FIELD_VALUE LAR ICHIDAN
        //VALUE NULL BO'LMAGAN CUSTOM_FIELD_ID LARNI SET GA YIG'IB OLAMIZ
        Set<UUID> customFieldIdSet = new HashSet<>();

        //customFieldValueDTOList NI AYLANIB customFieldIdLinkedList VA ownerIdLinkedList GA QIYMATLARNI KIRITIB OLAMIZ
        for (CustomFieldValueDTO customFieldValueDTO : customFieldValueDTOList) {
            //CUSTOM_FIELD_ID LARNI LINKEDLIST GA QO'SHAMIZ
            customFieldIdLinkedList.add(customFieldValueDTO.getCustomFieldId());

            //CUSTOM_FIELD_VALUE_DTO NING OWNER ID SINI LINKED LIST GA YIG'AMIZ
            ownerIdLinkedList.add(customFieldValueDTO.getOwnerId());

            //CustomFieldValueDTO NING customFieldId SINI customFieldIdSet GA QO'SHIB BORAMIZ
            customFieldIdSet.add(customFieldValueDTO.getCustomFieldId());
            customFieldValueDTO.setNeedless(false);
        }

        //SHU ID GA TEGISHLI BARCHA CUSTOM_FIELD LARNI OLIB KELAMIZ
        List<CustomField> customFieldList = customFieldRepository.findAllById(customFieldIdSet);

        //CUSTOM_FIELD LAR NI CUSTOM_FIELD_ID VA CUSTOM_FIELD DAN IBORAT HASHMAP YASAB QAYTARADI
        Map<UUID, CustomField> customFieldIdAndCustomFieldMap = makeCustomFieldIdAndCustomFieldMap(customFieldList);

        //CUSTOM_FIELD LARNI AYLANIB QIYMATLARNI TEKSHIRIB CHIQAMIZ
        for (CustomFieldValueDTO customFieldValueDTO : customFieldValueDTOList) {

            //USHBU METHOD O'ZIGA customFieldIdAndCustomFieldValueDTOMap NI QABUL QILIB MAP DAN BERILGAN  customFieldId LI VALUE NI QAYTARADI
            CustomField customField = getCustomFieldFromCustomFieldIdAndCustomFieldMap(customFieldValueDTO.getCustomFieldId(), customFieldIdAndCustomFieldMap);

            try {
                //CUSTOM FIELD LARNI HAR BIR TURINI TEKSHIRIB CHIQADI, LABEL VA DROPDOWN DA SHU OPTIONLAR BORMI, DATE NI TIMESTAMP GA PARSE QILIB KO'RADI,
                //RATING NI SHU QIYMATDAN OSHIB KETMAGANMI TEKSHIRADI
                checkCustomFieldOptionAndSimpleTypes(customFieldValueDTO, customField);
            } catch (RestException restException) {
                restException.printStackTrace();
                //CATCH GA TUSHDI DEGANI USHBU QIYMAT TEKSHIRUVDAN O'TOLMADI DEGANI.
                //TEKSHIRUVDAN O'TOLMASA SHU QIYMATNI KERAKSIZLIGINI BILDIRIB QO'YISHIMIZ KERAK
                //AGAR CUSTOM FIELD TURI DROPDOWN YOKI LABEL BO'LSA NEEDLESS NI TRUE QILIB QO'YILADI
                //(NEEDLESS TRUE BO'LGAN CustomFieldValueDTO NING QIYMATLARI SET QILINMAYDI) VA ISHDA DAVOM
                //ETILADI AKS HOLDA checkCustomFieldOptionAndSimpleTypes METHODIDAN KELGAN
                //THROW GA OTILADI
                if (customField.getType().equals(DROPDOWN) || customField.getType().equals(LABELS))
                    customFieldValueDTO.setNeedless(true);
                else throw restException;
            }
        }

        //CUSTOM_FIELD_ID LIST VA OWNER_ID_LIST QAY TARTIB DA BO'LSA SHU KETMA-KETLIKDAGI CUSTOM_FIELD_VALUE LARNI DB DAN OLIB KELADI
        List<CustomFieldValue> customFieldValueList = customFieldValueRepository.findAllCustomFieldValueListByCustomFieldIdLinkedListAndOwnerIdLinkedList(
                customFieldIdLinkedList,
                ownerIdLinkedList
        );

        //USHBU METHOD DB DAN OLINGAN BARCHA customFieldValueList LAR GA customFieldValueDTOList DAGI QIYMATLARNI
        //QO'YIB CHIQADI. AGAR customFieldValueDTOList DA YO'Q QIYMATLAR BO'LSA YANGI CUSTOM_FIELD_VALUE YARATIB
        //customFieldValueList GA QO'SHIB QO'YADI
        setValueToCustomFieldValueList(customFieldValueList, customFieldValueDTOList);

        //LIST DA YIG'ILGAN BARCHA customFieldValueList NI SAQLANDI
        customFieldValueRepository.saveAll(customFieldValueList);

        return ApiResult.successResponse(customFieldValueDTOList);
    }


    //USHBU METHOD DB DAN OLINGAN BARCHA customFieldValueList LAR GA customFieldValueDTOList DAGI QIYMATLARNI
    //QO'YIB CHIQADI. AGAR customFieldValueDTOList DA YO'Q QIYMATLAR BO'LSA YANGI CUSTOM_FIELD_VALUE YARATIB
    //customFieldValueList GA QO'SHIB QO'YADI
    private void setValueToCustomFieldValueList(List<CustomFieldValue> customFieldValueList, List<CustomFieldValueDTO> customFieldValueDTOList) {
        for (CustomFieldValue customFieldValue : customFieldValueList) {
            //customFieldValueDTOList NI AYLANIB CustomFieldValue DAGI CustomFieldId VA OwnerId GA TENG BO'LGANINI TOPAMIZ
            for (CustomFieldValueDTO customFieldValueDTO : customFieldValueDTOList) {

                //customFieldValueDTOList NI AYLANIB CustomFieldValue DAGI CustomFieldId VA OwnerId GA TENG BO'LGANINI VA
                //customFieldValueDTO NI NEEDLESS FIELDI FALSE BO'LGANLARNI
                //TOPAMIZ VA UNGA CustomFieldValueDTO DAGI QIYMATLARNI O'RNATAMIZ
                if (customFieldValueDTO.getCustomFieldId().equals(customFieldValue.getCustomFieldId()) &&
                        customFieldValueDTO.getOwnerId().equals(customFieldValue.getOwnerId()) &&
                        !customFieldValueDTO.isNeedless()) {

                    customFieldValue.setCustomFieldId(customFieldValueDTO.getCustomFieldId());
                    customFieldValue.setValue(customFieldValueDTO.getValue().toString());
                    customFieldValue.setOwnerId(customFieldValueDTO.getOwnerId());

                    //customFieldValueDTOList DAN HOZIRGI TURGAN customFieldValueDTO NI O'CHIRIB BORAMIZ
                    customFieldValueDTOList.remove(customFieldValueDTO);
                    break;
                }
            }
        }

        //AGAR customFieldValueDTOList DA BIRORTA CustomFieldValueDTO QOLGAN BO'LSA ULAR YANGI ELEMENTLAR HISOBLANADI
        //CustomFieldValueDTO DAN YANGI CustomFieldValue NI YARATIB UMUMIY  customFieldValueList GA QO'SHIB QO'YAMIZ
        if (!customFieldValueDTOList.isEmpty()) {

            //ENDI FAQATGINA SAQLANMAGAN  CustomFieldValueDTO LAR QOLGAN customFieldValueDTOList NI AYLANIB
            //YANGI CustomFieldValue YASAB UNI UMUMIY customFieldValueList GA QO'SHIB QO'YAMIZ
            for (CustomFieldValueDTO customFieldValueDTO : customFieldValueDTOList) {
                CustomFieldValue customFieldValue = new CustomFieldValue();
                customFieldValue.setCustomFieldId(customFieldValueDTO.getCustomFieldId());
                customFieldValue.setValue(customFieldValueDTO.getValue().toString());
                customFieldValue.setOwnerId(customFieldValueDTO.getOwnerId());

                //YARATILGAN YANGI CustomFieldValue NI UMUMIY LIST GA QO'SHIB BORAMIZ
                customFieldValueList.add(customFieldValue);
            }
        }
    }

    //CUSTOM FIELD LARNI HAR BIR TURINI TEKSHIRIB CHIQADI, LABEL VA DROPDOWN DA SHU OPTIONLAR BORMI, DATE NI TIMESTAMP GA PARSE QILIB KO'RADI,
    //RATING NI SHU QIYMATDAN OSHIB KETMAGANMI TEKSHIRADI
    private void checkCustomFieldOptionAndSimpleTypes(CustomFieldValueDTO customFieldValueDTO, CustomField customField) {
        //CUSTOM FIELD TURI
        CustomFieldTypeEnum type = customField.getType();

        if (type.equals(DROPDOWN)) {
            //DROPDOWN TIPIDAGI CUSTOM FIELD GA QIYMAT QO'SHILGAN BO'LSA SHU OPTION BORLIGINI TEKSHIRAMIZ
            checkDropDown(customFieldValueDTO, customField.getDropDowns());
        } else if (type.equals(LABELS)) {
            //DROPDOWN TIPIDAGI CUSTOM FIELD GA QIYMAT QO'SHILGAN BO'LSA SHU OPTION BORLIGINI TEKSHIRAMIZ
            checkLabels(customFieldValueDTO, customField.getLabels());
        } else if (type.equals(NUMBER) || type.equals(MONEY)) {
            //BERILGAN TUR NUMBER EKANLIGINI TEKSHIRADI AKS HOLDA THROW
            checkNumberAndMoney(customFieldValueDTO);
        } else if (type.equals(DATE)) {
            //DATE NI TIME_STAMP GA O'GIRIB TEKSHIRIB KO'RADI
            checkDate(customFieldValueDTO);
        } else if (type.equals(RATING)) {
            //CUSTOM FIELD RATING NI RAQAM VA MAXIMAL CHEGARADAN OSHIB KETMAGANLINI TEKSHIRADI
            checkRating(customFieldValueDTO, customField.getCustomFieldRating());
        } else if (customField.getType().equals(TREE)) {

            String string = replaceBracketAndBla(customFieldValueDTO.getValue());
            String[] split = string.split(",");
            customFieldValueDTO.setValue(string);

            //BERILGAN String[] DAGI BARCHA QIYMATLAR LABELDA BORMI TEKSHIRADI
            existValuesInTrees(customField.getTrees(), split);

        }
    }

    private String replaceBracketAndBla(Object object) {
        String s = object.toString().replaceAll("[\\[\\]\" ]", "");
        System.out.println(s);
        return s;
    }

    private void existValuesInTrees(List<CustomFieldTree> trees, String[] split) {
        Set<UUID> valueIdSet = Arrays.stream(split).map(UUID::fromString).collect(Collectors.toSet());
        for (CustomFieldTree tree : trees) {
            valueIdSet.remove(tree.getId());
        }
        if (!valueIdSet.isEmpty())
            throw RestException.restThrow("USHBU QIYMATLAR TREE DA MAVJUD EMAS", HttpStatus.BAD_REQUEST);
    }

//    //CUSTOM_FIELD_VALUE_DTO DA VALUE ANIQ KELISHI KERAK
//    private void checkNotNullValue(Object value) {
//        if (value == null)
//            throw RestException.restThrow("CUSTOM_FIELD_VALUE_NOT_NULL"), HttpStatus.BAD_REQUEST);
//    }

    //CUSTOM_FIELD LAR NI CUSTOM_FIELD_ID VA CUSTOM_FIELD DAN IBORAT HASHMAP YASAB QAYTARADI
    private Map<UUID, CustomField> makeCustomFieldIdAndCustomFieldMap(List<CustomField> customFieldList) {
        HashMap<UUID, CustomField> customFieldIdAndCustomFieldValueDTOHashMap = new HashMap<>();
        for (CustomField customField : customFieldList)
            customFieldIdAndCustomFieldValueDTOHashMap.put(customField.getId(), customField);
        return customFieldIdAndCustomFieldValueDTOHashMap;
    }

    //USHBU METHOD O'ZIGA customFieldIdAndCustomFieldValueDTOMap NI QABUL QILIB MAP DAN BERILGAN  customFieldId LI VALUE NI QAYTARADI
    private CustomField getCustomFieldFromCustomFieldIdAndCustomFieldMap(UUID customFieldId,
                                                                         Map<UUID, CustomField> customFieldIdAndCustomFieldMap) {
        if (customFieldId == null) return null;
        CustomField customField = customFieldIdAndCustomFieldMap.get(customFieldId);
        if (customField == null) throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_NOT_FOUND);
        return customField;
    }

    //ID ORQALI CUSTOM FIELD NIQIDIRADI AKS HOLDA THROW
    private CustomField findByIdCustomFieldOrElseThrow(UUID id) {
        return customFieldRepository.findById(id).orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_NOT_FOUND));
    }

    //DROPDOWN TIPIDAGI CUSTOM FIELD GA QIYMAT QO'SHILGAN BO'LSA SHU OPTION BORLIGINI TEKSHIRAMIZ
    private void checkDropDown(CustomFieldValueDTO customFieldValueDTO, List<CustomFieldDropDown> dropDowns) {
        try {
            //QAVS LARNI QIRQIB TASHLAYDI
            String selectedOption = CommonUtils.replaceBracket(customFieldValueDTO.getValue().toString());

            //ID NI UUID GA PARSE QILIB KO'RAMIZ
            UUID dropDownId = UUID.fromString(selectedOption);

            //BERILGAN ID LI VALUE DROPDOWN DA BORMI TEKSHURADI
            existValueInDropDown(dropDowns, dropDownId);

            customFieldValueDTO.setValue(dropDownId);
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("TYPE_UUID_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    //DROPDOWN TIPIDAGI CUSTOM FIELD GA QIYMAT QO'SHILGAN BO'LSA SHU OPTION BORLIGINI TEKSHIRAMIZ
    private void checkLabels(CustomFieldValueDTO customFieldValueDTO, List<CustomFieldLabel> labels) {
        try {
            //QAVS LARNI QIRQIB TASHLAYDI
            String arrayString = CommonUtils.replaceBracket(customFieldValueDTO.getValue().toString());

            String[] selectedOptions = arrayString.split(",");

            //BERILGAN ID LI VALUE DROPDOWN DA BORMI TEKSHURADI
            existValuesInLabels(labels, selectedOptions);

            customFieldValueDTO.setValue(selectedOptions);
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("TYPE_UUID_ARRAY_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    //BERILGAN TUR NUMBER EKANLIGINI TEKSHIRADI AKS HOLDA THROW
    private void checkNumberAndMoney(CustomFieldValueDTO customFieldValueDTO) {
        try {
            Object value = numberValueWithoutLastZero(customFieldValueDTO.getValue().toString());
            customFieldValueDTO.setValue(value);
        } catch (Exception e) {
            throw RestException.restThrow("TYPE_NUMBER_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    //DATE NI TIME_STAMP GA O'GIRIB TEKSHIRIB KO'RADI
    private void checkDate(CustomFieldValueDTO customFieldValueDTO) {
        try {
            Timestamp value = new Timestamp(Long.parseLong(customFieldValueDTO.getValue().toString()));
            customFieldValueDTO.setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("TYPE_NUMBER_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    //CUSTOM FIELD RATING NI RAQAM VA MAXIMAL CHEGARADAN OSHIB KETMAGANLINI TEKSHIRADI
    private void checkRating(CustomFieldValueDTO customFieldValueDTO, CustomFieldRating customFieldRating) {
        try {
            int count = Integer.parseInt(customFieldValueDTO.getValue().toString());

            //AGAR COUNT BELGILANGAN
            if (count > customFieldRating.getCount())
                throw RestException.restThrow("RATING_GREATER_MAX", HttpStatus.BAD_REQUEST);

            customFieldValueDTO.setValue(count);
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("TYPE_NUMBER_REQUIRED", HttpStatus.BAD_REQUEST);
        }
    }

    //BU AYNAN DOUBLE NING OXIRIDAGI .0 LARNI OLIB TASHLASH UCHUN. .98 NI EMAS
    private static Object numberValueWithoutLastZero(String value) {
        double parseDouble = Double.parseDouble(value);

        long longValue = (long) parseDouble;
        return longValue == parseDouble ? longValue : parseDouble;
    }

    //BERILGAN ID LI VALUE DROPDOWN DA BORMI TEKSHURADI
    private void existValueInDropDown(List<CustomFieldDropDown> dropDowns, UUID dropDownValueId) {
        boolean anyMatch = dropDowns.stream().anyMatch(dropDown -> dropDown.getId().equals(dropDownValueId));
        if (!anyMatch)
            throw RestException.restThrow("THIS_VALUES_NOT_EXIST_DROPDOWN", HttpStatus.BAD_REQUEST);
    }

    //BERILGAN String[] DAGI BARCHA QIYMATLAR LABELDA BORMI TEKSHIRADI
    private void existValuesInLabels(List<CustomFieldLabel> labels, String[] values) {
        try {
            Set<UUID> valueIds = Arrays.stream(values).map(UUID::fromString).collect(Collectors.toSet());

            for (CustomFieldLabel label : labels)
                valueIds.remove(label.getId());

            if (!valueIds.isEmpty())
                throw RestException.restThrow("THIS_VALUES_NOT_EXIST_LABEL", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw RestException.restThrow("THIS_VALUES_NOT_EXIST_LABEL", HttpStatus.BAD_REQUEST);
        }
    }
}
