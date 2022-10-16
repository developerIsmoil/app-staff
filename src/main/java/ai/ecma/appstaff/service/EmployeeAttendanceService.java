package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmployeeAttendance;
import ai.ecma.appstaff.entity.TariffGrid;
import ai.ecma.appstaff.entity.TimeSheet;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeAttendanceDTO;
import ai.ecma.appstaff.projection.IEmployeeAttendance;

import java.util.List;

public interface EmployeeAttendanceService {

    void changeEmployeeAttendance(EmployeeAttendanceDTO employeeAttendanceDTO);

    void calculateTimeSheetEmployeeWorkedHourAndWorkedDay(List<TimeSheet> timeSheetList);

    void calculateTimeSheetEmployeeWorkedHourAndWorkedDayAndPayment(List<TimeSheet> timeSheetList, List<TariffGrid> tariffGridList);

    List<IEmployeeAttendance> getAllByTimesheetEmployeeId(List<String> idList);
}
