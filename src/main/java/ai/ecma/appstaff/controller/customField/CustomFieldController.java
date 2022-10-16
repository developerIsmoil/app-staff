package ai.ecma.appstaff.controller.customField;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFieldAddDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldEditDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.ViewColumnDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(CustomFieldController.CUSTOM_FIELD_CONTROLLER_PATH)
public interface CustomFieldController {

    String CUSTOM_FIELD_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/custom-field/";
    String FINANCE_ADD_CUSTOM_FIELD_PATH = "add-custom-field";
    String FINANCE_DELETE_CUSTOM_FIELD_PATH = "delete-custom-field";
    String FINANCE_EDIT_CUSTOM_FIELD_PATH = "edit-custom-field";


    /**
     * USHBU YO'LDA LEAD GA CUSTOM FIELD LAR QO'SHILADI
     *
     * @param customFieldAddDTO RequestBody
     * @return CustomFieldDTO
     */
    @PostMapping(path = FINANCE_ADD_CUSTOM_FIELD_PATH)
    ApiResult<ViewColumnDTO> addCustomField(@RequestBody @Valid CustomFieldAddDTO customFieldAddDTO);


    @DeleteMapping(path = FINANCE_DELETE_CUSTOM_FIELD_PATH + "/{customFieldId}")
    ApiResult<Boolean> deleteCustomField(@PathVariable UUID customFieldId);

    @PutMapping(path = FINANCE_EDIT_CUSTOM_FIELD_PATH )
    ApiResult<ViewColumnDTO> editCustomField(@RequestBody @Valid CustomFieldEditDTO customFieldEditDTO);

    @PutMapping(FINANCE_EDIT_CUSTOM_FIELD_PATH + "/editCustomField/{customFieldId}")
    ApiResult<CustomFiledTypeConfigDTO> editViewColumn(@RequestBody CustomFiledTypeConfigDTO typeConfig,
                                                       @PathVariable UUID customFieldId,
                                                       @RequestParam(required = false) String customFieldName
    );
}
