package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// HODIMGA TEGISHLI TELEFON RAQAMLAR
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberDTO {

    private UUID id;

    // HODIMING TELEFON RAQAMI
    @NotBlank(message = "REQUIRED.PHONE.NUMBER")
    private String phoneNumber;

    // TURINI(UY TELEFONI, ISH TELEFONI, OTASINIKI, UKASINIKI) KIRITIB QO'YILADI
//    private String type;
    @NotNull(message = "REQUIRED.PHONE.NUMBER.TYPE.ID")
    private UUID phoneNumberTypeId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumberTypeName;

    // ASOSIY TELEFON RAQAM BO'LSA MAIN = TRUE BO'LADI
    private boolean main;

    public PhoneNumberDTO(UUID id, String phoneNumber, UUID phoneNumberTypeId, boolean main) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.phoneNumberTypeId = phoneNumberTypeId;
        this.main = main;
    }

    public PhoneNumberDTO(boolean main) {
        this.id = null;
        this.phoneNumber = null;
        this.phoneNumberTypeId = null;
        this.main = main;
    }
}
