package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeePassportInfoDTO {

    // PASSPORT SERIYA RAQAMI (AA)
    @NotBlank(message = "REQUIRED.PASSPORT.SERIAL")
    private String passportSerial;

    // PASSPORT RAQAMI (120203)
    @NotBlank(message = "REQUIRED.PASSPORT.NUMBER")
    private String passportNumber;

    // PASSPORT BERILGAN ORGAN (Toshkent IIB)
    @NotBlank(message = "REQUIRED.PASSPORT.GIVEN.ORGANISATION")
    private String passportGivenOrganisation;

    // PASSPORT BERILGAN SANA (28/06/2018)
    @NotNull(message = "REQUIRED.PASSPORT.GIVEN.DATE")
    private Long passportGivenDate;

    // PASSPORT AMAL QILISH MUDDATI (24/06/2028)
    @NotNull(message = "REQUIRED.PASSPORT.EXPIRE.DATE")
    private Long passportExpireDate;

    // DOIMIY YASSHASH MANZILI (Shayxontohur M1)
    @NotBlank(message = "REQUIRED.PERMANENT.ADDRESS")
    private String permanentAddress;

    // HOZIRGI YASSHASH MANZILI (Yunusobod Ц 5)
    @NotBlank(message = "REQUIRED.CURRENT.ADDRESS")
    private String currentAddress;

    // JSHSHR (5413 0963 1425)
    private String personalNumber;

}
