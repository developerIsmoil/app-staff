package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.AttendanceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceDTO {

    private UUID employeeAttendanceId;
    private UUID timesheetEmployeeId;

    private AttendanceEnum attendance;

    private Double workHour;

    private Date day;

    public EmployeeAttendanceDTO(UUID timesheetEmployeeId, Double workHour, Date day) {
        this.timesheetEmployeeId = timesheetEmployeeId;
        this.workHour = workHour;
        this.day = day;
    }

    public EmployeeAttendanceDTO(UUID timesheetEmployeeId, AttendanceEnum attendance, Date day) {
        this.timesheetEmployeeId = timesheetEmployeeId;
        this.attendance = attendance;
        this.day = day;
    }
}
