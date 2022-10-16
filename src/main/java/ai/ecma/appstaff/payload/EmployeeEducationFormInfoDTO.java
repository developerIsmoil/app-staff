package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// HODIMNING TA'LIM OLGAN MUASSASALARI HAQIDA
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeEducationFormInfoDTO {

    // ID
    private UUID id;

    // HODIMNING TALIM DARAJASI
    private OptionDTO<EnumDTO> studyDegree;

    // HODIM O'QIGAN MUASSASA NOMI
    private String organisationName;

    // TA’LIM YO’NALISHI
    private String studyType;

    // O’QISHNI BOSHLAGAN VAQTI
    private Long startedStudyDate;

    // O’QISHNI TUGATGAN VAQTI
    private Long finishedStudyDate;

    // HOZIR HAM O’QISA TRUE BO'LADI
    private boolean notFinished;

    public EmployeeEducationFormInfoDTO(OptionDTO<EnumDTO> studyDegree) {
        this.studyDegree = studyDegree;
    }
}
