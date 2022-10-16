package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.*;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.projection.ITimeSheet;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The interface Timesheet service.
 */
public interface TimesheetService {

    /**
     * Create time sheet.
     */
    void createTimeSheet();

    /**
     * Gets all time sheet.
     *
     * @return the all time sheet
     */
    ApiResult<List<ITimeSheet>> getAllTimeSheet();

    /**
     * Time sheet confirm api result.
     *
     * @param id the id
     * @return the api result
     */
    ApiResult<?> timeSheetConfirm(Long id,UUID departmentId);

    /**
     * Add new employment info current time sheet.
     *
     * @param employmentInfoList the employment info list
     */
    void addNewEmploymentInfoCurrentTimeSheet(List<EmploymentInfo> employmentInfoList);

    /**
     * Create time sheet for department.
     *
     * @param department the department
     */
    void createTimeSheetForDepartment(Department department);

    /**
     * Gets time sheet employee list from db.
     *
     * @param TimeSheetList the time sheet list
     * @return the time sheet employee list from db
     */
    List<TimeSheetEmployee> getTimeSheetEmployeeListFromDB(List<TimeSheet> TimeSheetList);

    /**
     * Delete time sheet employee by employment info.
     *
     * @param uuidList the uuid list
     */
    void deleteTimeSheetEmployeeByEmploymentInfo(List<UUID> uuidList);

    /**
     * Update employee attendance.
     *
     * @param timeSheetEmployeeList the time sheet employee list
     * @param employeeWorkDayList   the employee work day list
     */
    void updateEmployeeAttendance(List<TimeSheetEmployee> timeSheetEmployeeList, List<EmployeeWorkDay> employeeWorkDayList);

    /**
     * Gets month all days.
     *
     * @param date the date
     * @return the month all days
     */
    List<Date> getMonthAllDays(Date date);

    /**
     * Gets current time sheet list by employment info.
     *
     * @param employmentInfoList the employment info list
     * @return the current time sheet list by employment info
     */
    List<TimeSheet> getCurrentTimeSheetListByEmploymentInfo(List<EmploymentInfo> employmentInfoList);

    /**
     * Gets view by id.
     *
     * @param viewId the view id
     * @return the view by id
     */
    ApiResult<ViewDTO> getViewById(UUID viewId);

    /**
     * Gets view types.
     *
     * @param tableName the table name
     *
     * @return the view types
     */
    ApiResult<InitialViewTypesDTO> getViewTypes(String tableName);

    /**
     * Generic view api result.
     *
     * @param page     the page
     * @param viewDTO  the view dto
     * @param statusId the status id
     * @return the api result
     */
    ApiResult<?> genericView(int page,ViewDTO viewDTO, String statusId);

    /**
     * Edit view row data api result.
     *
     * @param viewId the view id
     * @param rowId  the row id
     * @param map    the map
     * @return the api result
     */
    ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map);

    /**
     * Gets view data by id list.
     *
     * @param viewId the view id
     * @param idList the id list
     * @return the view data by id list
     */
    ApiResult<List<Map<String, Object>>> getViewDataByIdList(UUID viewId, List<String> idList);

    /**
     * Exists by department id.
     *
     * @param departmentId the department id
     */
    void existsByDepartmentId(UUID departmentId);
}
