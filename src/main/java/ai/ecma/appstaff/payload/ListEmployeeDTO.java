package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.GenderEnum;
import ai.ecma.appstaff.enums.MaritalStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListEmployeeDTO {

    private UUID userId;

//    private EmployeeInfoDTO employerInfo;

    private UUID id;

    // HODIMNING ISMI
    private String firstName;

    // HODIMNING FAMILIYASI
    private String lastName;

    // HODIMNING OTASINING ISMI
    private String middleName;

    // HODIMNING RASMI
    private String photoId;

    // HODIMNING TUG'ILGAN SANASI
    private Long birthDate;

    // HODIMNING OILAVIY HOLATI
    private MaritalStatusEnum maritalStatus;

    // HODIMNING JINSI
    private GenderEnum gender;

    // HODIMING EMAILI
    private String email;

    // HODIMNING TELEFON RAQAMLARI
    // BIR NECHTA BO'LISHI MUMKIN BUNDA ASOSIYSI TANLANADI VA
    // TURINI(UY TELEFONI, ISH TELEFONI, OTASINIKI, UKASINIKI) KIRITIB QO'YILADI
    private List<PhoneNumberDTO> phoneNumbers;


//    private EmployeePassportInfoDTO employeePassportInfo;

    // PASSPORT SERIYA RAQAMI (AA)
    private String passportSerial;

    // PASSPORT RAQAMI (120203)
    private String passportNumber;

    // PASSPORT BERILGAN ORGAN (Toshkent IIB)
    private String passportGivenOrganisation;

    // PASSPORT BERILGAN SANA (28/06/2018)
    private String passportGivenDate;

    // PASSPORT AMAL QILISH MUDDATI (24/06/2028)
    private Long passportExpireDate;

    // DOIMIY YASSHASH MANZILI (Shayxontohur M1)
    private String permanentAddress;

    // HOZIRGI YASSHASH MANZILI (Yunusobod Ц 5)
    private String currentAddress;

    // JSHSHR (5413 0963 1425)
    private String jshshr;


    private List<EmploymentInfoDTO> employmentInfos;

    private List<EmployeeEducationInfoDTO> employeeEducationInfos;

    private List<EmployeeExperienceInfoDTO> employeeExperienceInfos;

    private List<AttachmentDTO> attachments;

    private EmployeeSkillInfoDTO employeeSkillInfo;

}
