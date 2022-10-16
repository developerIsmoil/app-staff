package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.StudyDegreeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// HODIMNING TA'LIM OLGAN MUASSASALARI HAQIDA
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeEducationInfoDTO {

    // ID
    private UUID id;

    // HODIMNING TALIM DARAJASI
    @NotNull(message = "REQUIRED.STUDY.DEGREE")
    private StudyDegreeEnum studyDegree;

    // HODIM O'QIGAN MUASSASA NOMI
    @NotBlank(message = "REQUIRED.ORGANISATION.NAME")
    private String organisationName;

    // TA’LIM YO’NALISHI
    @NotBlank(message = "REQUIRED.STUDY.TYPE")
    private String studyType;

    // O’QISHNI BOSHLAGAN VAQTI
    @NotNull(message = "REQUIRED.STARTED.STUDY.DATE")
    private Long startedStudyDate;

    // O’QISHNI TUGATGAN VAQTI
    private Long finishedStudyDate;

    // HOZIR HAM O’QISA TRUE BO'LADI
    private boolean notFinished;

}
