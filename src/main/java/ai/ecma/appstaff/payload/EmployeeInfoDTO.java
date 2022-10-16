package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.GenderEnum;
import ai.ecma.appstaff.enums.MaritalStatusEnum;
import ai.ecma.appstaff.utils.RestConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

// HODIMNING SHAXSIY MA'LUMOTLARI
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeInfoDTO {

    // HODIMNING ISMI
    @NotBlank(message = "REQUIRED.FIRSTNAME")
    private String firstName;

    // HODIMNING FAMILIYASI
    @NotBlank(message = "REQUIRED.LASTNAME")
    private String lastName;

    // HODIMNING OTASINING ISMI
    private String middleName;

    // HODIMNING RASMI
    private String photoId;

    // HODIMNING TUG'ILGAN SANASI
    @NotNull(message = "REQUIRED.BIRTHDATE")
    private Long birthDate;

    // HODIMNING OILAVIY HOLATI
    @NotNull(message = "REQUIRED.MARITAL.STATUS")
    private MaritalStatusEnum maritalStatus;

    // HODIMNING JINSI
    @NotNull(message = "REQUIRED.GENDER")
    private GenderEnum gender;

    // HODIMING EMAILI
    @NotBlank(message = "REQUIRED.EMAIL")
    @Pattern(regexp = RestConstants.EMAIL_REGEX, message = "ERROR.EMAIL.NOT.VALID")
    private String email;

    // HODIMNING TELEFON RAQAMLARI
    // BIR NECHTA BO'LISHI MUMKIN BUNDA ASOSIYSI TANLANADI VA
    // TURINI(UY TELEFONI, ISH TELEFONI, OTASINIKI, UKASINIKI) KIRITIB QO'YILADI
    @Valid
    private List<PhoneNumberDTO> phoneNumbers;

    // HODIMGA BERILGAN IMTIYOZLAR
    private List<UUID> privilegeTypes;

    // HODIMGA BERILGAN IMTIYOZLAR
    private List<PrivilegeTypeDTO> privilegeTypeList;

    public EmployeeInfoDTO(
            String firstName,
            String lastName,
            String middleName,
            String photoId,
            Long birthDate,
            MaritalStatusEnum maritalStatus,
            GenderEnum gender,
            String email,
            List<PhoneNumberDTO> phoneNumbers,
            List<PrivilegeTypeDTO> privilegeTypeList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.photoId = photoId;
        this.birthDate = birthDate;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.email = email;
        this.phoneNumbers = phoneNumbers;
        this.privilegeTypeList = privilegeTypeList;
    }
}
