package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.*;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeAttendanceDTO;
import ai.ecma.appstaff.payload.TimeSheetStatusDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.feign.ConfirmTimeSheetEmployeeDTO;
import ai.ecma.appstaff.payload.feign.ConfirmedTimeSheetDTO;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.projection.IEmployeeAttendance;
import ai.ecma.appstaff.projection.ITimeSheet;
import ai.ecma.appstaff.repository.*;
import ai.ecma.appstaff.repository.view.ViewObjectRepository;

import ai.ecma.appstaff.service.customField.CustomFieldValueService;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ai.ecma.appstaff.utils.CommonUtils.getThisMonthFirstDay;

/**
 * The type Timesheet service.
 */
@Slf4j
@Service
public class TimesheetServiceImpl implements TimesheetService {

    private final ViewService viewService;
    private final FeignService feignService;
    private final WorkDayRepository workDayRepository;
    private final HolidayRepository holidayRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final TariffGridRepository tariffGridRepository;
    private final ViewObjectRepository viewObjectRepository;
    private final DepartmentRepository departmentRepository;
    private final CustomFieldValueService customFieldValueService;
    private final EmploymentInfoRepository employmentInfoRepository;
    private final EmployeeAttendanceService employeeAttendanceService;
    private final EmployeeWorkDayRepository employeeWorkDayRepository;
    private final TimeSheetEmployeeRepository timeSheetEmployeeRepository;
    private final EmployeeAttendanceRepository employeeAttendanceRepository;

    /**
     * The constant objectMapper.
     */
    public static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The constant typeReference.
     */
    public static final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
    };

    /**
     * Instantiates a new Timesheet service.
     *
     * @param timeSheetRepository          the time sheet repository
     * @param holidayRepository            the holiday repository
     * @param workDayRepository            the work day repository
     * @param tariffGridRepository         the tariff grid repository
     * @param timeSheetEmployeeRepository  the time sheet employee repository
     * @param departmentRepository         the department repository
     * @param employmentInfoRepository     the employment info repository
     * @param employeeWorkDayRepository    the employee work day repository
     * @param employeeAttendanceRepository the employee attendance repository
     * @param feignService                 the feign service
     * @param employeeAttendanceService    the employee attendance service
     * @param viewService                  the view service
     * @param viewObjectRepository         the view object repository
     * @param customFieldValueService      the custom field value service
     */
    @Autowired
    public TimesheetServiceImpl(TimeSheetRepository timeSheetRepository,
                                HolidayRepository holidayRepository,
                                WorkDayRepository workDayRepository,
                                TariffGridRepository tariffGridRepository,
                                TimeSheetEmployeeRepository timeSheetEmployeeRepository,
                                DepartmentRepository departmentRepository,
                                EmploymentInfoRepository employmentInfoRepository,
                                EmployeeWorkDayRepository employeeWorkDayRepository,
                                EmployeeAttendanceRepository employeeAttendanceRepository,
                                FeignService feignService,
                                EmployeeAttendanceService employeeAttendanceService,
                                @Lazy ViewService viewService,
                                ViewObjectRepository viewObjectRepository,
                                CustomFieldValueService customFieldValueService) {
        this.timeSheetRepository = timeSheetRepository;
        this.holidayRepository = holidayRepository;
        this.workDayRepository = workDayRepository;
        this.tariffGridRepository = tariffGridRepository;
        this.timeSheetEmployeeRepository = timeSheetEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.employmentInfoRepository = employmentInfoRepository;
        this.employeeWorkDayRepository = employeeWorkDayRepository;
        this.employeeAttendanceRepository = employeeAttendanceRepository;
        this.feignService = feignService;
        this.employeeAttendanceService = employeeAttendanceService;
        this.viewService = viewService;
        this.viewObjectRepository = viewObjectRepository;
        this.customFieldValueService = customFieldValueService;
    }

    @Transactional
    @Override
    public void createTimeSheet() {

        TimeSheetStatusEnum timeSheetStatus = TimeSheetStatusEnum.OPENED;
        Date monthFirstDay = getThisMonthFirstDay();

        List<Department> departmentList = departmentRepository.findAllDepartmentNotCreateTimeSheet(
                timeSheetStatus.name(),
                monthFirstDay);

        List<TimeSheet> timeSheetList = departmentList
                .stream()
                .map(department -> new TimeSheet(
                        department,
                        timeSheetStatus,
                        monthFirstDay
                ))
                .collect(Collectors.toList());

        timeSheetRepository.saveAll(timeSheetList);

        createTimeSheetEmployee(timeSheetList, departmentList);

        for (TimeSheet timeSheet : timeSheetList) {
            createViewForTimesheet(timeSheet);
        }
    }

    @Override
    public ApiResult<List<ITimeSheet>> getAllTimeSheet() {

        boolean viewAllDepartmentTimeSheet = CommonUtils.havePermission(new PermissionEnum[]{PermissionEnum.MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS});

        List<ITimeSheet> iTimeSheetList;

        if (viewAllDepartmentTimeSheet) {

            iTimeSheetList = timeSheetRepository.getAllTimeSheetForAllDepartmentsManageUser();

        } else {

            List<UUID> departmentIdList = getCurrentUserDepartmentIdList();
            iTimeSheetList = timeSheetRepository.getAllTimeSheetForSomeDepartmentsManageUser(departmentIdList);

        }

        return ApiResult.successResponse(iTimeSheetList);

    }

    @Override
    public ApiResult<?> timeSheetConfirm(Long id, UUID departmentId) {

        log.info("timeSheetConfirm id {}, departmentId {} ", id, departmentId);
        Date date = new Date(id);

        checkConfirmationDateIsValid(date);

        boolean isManageAllDepartmentTimeSheet = CommonUtils.havePermission(
                new PermissionEnum[]{PermissionEnum.MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS}
        );

        boolean isManageTimesheetFinance = CommonUtils.havePermission(
                new PermissionEnum[]{PermissionEnum.MANAGE_TIME_SHEET_FINANCE}
        );

        log.info("timeSheetConfirm isManageAllDepartmentTimeSheet {} ", isManageAllDepartmentTimeSheet);

        List<TimeSheet> timeSheetList = getTimeSheetListByDateAndPermission(date, isManageTimesheetFinance, isManageAllDepartmentTimeSheet, departmentId);
        log.info("timeSheetConfirm timeSheetList {} ", timeSheetList.size());
        for (TimeSheet timeSheet : timeSheetList) {

            // today
            timeSheet.setConfirmDate(new Date());


            if (isManageAllDepartmentTimeSheet) {

                timeSheet.setTimeSheetStatus(TimeSheetStatusEnum.CONFIRM_HR);

            } else if (isManageTimesheetFinance) {

                timeSheet.setTimeSheetStatus(TimeSheetStatusEnum.CONFIRMED);

            } else {

                timeSheet.setTimeSheetStatus(TimeSheetStatusEnum.CONFIRM_HEAD_OF_DEPARTMENT);

            }

        }

        timeSheetRepository.saveAll(timeSheetList);

        if (isManageTimesheetFinance) {
            confirmedTimeSheetSendToFinanceService(timeSheetList);
        }

        return ApiResult.successResponse();
    }

    @Override
    public void addNewEmploymentInfoCurrentTimeSheet(List<EmploymentInfo> employmentInfoList) {

        List<TimeSheet> timeSheetList = getCurrentTimeSheetListByEmploymentInfo(employmentInfoList);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // HOZIRDA TIZIMDA MAVJUD VA AKTIV TARIF SETKALARINI OLISH UCHUN

        List<TimeSheetEmployee> timeSheetEmployeeList = createTimeSheetEmployeeList(timeSheetList, employmentInfoList);

        List<EmployeeWorkDay> employeeWorkDayList = employeeWorkDayRepository.findAllByEmploymentInfoIn(employmentInfoList);

        List<TariffGrid> tariffGridList = tariffGridRepository.findAllByActiveTrue(sortByColumn);

        if (!timeSheetEmployeeList.isEmpty()) {

            timeSheetEmployeeRepository.saveAll(timeSheetEmployeeList);
            createEmployeeAttendance(timeSheetEmployeeList, employeeWorkDayList);

//            employeeAttendanceService.calculateTimeSheetEmployeeWorkedHourAndWorkedDay(timeSheetList);
            employeeAttendanceService.calculateTimeSheetEmployeeWorkedHourAndWorkedDayAndPayment(timeSheetList, tariffGridList);
        }
    }

    @Override
    public void createTimeSheetForDepartment(Department department) {

        TimeSheetStatusEnum opened = TimeSheetStatusEnum.OPENED;
        Date monthFirstDay = getThisMonthFirstDay();

        boolean exists = timeSheetRepository.existsByTimeSheetStatusAndDepartmentAndDate(
                opened,
                department,
                monthFirstDay
        );

        if (!exists) {
            TimeSheet timeSheet = new TimeSheet(
                    department,
                    opened,
                    monthFirstDay
            );

            timeSheetRepository.save(timeSheet);

            createViewForTimesheet(timeSheet);
        }

    }

    @Override
    public List<TimeSheetEmployee> getTimeSheetEmployeeListFromDB(List<TimeSheet> timeSheetList) {
        return timeSheetEmployeeRepository.findAllByTimeSheetIn(timeSheetList);
    }

    @Override
    public void deleteTimeSheetEmployeeByEmploymentInfo(List<UUID> uuidList) {

        try {

            employeeAttendanceRepository.deleteAllByTimeSheetEmployeeByEmploymentInfoIdIn(uuidList);

            for (UUID uuid : uuidList) {
                employeeWorkDayRepository.deleteAllByEmploymentInfoId(uuid);
                timeSheetEmployeeRepository.deleteAllByEmploymentInfo_Id(uuid);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_TIMESHEET_EMPLOYEE);
        }

    }

    private void confirmedTimeSheetSendToFinanceService(List<TimeSheet> timeSheetList) {
        log.info("confirmedTimeSheetSendToFinanceService timeSheetList {}", timeSheetList.size());
        List<TimeSheetEmployee> timeSheetEmployeeList = getTimeSheetEmployeeListFromDB(timeSheetList);

        log.info("confirmedTimeSheetSendToFinanceService  timeSheetEmployeeList {}", timeSheetEmployeeList.size());
        List<ConfirmedTimeSheetDTO> confirmedTimeSheetDTOList = timeSheetList
                .stream()
                .map(timeSheet -> new ConfirmedTimeSheetDTO(
                        timeSheet.getTimeSheetStatus(),
                        timeSheet.getDate(),
                        timeSheetEmployeeList
                                .stream()
                                .filter(timeSheetEmployee -> Objects.equals(timeSheetEmployee.getTimeSheet().getId(), timeSheet.getId()))
                                .map(ConfirmTimeSheetEmployeeDTO::fromTimeSheetEmployee)
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        log.info("confirmedTimeSheetSendToFinanceService  confirmedTimeSheetDTOList {}", confirmedTimeSheetDTOList.size());
        log.info("confirmedTimeSheetSendToFinanceService  confirmedTimeSheetDTOList {}", confirmedTimeSheetDTOList);
        feignService.confirmedTimeSheetSendToFinanceService(confirmedTimeSheetDTOList);

    }

    private void checkCanConfirmTimeSheetIfNotThrowFullManageUser(List<TimeSheet> timeSheetList) {

        for (TimeSheet timeSheet : timeSheetList) {
            boolean matched = Objects.equals(timeSheet.getTimeSheetStatus(), TimeSheetStatusEnum.OPENED);

            if (matched) {

                String errorMessage = String.format(ResponseMessage.ERROR_CAN_NOT_CONFIRM_TIMESHEET_BEFORE_HEAD_OF_DEPARTMENT, timeSheet.getDepartment().getName());

                throw RestException.restThrow(errorMessage);
            }
        }
    }

    private void checkCanConfirmTimeSheetIfNotThrow(List<TimeSheet> timeSheetList, TimeSheetStatusEnum timeSheetStatusEnum) {

        for (TimeSheet timeSheet : timeSheetList) {
            boolean matched = !Objects.equals(timeSheet.getTimeSheetStatus(), timeSheetStatusEnum);

            if (matched) {

                String errorMessage = String.format(ResponseMessage.ERROR_CAN_NOT_CONFIRM_DEPARTMENT_STATUS, timeSheetStatusEnum.name());

                throw RestException.restThrow(errorMessage);
            }
        }
    }

    private void checkConfirmationDateIsValid(Date date) {

        Date today = new Date();
        Date givenDateMonthLastDay = CommonUtils.getGivenDateMonthLastDay(date);

        //todo messageni to'g'irla
        if (givenDateMonthLastDay.getTime() > today.getTime()) {
            throw RestException.restThrow(ResponseMessage.ERROR_TIMESHEET_NOT_CONFIRMING_TODAY);
        }

    }

    private List<Department> getCurrentUserDepartmentList() {
        UUID userId = CommonUtils.getCurrentUser().getId();

//        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllByManageTableIsTrueAndEmployee_UserId(userId);

        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllByEmployee_UserId(userId);

        employmentInfoList = employmentInfoList
                .stream()
                .filter(employmentInfo -> Objects.equals(Boolean.TRUE, employmentInfo.getPosition().getManageTimesheet()))
                .collect(Collectors.toList());

        List<Department> departmentList = employmentInfoList
                .stream()
                .map(EmploymentInfo::getDepartment)
                .collect(Collectors.toList());

        if (departmentList.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_DEPARTMENT_FOR_MANAGE);
        }

        return departmentList;
    }

    private List<UUID> getCurrentUserDepartmentIdList() {

        List<Department> departmentList = getCurrentUserDepartmentList();

        return departmentList
                .stream()
                .map(AbsUUIDUserAuditEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * @param timeSheetList  BU TEPADA YARATIB OLINGAN TimeSheetLAR RO'YXATI (YANGI HALI HODIMLARI YO'Q BO'LGAN TimeSheetLAR) a
     * @param departmentList TIMESHEET YARATILGAN DEPARTMENTLAR a
     */
    private void createTimeSheetEmployee(List<TimeSheet> timeSheetList, List<Department> departmentList) {

        // TIZIMDA ISHLAB TURGAN BARCHA SHU DEPARTMENTGA TEGISHLI HODIMLARNI OLISH UCHUN
        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllByDepartmentInAndEmployerStatus(departmentList, EmployerStatusEnum.WORKING);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);

//        List<TimeSheetEmployee> timeSheetEmployeeList = createTimeSheetEmployeeList(
//                timeSheetList,
//                employmentInfoList,
//                tariffGridList
//        );

        List<TimeSheetEmployee> timeSheetEmployeeList = createTimeSheetEmployeeList(
                timeSheetList,
                employmentInfoList
        );

        List<EmployeeWorkDay> employeeWorkDayList = employeeWorkDayRepository.findAllByEmploymentInfoIn(employmentInfoList);

        timeSheetEmployeeRepository.saveAll(timeSheetEmployeeList);
        createEmployeeAttendance(timeSheetEmployeeList, employeeWorkDayList);

        // HOZIRDA TIZIMDA MAVJUD VA AKTIV TARIF SETKALARINI OLISH UCHUN
        List<TariffGrid> tariffGridList = tariffGridRepository.findAllByActiveTrue(sortByColumn);

        employeeAttendanceService.calculateTimeSheetEmployeeWorkedHourAndWorkedDayAndPayment(timeSheetList, tariffGridList);

    }

    /**
     * Create time sheet employee list list.
     *
     * @param TimeSheetList      the time sheet list
     * @param employmentInfoList the employment info list
     * @param tariffGridList     the tariff grid list
     * @return the list
     */
    public List<TimeSheetEmployee> createTimeSheetEmployeeList(List<TimeSheet> TimeSheetList, List<EmploymentInfo> employmentInfoList, List<TariffGrid> tariffGridList) {
        List<TimeSheetEmployee> timeSheetEmployeeList = new ArrayList<>();

        for (TimeSheet timeSheet : TimeSheetList) {

            // TEPADAGI HODIMLARNI AYLANIB CHIQILGAN
            // MAQSAD:
            for (EmploymentInfo employmentInfo : employmentInfoList) {

                // TimeSheetDAGI DEPARTMENT BILAN HODIMNING DEPARTMENTI BIR XIL BO'LGANLARINI AJRATIB OLGANMIZ
                if (Objects.equals(timeSheet.getDepartment().getId(), employmentInfo.getDepartment().getId())) {

                    // TARIF SETKASINI AYLANIB CHIQILGAN
                    // MAQSAD: HODIMGA BERILADIGAN OYLIK MAOSHNI TARIF GRIDDAN CHIQARIB OLIB
                    // UNI TIMESHEET BILAN HODIM BIRLASHGAN TABLE {TIMESHEET_EMPLOYEE} GA YOZIB QO'YISH UCHUN.
                    // BUNDA HODIMNI AYNI BIR TimeSheetGA BIRLASHTIRDIK DESAK BO'LADI
                    for (TariffGrid tariffGrid : tariffGridList) {

                        // TARIF SETKASIDAGI MA'LUMOTLAR VA HODIM MA'LUMOTLARINI O'ZARO TEKSHIRIB BIR BIRIGA MOS KELGANINI TOPISH UCHUN
                        if (Objects.equals(employmentInfo.getDepartment().getId(), tariffGrid.getDepartment().getId())
                                && Objects.equals(employmentInfo.getPosition().getId(), tariffGrid.getPosition().getId())
                                && Objects.equals(employmentInfo.getBranchId(), tariffGrid.getBranchId())
                                && Objects.equals(employmentInfo.getEmployeeCategory().getId(), tariffGrid.getEmployeeCategory().getId())
                                && Objects.equals(employmentInfo.getPaymentCriteriaType(), tariffGrid.getPaymentCriteriaType())) {

                            // AGAR TARIF SETKASIGA SHU HODIM UCHUN MOS KELADIGAN TARIF MAVJUD BO'LSA
                            // UNDAGI HODIMGA TO'LANADIGAN SUMMANI SHU HODIM BILAN TIMESHEET BIRIKKAN TimeSheetEMPLOYEE GA YOZIB QO'YAMIZ
                            timeSheetEmployeeList.add(
                                    new TimeSheetEmployee(
                                            employmentInfo,
                                            timeSheet,
                                            tariffGrid.getPaymentAmount()
                                    )
                            );
                        }
                    }
                }
            }
        }

        return timeSheetEmployeeList;
    }

    public List<TimeSheetEmployee> createTimeSheetEmployeeList(List<TimeSheet> TimeSheetList, List<EmploymentInfo> employmentInfoList) {
        List<TimeSheetEmployee> timeSheetEmployeeList = new ArrayList<>();

        for (TimeSheet timeSheet : TimeSheetList) {

            // TEPADAGI HODIMLARNI AYLANIB CHIQILGAN
            // MAQSAD:
            for (EmploymentInfo employmentInfo : employmentInfoList) {

                // TimeSheetDAGI DEPARTMENT BILAN HODIMNING DEPARTMENTI BIR XIL BO'LGANLARINI AJRATIB OLGANMIZ
                if (Objects.equals(timeSheet.getDepartment().getId(), employmentInfo.getDepartment().getId())) {

                    // TARIF SETKASINI AYLANIB CHIQILGAN
                    // MAQSAD: HODIMGA BERILADIGAN OYLIK MAOSHNI TARIF GRIDDAN CHIQARIB OLIB
                    // UNI TIMESHEET BILAN HODIM BIRLASHGAN TABLE {TIMESHEET_EMPLOYEE} GA YOZIB QO'YISH UCHUN.
                    // BUNDA HODIMNI AYNI BIR TimeSheetGA BIRLASHTIRDIK DESAK BO'LADI

                    // AGAR TARIF SETKASIGA SHU HODIM UCHUN MOS KELADIGAN TARIF MAVJUD BO'LSA
                    // UNDAGI HODIMGA TO'LANADIGAN SUMMANI SHU HODIM BILAN TIMESHEET BIRIKKAN TimeSheetEMPLOYEE GA YOZIB QO'YAMIZ
                    timeSheetEmployeeList.add(
                            new TimeSheetEmployee(
                                    employmentInfo,
                                    timeSheet
                            )
                    );
                }
            }
        }

        return timeSheetEmployeeList;
    }

    /**
     * @param timeSheetEmployeeList a
     * @param employeeWorkDayList   a
     */
    private void createEmployeeAttendance(List<TimeSheetEmployee> timeSheetEmployeeList, List<EmployeeWorkDay> employeeWorkDayList) {

        // MA'LUM VAQT ORALIG'IDAGI BAYRAMLARNI OLISH UCHUN
        List<Holiday> holidayList = holidayRepository.findAllByFromAndToDate(
                CommonUtils.getMonthDate(RestConstants.FIRST),
                CommonUtils.getMonthDate(RestConstants.LAST)
        );

        List<Date> notPaidHolidayList = getNotPaidHolidays(holidayList)
                .stream()
                .map(CommonUtils::convertLocalDateToDate)
                .collect(Collectors.toList());

        List<Date> dateList = getMonthAllDays();
//        List<Date> dateLists = getMonthAllDays(new Date());

        // agar pustoy qayts bo'sh array aks holda mavjud attendancelar bo'ladi
        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceRepository.findAllByTimeSheetEmployeeInAndDayIn(timeSheetEmployeeList, dateList);

        boolean editing = false;

        if (!employeeAttendanceList.isEmpty()) {
            editing = true;
        }

        for (Date date : dateList) {

            for (TimeSheetEmployee timeSheetEmployee : timeSheetEmployeeList) {

                List<EmployeeWorkDay> workDayList = employeeWorkDayList
                        .stream()
                        .filter(
                                employeeWorkDay -> Objects.equals(timeSheetEmployee.getEmploymentInfo(), employeeWorkDay.getEmploymentInfo()))
                        .collect(Collectors.toList());

                for (EmployeeWorkDay employeeWorkDay : workDayList) {

                    LocalDate localDate = CommonUtils.convertDateToLocalDate(date.getTime());

                    if (Objects.equals(localDate.getDayOfWeek().name(), employeeWorkDay.getWeekDay().name())) {

                        boolean working = employeeWorkDay.isWorking();

                        EmployeeAttendance employeeAttendance;

                        if (editing) {

                            long today = new Date().getTime();
                            long time = date.getTime();

                            if (today > time) {
                                continue;
                            }

                            employeeAttendance = employeeAttendanceList
                                    .stream()
                                    .filter(e -> CommonUtils.compareTwoDateWithStr(e.getDay(), date) && Objects.equals(e.getTimeSheetEmployee(), timeSheetEmployee))
                                    .findAny()
                                    .orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_ATTENDANCE_NOT_FOUND));
                        } else {
                            employeeAttendance = new EmployeeAttendance();
                        }

                        employeeAttendance.setTimeSheetEmployee(timeSheetEmployee);
                        employeeAttendance.setAttendance(getAttendanceEnumByDate(date, notPaidHolidayList, working, employeeWorkDay));
                        employeeAttendance.setDay(date);
                        employeeAttendance.setWorkHour(working ? employeeWorkDay.getWorkingHours() : 0d);
                        employeeAttendance.setMonth(CommonUtils.getThisMonth());
                        employeeAttendance.setYear(CommonUtils.getThisYear());


                        employeeAttendanceList.add(employeeAttendance);
                    }

                }
            }
        }

        employeeAttendanceRepository.saveAll(employeeAttendanceList);

    }

    @Override
    public void updateEmployeeAttendance(List<TimeSheetEmployee> timeSheetEmployeeList, List<EmployeeWorkDay> employeeWorkDayList) {

        createEmployeeAttendance(timeSheetEmployeeList, employeeWorkDayList);

    }

    /**
     * Gets attendance enum by date.
     *
     * @param date               the date
     * @param notPaidHolidayList the not paid holiday list
     * @param isWork             the is work
     * @param employeeWorkDay    the employee work day
     * @return the attendance enum by date
     */
    public AttendanceEnum getAttendanceEnumByDate(Date date, List<Date> notPaidHolidayList, boolean isWork, EmployeeWorkDay employeeWorkDay) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(date);

        for (Date day : notPaidHolidayList) {
            String strDay = formatter.format(day);

            if (strDate.equals(strDay)) {
                employeeWorkDay.setWorkingHours(0d);
                return AttendanceEnum.HOLIDAY;
            }
        }

        if (isWork) {
            return AttendanceEnum.WORKING;
        } else {
            return AttendanceEnum.NOT_WORKING;
        }

    }

    /**
     * @param holidayList a
     * @return a
     */
    private List<Date> countBusinessDaysBetween(List<Holiday> holidayList) {

        List<DayOfWeek> dayOfWeekList = workDayRepository.findAllByActiveFalse()
                .stream()
                .map(workDay -> DayOfWeek.valueOf(workDay.getWeekDay().name()))
                .collect(Collectors.toList());

        List<LocalDate> holidays = getNotPaidHolidays(holidayList);

        List<LocalDate> localDateList = countBusinessDaysBetween(
                CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.FIRST)),
                CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.LAST)),
                Optional.of(holidays),
                dayOfWeekList);

        List<Date> dateList = new ArrayList<>();

        for (LocalDate localDate : localDateList) {
            dateList.add(CommonUtils.convertLocalDateToDate(localDate));
        }

        return dateList;
    }

    /**
     * @return a
     */
    public List<Date> getMonthAllDays(Date date) {
        LocalDate startDate = CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.FIRST, date));
        LocalDate endDate = CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.LAST, date));
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        numOfDaysBetween++;
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(startDate::plusDays)
                .map(CommonUtils::convertLocalDateToDate)
                .collect(Collectors.toList());
    }

    /**
     * @return a
     */
    private List<Date> getMonthAllDays() {
        LocalDate startDate = CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.FIRST));
        LocalDate endDate = CommonUtils.convertDateToLocalDate(CommonUtils.getMonthDate(RestConstants.LAST));

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        numOfDaysBetween++;
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(startDate::plusDays)
                .map(CommonUtils::convertLocalDateToDate)
                .collect(Collectors.toList());
    }

    /**
     * OYLIK MAOSH BERILMAYDIGAN BAYRAMLARNI AJRATIB OLISH UCHUN
     *
     * @param holidayList a
     * @return a
     */
    private List<LocalDate> getNotPaidHolidays(List<Holiday> holidayList) {

        List<LocalDate> holidays = new ArrayList<>();

        for (Holiday holiday : holidayList) {

            Set<Date> dateList = holiday.getDateList();

            for (Date date : dateList) {
                if (!holiday.isCalcMonthlySalary()) {
                    LocalDate localDate = CommonUtils.convertDateToLocalDate(date);
                    holidays.add(localDate);
                }
            }
        }

        return holidays;
    }

    /**
     * @param startDate     a
     * @param endDate       a
     * @param holidays      a
     * @param dayOfWeekList a
     * @return a
     */
    private List<LocalDate> countBusinessDaysBetween(LocalDate startDate, LocalDate endDate, Optional<List<LocalDate>> holidays, List<DayOfWeek> dayOfWeekList) {
        // Validate method arguments
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween (" + startDate + "," + endDate + "," + holidays + ")");
        }

        // Predicate 1: Is a given date is a holiday
        Predicate<LocalDate> isHoliday = date -> holidays.isPresent() && holidays.get().contains(date);

        // Predicate 2: Is a given date is a weekday
        Predicate<LocalDate> isWeekend = date -> checkDayIsDayOfWeek(date, dayOfWeekList);

        // Get all days between two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);


        // Iterate over stream of all dates and check each day against any weekday or
        // holiday
        return Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter(isHoliday.or(isWeekend).negate()).collect(Collectors.toList());
    }

    @Override
    public List<TimeSheet> getCurrentTimeSheetListByEmploymentInfo(List<EmploymentInfo> employmentInfoList) {
        Date monthFirstDay = getThisMonthFirstDay();

        List<UUID> departmentIdList = employmentInfoList
                .stream()
                .map(employmentInfo -> employmentInfo.getDepartment().getId())
                .collect(Collectors.toList());

        return timeSheetRepository.findAllByDateAndDepartmentIdInAndTimeSheetStatusAndConfirmDateIsNull(
                monthFirstDay,
                departmentIdList,
                TimeSheetStatusEnum.OPENED

        );
    }

    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {

        ViewObject viewObject = viewService.getViewObjectByIdIfNotThrow(viewId);

        ApiResult<ViewDTO> viewById = viewService.getViewById(viewId);
        ViewDTO data = viewById.getData();

        List<ViewColumnDTO> columnDTOList = data.getColumns();

        List<Date> dateList = getMonthAllDays(viewObject.getDate());
        List<CustomFieldOptionDTO> customFieldOptionDTOList = new ArrayList<>();
        for (AttendanceEnum value : AttendanceEnum.values()) {

            customFieldOptionDTOList.add(
                    new CustomFieldOptionDTO(
                            value.name(),
                            value.name(),
                            value.getColorCode()
                    ));

        }

        for (Date date : dateList) {
            columnDTOList.add(
                    new ViewColumnDTO(
                            CommonUtils.getDateFormat(date),
                            CommonUtils.getDateFormat(date),
                            CustomFieldTypeEnum.INPUT_SELECT_DROPDOWN,
                            new CustomFiledTypeConfigDTO(customFieldOptionDTOList),
                            CommonUtils.havePermission(new PermissionEnum[]{
                                    PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ATTENDANCE,
                                    PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ATTENDANCE
                            })
                    )
            );
        }

        setTimesheetViewDTO(data);

        return viewById;
    }

    private void setTimesheetViewDTO(ViewDTO viewDTO) {

        List<TimeSheet> timeSheetList = getTimeSheetListByPermission();

        List<TimeSheetStatusDTO> timeSheetStatusDTOList = timeSheetList
                .stream()
                .map(timeSheet ->
                        new TimeSheetStatusDTO(
                                timeSheet.getTimeSheetStatus(),
                                timeSheet.getDate().getTime(),
                                Objects.isNull(timeSheet.getDepartment()) ? null : timeSheet.getDepartment().getName(),
                                Objects.isNull(timeSheet.getDepartment()) ? null : timeSheet.getDepartment().getId()

                        ))
                .collect(Collectors.toList());

        viewDTO.setTimesheet(timeSheetStatusDTOList);
    }

    /**
     * @param localDate     a
     * @param dayOfWeekList a
     * @return a
     */
    private boolean checkDayIsDayOfWeek(LocalDate localDate, List<DayOfWeek> dayOfWeekList) {

        for (DayOfWeek dayOfWeek : dayOfWeekList) {
            if (localDate.getDayOfWeek() == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create view for timesheet.
     *
     * @param timeSheet the time sheet
     */
    public void createViewForTimesheet(TimeSheet timeSheet) {

        boolean exists = viewObjectRepository.existsByDateAndTableNameAndType(
                timeSheet.getDate(),
                TableNameConstant.TIMESHEET_EMPLOYEE,
                ViewTypeEnum.TABLE
        );

        String defaultName = CommonUtils.getDateFormat(timeSheet.getDate());

        if (!exists) {
            viewService.createDefaultView(
                    false,
                    true,
                    defaultName,
                    ViewTypeEnum.TABLE,
                    TableNameConstant.TIMESHEET_EMPLOYEE,
                    TableMapList.ENTITY_FIELDS.get(TableNameConstant.TIMESHEET_EMPLOYEE),
                    Optional.of(timeSheet.getDate())
            );
        }

    }

    public ApiResult<InitialViewTypesDTO> getViewTypes(String tableName) {

        return viewService.getViewTypes(tableName);

    }

    @Override
    public ApiResult<?> genericView(int page, ViewDTO viewDTO, String statusId) {


        ApiResult<GenericViewResultDTO> result = viewService.genericView(page, viewDTO, statusId, null);

        GenericViewResultDTO resultData = result.getData();

        List<String> idList = (List<String>) resultData.getGenericResult();

//        ViewObject viewObject = getViewObject(viewDTO.getId());

        List<String> timeSheetEmployeeIdList = getTimeSheetEmployeeIdListByPermission();

        List<String> timesheetEmployeeIdListResult = timeSheetEmployeeIdList
                .stream()
                .filter(idList::contains)
                .collect(Collectors.toList());

        resultData.setGenericResult(timesheetEmployeeIdListResult);

        List<String> initialSum = viewService.genericInitialSumForTimeSheetEmployee(timesheetEmployeeIdListResult);

        try {
            Map<String, Object> mapObject;

            String objectStr = initialSum.get(0);

            mapObject = objectMapper.readValue(objectStr, typeReference);

            resultData.setInitialSum(mapObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {

        TimeSheetEmployee timeSheetEmployee = getTimeSheetEmployee(rowId);

        boolean saveOtherMethod = false;

        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();

            if (Pattern.matches(RestConstants.DATE_REGEX, key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ATTENDANCE,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ATTENDANCE
                });

                saveOtherMethod = true;
                // ATTENDANCE UCHUN
                changeEmployeeAttendance(key, value, timeSheetEmployee.getId());

            } else if (Pattern.matches(RestConstants.UUID_REGEX, key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD,
                        PermissionEnum.FINANCE_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD
                });

                saveOtherMethod = true;
                //  CUSTOM FIELD UCHUN
                customFieldValueService.addCustomFieldValue(
                        new CustomFieldValueDTO(
                                value,
                                UUID.fromString(key),
                                rowId.toString()
                        )
                );

            } else if (ColumnKey.BONUS.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT
                });

                Double premium = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setBonus(premium);

            } else if (ColumnKey.PREMIUM.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT
                });

                Double premium = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setPremium(premium);

            } else if (ColumnKey.ADVANCE_SALARY.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT
                });

                Double additionSalary = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setAdvanceSalary(additionSalary);

            } else if (ColumnKey.RETENTION_SALARY.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT
                });

                Double retentionSalary = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setRetentionSalary(retentionSalary);

            } else if (ColumnKey.ADDITION_SALARY.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT
                });

                Double additionSalary = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setAdditionSalary(additionSalary);

            } else if (ColumnKey.TAX_AMOUNT.equals(key)) {

                checkPermissionOrElseThrow(new PermissionEnum[]{
                        PermissionEnum.HRM_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT,
                        PermissionEnum.FINANCE_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT
                });

                Double taxAmount = Double.valueOf(String.valueOf(value));
                timeSheetEmployee.setTaxAmount(taxAmount);

            } else {
                throw RestException.restThrow(ResponseMessage.ERROR_INVALID_ACTION_TYPE);
            }

        }

        if (saveOtherMethod) {
            timeSheetEmployee = getTimeSheetEmployee(rowId);
        } else {
            balanceTotalSalary(timeSheetEmployee);
        }

        try {
            timeSheetEmployeeRepository.save(timeSheetEmployee);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_SAVING_TIME_SHEET_EMPLOYEE);
        }

        ApiResult<List<Map<String, Object>>> result = getViewDataByIdList(viewId, Collections.singletonList(rowId.toString()));

        List<Map<String, Object>> resultData = result.getData();
        if (!resultData.isEmpty()) {
            return ApiResult.successResponse(resultData.get(0));
        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_DATA_NOT_FOUND);
        }
    }

    @Override
    public ApiResult<List<Map<String, Object>>> getViewDataByIdList(UUID viewId, List<String> idList) {

        List<Map<String, Object>> rowData = viewService.getRowData(viewId, idList);

        List<IEmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getAllByTimesheetEmployeeId(idList);

        for (Map<String, Object> rowDatum : rowData) {
            Object id = rowDatum.get("id");

            List<IEmployeeAttendance> employeeAttendances = employeeAttendanceList
                    .stream()
                    .filter(employeeAttendance -> Objects.equals(id, employeeAttendance.getTimeSheetEmployeeIdS()))
                    .collect(Collectors.toList());

            for (IEmployeeAttendance employeeAttendance : employeeAttendances) {

                Double workHour = null;

                if (AttendanceEnum.WORKING.equals(employeeAttendance.getAttendanceEnum())) {
                    workHour = employeeAttendance.getWorkHourD();
                }

                rowDatum.put(
                        CommonUtils.getDateFormat(employeeAttendance.getDay()),
                        Objects.nonNull(workHour) ? workHour : employeeAttendance.getAttendanceEnum()
                );
            }
        }

        return ApiResult.successResponse(rowData);
    }

    @Override
    public void existsByDepartmentId(UUID departmentId) {
        boolean exists = timeSheetRepository.existsByDepartmentId(departmentId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_TIMESHEET);
        }
    }

    private List<TimeSheet> getTimeSheetListByPermission() {

        boolean isManageAllDepartmentTimeSheet = CommonUtils.havePermission(new PermissionEnum[]{PermissionEnum.MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS});

        List<TimeSheet> timeSheetList;
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);

        if (isManageAllDepartmentTimeSheet) {

            timeSheetList = timeSheetRepository.findAll(sortByColumn);
            // pastda departmentni olganman agar full dostupi bo'lsa department name null ketishi kerak frontga
            // bu null saqlanmaydi xavotir olma :)

            timeSheetList
                    .stream()
                    .filter(timeSheet -> !Objects.equals(TimeSheetStatusEnum.OPENED, timeSheet.getTimeSheetStatus()))
                    .forEach(timeSheet -> timeSheet.setDepartment(null));
        } else {
            List<Department> currentUserDepartmentList = getCurrentUserDepartmentList();
            timeSheetList = timeSheetRepository.findAllByDepartmentIn(currentUserDepartmentList, sortByColumn);

        }

        return timeSheetList;
    }

    private List<TimeSheet> getTimeSheetListByDateAndPermission(Date date, boolean isManageTimesheetFinance, boolean isManageAllDepartmentTimeSheet, UUID departmentId) {

        List<TimeSheet> timeSheetList;

        if (isManageAllDepartmentTimeSheet) {

            timeSheetList = timeSheetRepository.findAllByDate(date);
            checkCanConfirmTimeSheetIfNotThrow(timeSheetList, TimeSheetStatusEnum.CONFIRM_HEAD_OF_DEPARTMENT);
        }
        if (isManageTimesheetFinance) {

            timeSheetList = timeSheetRepository.findAllByDate(date);
            checkCanConfirmTimeSheetIfNotThrow(timeSheetList, TimeSheetStatusEnum.CONFIRM_HR);

        } else {
//            List<Department> currentUserDepartmentList = getCurrentUserDepartmentList();

            if (Objects.isNull(departmentId)) {
                throw RestException.restThrow(ResponseMessage.ERROR_REQUIRED_DEPARTMENT_ID);
            }

            timeSheetList = timeSheetRepository.findAllByDateAndDepartment_Id(
                    date,
                    departmentId
            );

            checkCanConfirmTimeSheetIfNotThrow(timeSheetList, TimeSheetStatusEnum.OPENED);

        }

        return timeSheetList;
    }

    private List<String> getTimeSheetEmployeeIdListByPermission() {

        List<TimeSheet> timeSheetList = getTimeSheetListByPermission();

        List<TimeSheetEmployee> timeSheetEmployeeList = timeSheetEmployeeRepository.findAllByTimeSheetIn(timeSheetList);

        return timeSheetEmployeeList
                .stream()
                .map(timeSheetEmployee -> timeSheetEmployee.getId().toString())
                .collect(Collectors.toList());

    }

    private ViewObject getViewObject(UUID id) {
        Optional<ViewObject> optionalViewObject = viewObjectRepository.findById(id);
        if (optionalViewObject.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_NOT_FOUND);
        }
        return optionalViewObject.get();
    }

    private TimeSheetEmployee getTimeSheetEmployee(UUID id) {
        Optional<TimeSheetEmployee> optionalTimeSheetEmployee = timeSheetEmployeeRepository.findById(id);
        if (optionalTimeSheetEmployee.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_TIMESHEET_EMPLOYEE_NOT_FOUND);
        }
        return optionalTimeSheetEmployee.get();
    }

    private void changeEmployeeAttendance(String key, Object value, UUID timeSheetEmployeeId) {

        AttendanceEnum attendanceEnum = null;
        Double workHour = null;

        boolean isEnum = false;

        try {
            boolean isAttendanceEnum = Arrays.stream(AttendanceEnum.values()).anyMatch(attendanceEnum1 -> Objects.equals(attendanceEnum1.toString(), value));

            if (isAttendanceEnum) {
                attendanceEnum = AttendanceEnum.valueOf(String.valueOf(value));
                isEnum = true;
            } else {
                workHour = Double.valueOf(String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date date = null;

        try {
            date = CommonUtils.stringToDate(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        EmployeeAttendanceDTO employeeAttendanceDTO = isEnum ?
                new EmployeeAttendanceDTO(
                        timeSheetEmployeeId,
                        attendanceEnum,
                        date
                )
                :
                new EmployeeAttendanceDTO(
                        timeSheetEmployeeId,
                        workHour,
                        date
                );

        employeeAttendanceService.changeEmployeeAttendance(
                employeeAttendanceDTO
        );
    }

    /**
     * Balance total salary.
     *
     * @param timeSheetEmployee the time sheet employee
     */
    public void balanceTotalSalary(TimeSheetEmployee timeSheetEmployee) {

        //  salary
        Double salary = timeSheetEmployee.getSalary();
        //  bonus
        Double bonus = timeSheetEmployee.getBonus();
        //  premium
        Double premium = timeSheetEmployee.getPremium();
        //  advanceSalary
        Double advanceSalary = timeSheetEmployee.getAdvanceSalary();
        //  retentionSalary
        Double retentionSalary = timeSheetEmployee.getRetentionSalary();
        //  additionSalary
        Double additionSalary = timeSheetEmployee.getAdditionSalary();
        //  totalSalary
        Double totalSalary = timeSheetEmployee.getTotalSalary();
        //  taxAmount
        Double taxAmount = timeSheetEmployee.getTaxAmount();
        //  paidSalary
        Double paidSalary = timeSheetEmployee.getPaidSalary();

        totalSalary = (salary + bonus + premium + advanceSalary + additionSalary) - retentionSalary;
        paidSalary = totalSalary - taxAmount;

        timeSheetEmployee.setTotalSalary(totalSalary);
        timeSheetEmployee.setPaidSalary(paidSalary);
    }

    private void checkPermissionOrElseThrow(PermissionEnum[] permissionEnums) {

        boolean havePermission = CommonUtils.havePermission(permissionEnums);

        if (!havePermission) {
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_HAVE_NO_SUCH_RIGHT);
        }
    }

}
