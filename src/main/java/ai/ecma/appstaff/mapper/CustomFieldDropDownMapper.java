package ai.ecma.appstaff.mapper;

import ai.ecma.appstaff.entity.customField.CustomFieldDropDown;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomFieldDropDownMapper {

    //CustomFieldOptionDTO NI -> CustomFieldDropDown GA PARSE QILADI
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customFieldId", expression = "java(customFieldId)")
    CustomFieldDropDown mapCustomFieldOptionDTOToCustomFieldDropDown(CustomFieldOptionDTO customFieldOptionDTO, @Context UUID customFieldId);

    //List<CustomFieldOptionDTO> ni -> List<CustomFieldDropDown> PARSE QILIB BERADI
    List<CustomFieldDropDown> mapCustomFieldOptionDTOToCustomFieldDropDownList(List<CustomFieldOptionDTO> customFieldOptionDTOList, @Context UUID customFieldId);

    //CustomFieldDropDown NI -> CustomFieldOptionDTO GA PARSE QILIB BERADI
    @Mapping(target = "id", expression = "java(customFieldDropDown.getId().toString())")
    CustomFieldOptionDTO mapCustomFieldDropDownToCustomFieldOptionDTO(CustomFieldDropDown customFieldDropDown);

    //List<CustomFieldDropDown> NI -> List<CustomFieldOptionDTO> GA PARSE QILIB BERADI
    List<CustomFieldOptionDTO> mapCustomFieldDropDownToCustomFieldOptionDTOList(List<CustomFieldDropDown> customFieldDropDownList);
}
