package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.WeekDayEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkDayDTO {

    private boolean working;

    private WeekDayEnum weekDay;

    private String startTime;
    private String endTime;

    private boolean lunch;

    private String lunchStartTime;
    private String lunchEndTime;

}
