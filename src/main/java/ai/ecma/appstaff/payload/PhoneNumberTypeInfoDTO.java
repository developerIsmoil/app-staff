package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.PhoneNumberType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneNumberTypeInfoDTO {

    private UUID id;
    private String name;
    private String colorCode;

    public static PhoneNumberTypeInfoDTO fromPhoneNumberType(PhoneNumberType phoneNumberType) {

        return new PhoneNumberTypeInfoDTO(
                phoneNumberType.getId(),
                phoneNumberType.getName(),
                phoneNumberType.getColor()
        );
    }
}
