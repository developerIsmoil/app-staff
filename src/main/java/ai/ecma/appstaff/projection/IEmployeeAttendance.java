package ai.ecma.appstaff.projection;

import ai.ecma.appstaff.enums.AttendanceEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface IEmployeeAttendance {

    Integer getWorkDay();

    Integer getWorkHour();

    Double getWorkHourD();

    String getTimeSheetEmployeeId();

    String getTimeSheetEmployeeIdS();

    AttendanceEnum getAttendanceEnum();

    Date getDay();
}
