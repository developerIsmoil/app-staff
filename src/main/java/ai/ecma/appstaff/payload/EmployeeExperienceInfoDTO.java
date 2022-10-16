package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


// HODIMNING ISH TAJRIBASI HAQIDA
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeExperienceInfoDTO {

    // ID
    private UUID id;

    // TASHKILOT NOMI
    @NotBlank(message = ResponseMessage.REQUIRED_ORGANISATION_NAME)
    private String organisationName;

    // LAVOZIMI
    @NotBlank(message = "REQUIRED.POSITION")
    private String position;

    // ISHNI BOSHLAGAN VAQTI
    @NotNull(message = "REQUIRED.STARTED.WORK.DATE")
    private Long startedWorkDate;

    // ISHDAN BO’SHAGAN VAQTI
    private Long finishedWorkDate;

    // HOZIR HAM ISHLAYDI
    private boolean notFinished;
}
