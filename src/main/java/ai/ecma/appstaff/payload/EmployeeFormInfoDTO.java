package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.payload.feign.AttachmentFeignDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// HODIMNING SHAXSIY MA'LUMOTLARI
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeFormInfoDTO {

    // HODIMNING ISMI
    private String firstName;

    // HODIMNING FAMILIYASI
    private String lastName;

    // HODIMNING OTASINING ISMI
    private String middleName;

    // HODIMNING RASMI
    private AttachmentFeignDTO photo;

    // HODIMNING TUG'ILGAN SANASI
    private Long birthDate;

    // HODIMNING OILAVIY HOLATI
    private OptionDTO<EnumDTO> maritalStatus;

    // HODIMNING JINSI
    private OptionDTO<EnumDTO> gender;

    // HODIMING EMAILI
    private String email;

    // HODIMNING TELEFON RAQAMLARI
    // BIR NECHTA BO'LISHI MUMKIN BUNDA ASOSIYSI TANLANADI VA
    // TURINI(UY TELEFONI, ISH TELEFONI, OTASINIKI, UKASINIKI) KIRITIB QO'YILADI
    private PhoneNumberFormDTO phoneNumber;

    // HODIMGA BERILGAN IMTIYOZLAR
    private OptionDTO<PrivilegeTypeDTO> privilegeType;

    public EmployeeFormInfoDTO(OptionDTO<EnumDTO> maritalStatus, OptionDTO<EnumDTO> gender, PhoneNumberFormDTO phoneNumber, OptionDTO<PrivilegeTypeDTO> privilegeType) {
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.privilegeType = privilegeType;
    }
}
