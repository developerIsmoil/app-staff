package ai.ecma.appstaff.mapper;

import ai.ecma.appstaff.entity.customField.CustomFieldLabel;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomFieldLabelMapper {

    //CustomFieldOptionDTO NI -> CustomFieldLabel GA PARSE QILIB BERADI
    @Mapping(target = "customFieldId", expression = "java(customFieldId)")
    @Mapping(target = "id", ignore = true)
    CustomFieldLabel mapCustomFieldOptionDTOToCustomFieldLabel(CustomFieldOptionDTO customFieldOptionDTO, @Context UUID customFieldId);

    //List<CustomFieldOptionDTO> NI -> List<CustomFieldLabel> GA PARSE QILADI
    List<CustomFieldLabel> mapCustomFieldOptionDTOToCustomFieldLabelList(List<CustomFieldOptionDTO> customFieldOptionDTOList, @Context UUID customFieldId);

    //CustomFieldLabel NI -> CustomFieldOptionDTO GA PARSE QILIB BERADI
    @Mapping(target = "id", expression = "java(customFieldLabel.getId().toString())")
    CustomFieldOptionDTO mapCustomFieldLabelToCustomFieldOptionDTO(CustomFieldLabel customFieldLabel);

    //List<CustomFieldLabel> NI -> List<CustomFieldOptionDTO> GA PARSE QILIB BERADI
    List<CustomFieldOptionDTO> mapCustomFieldLabelToCustomFieldOptionDTOList(List<CustomFieldLabel> customFieldLabelList);
}
