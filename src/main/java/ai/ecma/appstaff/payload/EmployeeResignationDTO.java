package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author IkhtiyorDev  <br/>
 * Date 10/08/22
 **/


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResignationDTO {

    private UUID employeeId;
    private UUID employmentId;

    @NotNull(message = ResponseMessage.REQUIRED_DESCRIPTION)
    private String description;

    @NotNull(message = ResponseMessage.REQUIRED_RESIGNATION_DATE)
    private Long resignationDate;

    @Valid
    private List<AttachmentDTO> attachments;

}
