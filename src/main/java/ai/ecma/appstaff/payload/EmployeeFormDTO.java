package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.payload.feign.AttachmentFeignDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeFormDTO {

    // HODIMNING ID SI
    private UUID id;

    // HODIMNIG SHAXSIY MA'LUMOTLARI.
    // FRONTDAN KELADI
    private EmployeeFormInfoDTO employerInfo;

    // HODIMNING TIZIMDA OLIB BORADIGAN VAZIFASI UCHUN (ROLE)
    // FRONTDAN KELADI
    private AccountFormInfoDTO accountInfo;

    // HODIMNING PASSPORT MA'LUMOTLARI
    // FRONTDAN KELADI
    private EmployeePassportInfoDTO passportInfo;

    // HODIMNING ISH BILAN BANDLIGI HAQIDA MA'LUMOT (BO'LIM, LAVOZIM)
    // TIZIM UCHUN KIMLIGI
    // FRONTDAN KELADI
    private List<EmploymentFormInfoDTO> employments = new ArrayList<>();

    // HODIMNING TA'LIMI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    private List<EmployeeEducationFormInfoDTO> educations = new ArrayList<>();

    // HODIMNING TAJRIBASI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    private List<EmployeeExperienceInfoDTO> experiences = new ArrayList<>();

    // HODIMNING FAYLLARI HAQIDAGI MA'LUMOT
    // FRONTDAN KELADI
    private List<AttachmentFeignDTO> attachments = new ArrayList<>();

    // HODIMNING MAHORATLARI HAQIDA MA'LUMOT
    // FRONTDAN KELADI
    private OptionDTO<SkillDTO> skill;

    private Boolean resignation;
    private Long resignationDate;
    private String resignationDescription;

    public EmployeeFormDTO(EmployeeFormInfoDTO employerInfo,
                           AccountFormInfoDTO accountInfo,
                           EmployeePassportInfoDTO passportInfo,
                           List<EmploymentFormInfoDTO> employment,
                           List<EmployeeEducationFormInfoDTO> educations,
                           List<EmployeeExperienceInfoDTO> experiences,
                           List<AttachmentFeignDTO> attachments,
                           OptionDTO<SkillDTO> skill,
                           Boolean resignation,
                           Long resignationDate,
                           String resignationDescription

    ) {
        this.employerInfo = employerInfo;
        this.accountInfo = accountInfo;
        this.passportInfo = passportInfo;
        this.employments = employment;
        this.educations = educations;
        this.experiences = experiences;
        this.attachments = attachments;
        this.skill = skill;
        this.resignation = resignation;
        this.resignationDate = resignationDate;
        this.resignationDescription = resignationDescription;
    }
    public EmployeeFormDTO(EmployeeFormInfoDTO employerInfo,
                           AccountFormInfoDTO accountInfo,
                           EmployeePassportInfoDTO passportInfo,
                           List<EmploymentFormInfoDTO> employment,
                           List<EmployeeEducationFormInfoDTO> educations,
                           List<EmployeeExperienceInfoDTO> experiences,
                           List<AttachmentFeignDTO> attachments,
                           OptionDTO<SkillDTO> skill

    ) {
        this.employerInfo = employerInfo;
        this.accountInfo = accountInfo;
        this.passportInfo = passportInfo;
        this.employments = employment;
        this.educations = educations;
        this.experiences = experiences;
        this.attachments = attachments;
        this.skill = skill;
    }
}
