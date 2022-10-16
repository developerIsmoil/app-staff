package ai.ecma.appstaff.mapper;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.entity.customField.CustomFieldDropDown;
import ai.ecma.appstaff.entity.customField.CustomFieldLabel;
import ai.ecma.appstaff.entity.customField.CustomFieldRating;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.payload.customField.CustomFieldAddDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ai.ecma.appstaff.enums.CustomFieldTypeEnum.*;


@Mapper(componentModel = "spring")
public abstract class CustomFieldMapper {
    @Autowired
    private CustomFieldDropDownMapper customFieldDropDownMapper;
    @Autowired
    private CustomFieldLabelMapper customFieldLabelMapper;
    @Autowired
    private CustomFieldRatingMapper customFieldRatingMapper;


    //CustomFieldAddDTO ni -> CustomField GA PARSE QILIB BERADI
    public abstract CustomField mapCustomFieldAddDTOToCustomField(CustomFieldAddDTO customFieldAddDTO);

    //CustomField NI -> CustomFieldDTO GA PARSE QILADI
    @Mapping(target = "typeConfig", expression = "java(typeConfigSetter(customField))")
    public abstract CustomFieldDTO mapCustomFielDTODTO(CustomField customField);

    //List<CustomField> NI -> List<CustomFieldDTO> GA PARSE QILADI
    public abstract List<CustomFieldDTO> mapCustomFieldListToDTOList(List<CustomField> customFieldList);

    //CustomField NI -> CustomFieldDTO GA PARSE QILIB BERADI
    public abstract void mapCustomFielDTOCustomFieldDTO(CustomField customField, @MappingTarget CustomFieldDTO customFieldDTO);


    //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
    protected CustomFiledTypeConfigDTO typeConfigSetter(CustomField customField) {
        CustomFieldTypeEnum type = customField.getType();
        CustomFiledTypeConfigDTO typeConfigDTO = new CustomFiledTypeConfigDTO();

        //DROPDOWN BO'LSA
        if (type.equals(DROPDOWN)) {

            List<CustomFieldDropDown> dropDowns = customField.getDropDowns();
            List<CustomFieldOptionDTO> customFieldOptionDTOList = customFieldDropDownMapper.mapCustomFieldDropDownToCustomFieldOptionDTOList(dropDowns);

            typeConfigDTO.setOptions(customFieldOptionDTOList);

            //LABELS BO'LSA
        } else if (type.equals(LABELS)) {

            List<CustomFieldLabel> labels = customField.getLabels();
            List<CustomFieldOptionDTO> customFieldOptionDTOList = customFieldLabelMapper.mapCustomFieldLabelToCustomFieldOptionDTOList(labels);

            typeConfigDTO.setOptions(customFieldOptionDTOList);

            //RATING BO'LSA
        } else if (type.equals(RATING)) {

            CustomFieldRating customFieldRating = customField.getCustomFieldRating();

            //AGAR RATING NULL BO'LMASA
            if (customFieldRating != null)
                typeConfigDTO = customFieldRatingMapper.mapCustomFieldRatingToCustomFiledTypeConfigDTO(customFieldRating);
        }

        return typeConfigDTO;
    }
}
