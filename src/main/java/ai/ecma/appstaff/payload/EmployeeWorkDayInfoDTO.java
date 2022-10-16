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
public class EmployeeWorkDayInfoDTO {

    private UUID id;
    private WeekDayEnum weekDay;
    private String weekDayTitle;
    private boolean working;
    private TimeDTO startTime;
    private TimeDTO endTime;
    private Double workingHours;
    private boolean lunch;
    private TimeDTO lunchStartTime;
    private TimeDTO lunchEndTime;
    private Double lunchHours;

    public EmployeeWorkDayInfoDTO(WeekDayEnum weekDay, String weekDayTitle, boolean working, TimeDTO startTime, TimeDTO endTime, boolean lunch, TimeDTO lunchStartTime, TimeDTO lunchEndTime) {
        this.weekDay = weekDay;
        this.weekDayTitle = weekDayTitle;
        this.working = working;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lunch = lunch;
        this.lunchStartTime = lunchStartTime;
        this.lunchEndTime = lunchEndTime;
    }

    public EmployeeWorkDayInfoDTO(UUID id, WeekDayEnum weekDay, String weekDayTitle, boolean working, TimeDTO startTime, TimeDTO endTime, boolean lunch, TimeDTO lunchStartTime, TimeDTO lunchEndTime) {
        this.id = id;
        this.weekDay = weekDay;
        this.weekDayTitle = weekDayTitle;
        this.working = working;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lunch = lunch;
        this.lunchStartTime = lunchStartTime;
        this.lunchEndTime = lunchEndTime;
    }
}
