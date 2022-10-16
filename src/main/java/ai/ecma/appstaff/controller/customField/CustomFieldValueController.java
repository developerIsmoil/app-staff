package ai.ecma.appstaff.controller.customField;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(CustomFieldValueController.CUSTOM_FIELD_VALUE_CONTROLLER_PATH)
public interface CustomFieldValueController {
    String CUSTOM_FIELD_VALUE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/custom-field-value/";
    String FINANCE_ADD_CUSTOM_FIELD_VALUE_PATH = "add-custom-field-value";
    String FINANCE_ADD_CUSTOM_FIELD_VALUE_LIST_PATH = "add-custom-field-value-list";


    /**
     * CUSTOM FIELD GA VALUE QO'SHISH (BITTA CUSTOM FIELD UCHUN).
     * CUSTOM FIELD VALUE NING VALUE SI NULL BO'LMAGAN BARCHA VALUE LARNI OLIB SAQLAYDI,
     * VALUE LARNI TURI TEKSHIRILADI, AGAR DROPDOWN YOKI LABEL BO'LSA SHU VALUE OPTION
     * LAR ICHIDA BORMI, AGAR RATING BO'LSA MAKSIMAL QIYMATDAN OSHIB KETMAGANMI,
     * DATE NI BO'LSA TIMESTAMP GA PARSE QILIB KO'RILADI VA HOKAZO
     * @param customFieldValueDTO RequestBody
     * @return CustomFieldValueDTO
     */
    @PostMapping(path = FINANCE_ADD_CUSTOM_FIELD_VALUE_PATH)
    ApiResult<CustomFieldValueDTO> addCustomFieldValue(@RequestBody @Valid CustomFieldValueDTO customFieldValueDTO);


    /**
     * CUSTOM FIELD VALUE LAR LISTI NI QO'SHISH. CUSTOM FIELD VALUE NING
     * VALUE SI NULL BO'LMAGAN BARCHA VALUE LARNI OLIB SAQLAYDI, VALUE LARNI TURI TEKSHIRILADI,
     * AGAR DROPDOWN YOKI LABEL BO'LSA SHU VALUE OPTION LAR ICHIDA BORMI, AGAR RATING BO'LSA
     * MAKSIMAL QIYMATDAN OSHIB KETMAGANMI, DATE NI BO'LSA TIMESTAMP GA PARSE QILIB KO'RILADI VA HOKAZO
     * @param customFieldValueDTOList RequestBody
     * @return CustomFieldValueDTO
     */
    @PostMapping(path = FINANCE_ADD_CUSTOM_FIELD_VALUE_LIST_PATH)
    ApiResult<List<CustomFieldValueDTO>> addCustomFieldValueList(@RequestBody @Valid List<CustomFieldValueDTO> customFieldValueDTOList);

}
