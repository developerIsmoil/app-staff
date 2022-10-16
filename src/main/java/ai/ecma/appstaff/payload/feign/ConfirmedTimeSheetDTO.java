package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmedTimeSheetDTO implements Serializable {

    private TimeSheetStatusEnum TimeSheetStatus;

    private Date date;

    private List<ConfirmTimeSheetEmployeeDTO> confirmTimesheetEmployeeDTOList;
}
