package ai.ecma.appstaff.mapper;

import ai.ecma.appstaff.entity.customField.CustomFieldRating;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomFieldRatingMapper {

    //CustomFiledTypeConfigDTO NI -> CustomFieldRating GA PARSE QILIB BERADI
    @Mapping(target = "customFieldId", expression = "java(customFieldId)")
    CustomFieldRating mapCustomFiledTypeConfigDTOToCustomFieldRating(CustomFiledTypeConfigDTO customFiledTypeConfigDTO, @Context UUID customFieldId);


    //CustomFieldRating NI -> CustomFiledTypeConfigDTO GA PARSE QILIB BERADI
    CustomFiledTypeConfigDTO mapCustomFieldRatingToCustomFiledTypeConfigDTO(CustomFieldRating customFieldRating);

}
