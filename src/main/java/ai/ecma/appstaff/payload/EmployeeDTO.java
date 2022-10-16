package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.utils.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {

    // HODIMNING ID SI
    private UUID id;

    // HODIMNIG SHAXSIY MA'LUMOTLARI.
    // FRONTDAN KELADI
    @Valid
    private EmployeeInfoDTO employerInfo;

    // HODIMNING TIZIMDA OLIB BORADIGAN VAZIFASI UCHUN (ROLE)
    // FRONTDAN KELADI
    private AccountInfoDTO accountInfo;

    // HODIMNING PASSPORT MA'LUMOTLARI
    // FRONTDAN KELADI
//    @Valid
    private EmployeePassportInfoDTO passportInfo;

    // HODIMNING ISH BILAN BANDLIGI HAQIDA MA'LUMOT (BO'LIM, LAVOZIM)
    // TIZIM UCHUN KIMLIGI
    // FRONTDAN KELADI
    @Valid
    private List<EmploymentInfoDTO> employments = new ArrayList<>();

    // HODIMNING TA'LIMI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    @Valid
    private List<EmployeeEducationInfoDTO> educations = new ArrayList<>();

    // HODIMNING TAJRIBASI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    @Valid
    private List<EmployeeExperienceInfoDTO> experiences = new ArrayList<>();

    // HODIMNING FAYLLARI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    @Valid
    private List<AttachmentDTO> attachments = new ArrayList<>();

    // HODIMNING MAHORATLARI HAQIDA MA'LUMOT
    // FRONTDAN KELADI
    private EmployeeSkillInfoDTO skill;

    // HODIMNING ASOSIY TELEFON RAQAMI
    // BU FRONTDAN KELMAYDI HAM BERILMAYDI HAM
    // ISHKI METHOD UCHUN ISHLATILGAN
    private String phoneNumber;

    // USERNING ID SI
    // BU FRONTDAN KELMAYDI HAM BERILMAYDI HAM
    // ISHKI METHOD UCHUN ISHLATILGAN
    private UUID userId;

    public EmployeeDTO(
            UUID id,
            EmployeeInfoDTO employerInfo,
            AccountInfoDTO accountInfo,
            EmployeePassportInfoDTO passportInfo,
            List<EmploymentInfoDTO> employment,
            List<EmployeeEducationInfoDTO> educations,
            List<EmployeeExperienceInfoDTO> experiences,
            List<AttachmentDTO> attachments,
            EmployeeSkillInfoDTO skill) {
        this.id = id;
        this.employerInfo = employerInfo;
        this.accountInfo = accountInfo;
        this.passportInfo = passportInfo;
        this.employments = employment;
        this.educations = educations;
        this.experiences = experiences;
        this.attachments = attachments;
        this.skill = skill;
    }

    public String getPhoneNumber() {
        for (PhoneNumberDTO phoneNumberDTO : getEmployerInfo().getPhoneNumbers()) {
            if (phoneNumberDTO.isMain()) {
                return phoneNumberDTO.getPhoneNumber();
            }
        }
        throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_NOT_SELECTED_MAIN_PHONE_NUMBER);
    }
}
