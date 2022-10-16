package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.WeekDayEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeWorkDayDTO {

    private UUID id;

    private WeekDayEnum weekDay;

    private boolean working;

    private TimeDTO startTime;
    private TimeDTO endTime;

    private boolean lunch;
    private TimeDTO lunchStartTime;
    private TimeDTO lunchEndTime;

}
