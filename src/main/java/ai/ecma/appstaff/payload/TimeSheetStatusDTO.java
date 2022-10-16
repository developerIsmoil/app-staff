package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author IkhtiyorDev  <br/>
 * Date 22/08/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetStatusDTO {

    private UUID id = UUID.randomUUID();
    private TimeSheetStatusEnum status;
    private Long date;
    private String department;
    private UUID departmentId;

    public TimeSheetStatusDTO(TimeSheetStatusEnum status, Long date, String department,UUID departmentId) {
        this.status = status;
        this.date = date;
        this.department = department;
        this.departmentId = departmentId;
    }
}
