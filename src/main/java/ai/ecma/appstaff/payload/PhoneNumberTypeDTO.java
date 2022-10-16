package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.PhoneNumberType;
import ai.ecma.appstaff.enums.ActionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneNumberTypeDTO {

    private UUID id;

    @NotBlank(message = "{REQUIRED.NAME}")
    private String name;
    //    @NotBlank(message = "{REQUIRED.COLOR}")
    private String colorCode;
    @NotNull(message = "{REQUIRED.METHOD}")
    private ActionEnum method;
    private Long companyId;

    public PhoneNumberTypeDTO(UUID id, String name, String colorCode, Long companyId) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.companyId = companyId;
    }


    public static PhoneNumberTypeDTO fromPhoneNumberType(PhoneNumberType phoneNumberType) {
        return new PhoneNumberTypeDTO(
                phoneNumberType.getId(),
                phoneNumberType.getName(),
                phoneNumberType.getColor(),
                phoneNumberType.getCompanyId()
        );
    }

    public PhoneNumberTypeDTO(String name, Long companyId) {
        this.name = name;
        this.companyId = companyId;
    }
}
