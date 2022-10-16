package ai.ecma.appstaff.payload.customField;

import ai.ecma.appstaff.payload.OptionActionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CustomFiledTypeConfigDTO {


    private OptionActionDTO action;

    //AGAR TUR DROP_DOWN YOKI LABEL BO'LSA
    private List<CustomFieldOptionDTO> options;

    //AGAR TUR TREE BO'LSA UNING OPTION LARI
    private List<CustomFieldTreeOptionDTO> treeOptions;

    //AGAR COLUMN TURI TELEFON RAQAM KO'RINISHIDA BO'LSA
    private CallConfigDTO callConfig;

    //SuperLabel UCHUN CONFIG ICHIDA URL VA COLUMN NAME BO'LADI
    private SpecialLabelConfigDTO specialLabelConfig;

    //AGAR TUR CALL_DURATION BO'LSA UNING TYPE CONFIGI
    private CallDurationConfigDTO callDurationConfig;

    //MESSAGE YOZISHI UCHUN TYPE_CONFIG
    private MessageConfigDTO messageConfig;

    //RATING UCHUN CONFIG
    private RatingConfigDTO ratingConfig;

    //ATTACH MENT UCHUN CONFIG SHAPKADA URL BO'LADI
    private AttachmentConfigDTO attachmentConfig;


    //TURI CALL BO'LGAN COLUMN DA CALL TURI
    private List<CallTypeOptionDTO> callTypeOptions;

    //TUR CALL_STATUS BO'LSA SHU STATUS DAGI OPTION LAR VA ULARNING RANGI
    private List<CallStatusOptionDTO> callStatusOptions;

    public CustomFiledTypeConfigDTO(List<CustomFieldOptionDTO> options) {
        this.options = options;
    }
}
