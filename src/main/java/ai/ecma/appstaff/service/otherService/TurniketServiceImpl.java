package ai.ecma.appstaff.service.otherService;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.entity.UserDailyInOut;
import ai.ecma.appstaff.entity.UserInOut;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.enums.UserInOutEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.feign.AuthFeign;
import ai.ecma.appstaff.feign.TurniketFeign;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldTreeOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.payload.feign.turniket.TurniketDTOForMessage;
import ai.ecma.appstaff.payload.feign.turniket.TurniketUserDTO;
import ai.ecma.appstaff.payload.view.ViewColumnDTO;
import ai.ecma.appstaff.projection.EmployeeInOfficeProjection;
import ai.ecma.appstaff.projection.EmploymentInfoProjection;
import ai.ecma.appstaff.projection.EmploymentInfoTurniketProjection;
import ai.ecma.appstaff.repository.EmployeeRepository;
import ai.ecma.appstaff.repository.EmploymentInfoRepository;
import ai.ecma.appstaff.repository.UserDailyInOutRepository;
import ai.ecma.appstaff.repository.UserInOutRepository;
import ai.ecma.appstaff.service.DepartmentService;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ai.ecma.appstaff.utils.TimeApiUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurniketServiceImpl implements TurniketService {
    private final EmployeeRepository employeeRepository;
    private final TurniketFeign turniketFeign;
    private final UserInOutRepository userInOutRepository;
    private final DepartmentService departmentService;
    private final FeignService feignService;
    private final UserDailyInOutRepository userDailyInOutRepository;
    private final EmploymentInfoRepository employmentInfoRepository;
    private final AuthFeign authFeign;


    @Value("#{ T(java.time.LocalTime).parse('${employeeWorkingStartTime}')}")
    private LocalTime employeeWorkingStartTime;
    private final int defaultDaysForHistory = 9;

    private final String fullNameColumnId = "fullName";
    private final String departmentColumnId = "department";
    private final String phoneNumberColumnId = "phoneNumber";
    private final String idColumnId = "id";
    private final String dateRangeFilterName = "Vaqt oralig'i";
    private final String departmentFilterName = "Bo'lim";
    private final String companyFilterName = "Kompaniya";
    private final String lateCountExcelColumnName = "Kechikkan kunlar soni";
    private final String excelFileName = "reportTurniket.xls";
    private final short maxDayFilter = 60;


    @Override
    public ApiResult<TurniketInOutInfoDTO> getInOutData() {

        List<Employee> employeeList = getEmployeeByFilter();

        List<UserDailyInOut> userDailyInOutList = userDailyInOutRepository.findAllByDateBetweenOrderByDate(
                Date.valueOf(nowLocalDate().minusDays(defaultDaysForHistory)),
                Date.valueOf(nowLocalDate())
        );

        Map<String, ViewColumnDTO> columnMap = getColumns(
                nowLocalDate().minusDays(defaultDaysForHistory),
                nowLocalDate()
        );

        TurniketInOutInfoDTO turniketInOutInfoDTO = new TurniketInOutInfoDTO();

        Map<String, TurniketFilterDTO> filterMap = getFilters();

        List<Map<String, TurniketColumnDataDTO>> columnDataMapList = getColumnData(userDailyInOutList, employeeList);

        turniketInOutInfoDTO.setColumns(columnMap);
        turniketInOutInfoDTO.setFilters(filterMap);
        turniketInOutInfoDTO.setColumnData(columnDataMapList);

        return ApiResult.successResponse(turniketInOutInfoDTO);
    }

    @Override
    public ApiResult<TurniketInOutInfoDTO> getByFilter(List<TurniketFilterDTO> filters) {

        List<UserDailyInOut> userDailyInOutList = null;

        TurniketFilterDTO companyFilter = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        for (TurniketFilterDTO filter : filters) {

            //
            if (Objects.equals(filter.getName(), dateRangeFilterName)) {
                userDailyInOutList = getUserDailyInOutListByDateRange(filter.getValues());

                startDate = Objects.nonNull(filter.getValues()) ? getLocalDateFromStr(filter.getValues().get(0)) : nowLocalDate().minusDays(defaultDaysForHistory);
                endDate = Objects.nonNull(filter.getValues()) ? getLocalDateFromStr(filter.getValues().get(1)) : nowLocalDate();

            } else if (Objects.equals(filter.getName(), companyFilterName)) {
                companyFilter = filter;
            }
        }

        List<Employee> employeeList = getEmployees(companyFilter);

        if (Objects.isNull(employeeList) || Objects.isNull(userDailyInOutList))
            throw RestException.restThrow("Employee or daily in out is null");

        //FILTERLARNI OL
        Map<String, TurniketFilterDTO> filtersMap = getFilters(filters);

        //COLUMNLARNI OL
        Map<String, ViewColumnDTO> columnDTOMap = getColumns(startDate, endDate);

        List<Map<String, TurniketColumnDataDTO>> columnData = getColumnData(userDailyInOutList, employeeList);

        TurniketInOutInfoDTO turniketInOutInfoDTO = new TurniketInOutInfoDTO(
                filtersMap,
                columnDTOMap,
                columnData
        );

        return ApiResult.successResponse(turniketInOutInfoDTO);
    }

    @Override
    public ApiResult<UserDailyInOutDTO> getOneDayData(UUID userDailyInOutId) {
        UserDailyInOut userDailyInOut = userDailyInOutRepository.findById(userDailyInOutId).orElseThrow(() -> RestException.restThrow("User"));
        UserDailyInOutDTO userDailyInOutDTO = getUserDailyInOutDTO(getDateFormat().format(userDailyInOut.getDate()), userDailyInOut);
        return ApiResult.successResponse(userDailyInOutDTO);
    }

    @Override
    public ApiResult<UserDailyInOutDTO> getOneDayData(OneDayTurniketDTO oneDayTurniketDTO) {

        UUID employeeId = oneDayTurniketDTO.getEmployeeId();
        Date date = oneDayTurniketDTO.getDate();
        log.info("Date: {} payload : {}", date, oneDayTurniketDTO);
        UserDailyInOut userDailyInOut = userDailyInOutRepository.findByDateAndEmployeeId(date, employeeId).orElseThrow(() -> RestException.restThrow("User daily in out not found"));

        UserDailyInOutDTO userDailyInOutDTO = getUserDailyInOutDTO(getDateFormat().format(date), userDailyInOut);

        return ApiResult.successResponse(userDailyInOutDTO);
    }


    @Override
    public ResponseEntity<Resource> downloadExcel(String filters, String token) {
        try {

            checkToken(token);

            filters = new String(Base64.getDecoder().decode(filters));

            List<TurniketFilterDTO> filterDTOList = CommonUtils.objectMapper.readValue(filters, CommonUtils.typeFactory.constructCollectionType(List.class, TurniketFilterDTO.class));

            TurniketInOutInfoDTO turniketInOutInfoDTO = getByFilter(filterDTOList).getData();

            byte[] excelByteArray = generateExcel(turniketInOutInfoDTO);

            Resource resource = new ByteArrayResource(excelByteArray);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + UUID.randomUUID() + ".xls\"")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("Exception read json");
        }
    }


    @CheckAuth(permission = PermissionEnum.ADD_EMPLOYEE_IN_TURNIKET)
    @Override
    public void addUserInTurniket(Employee employee, EmployeeInfoDTO employeeInfoDTO) {

        if (Objects.isNull(employeeInfoDTO.getPhotoId()))
            return;

        Integer turniketId = getNextTurniketId();
        String name = employeeInfoDTO.getFirstName() + " " + employeeInfoDTO.getLastName();
        String gender = employeeInfoDTO.getGender().name().toLowerCase(Locale.ROOT);

        TurniketUserDTO turniketUserDTO = new TurniketUserDTO(
                turniketId,
                name,
                gender,
                employeeInfoDTO.getPhotoId()
        );

        turniketFeign.addUserInTurniket(CommonUtils.getTokenFromRequest(), turniketUserDTO);
        employee.setTurniketId(turniketId);
        employeeRepository.save(employee);
    }


    @CheckAuth(permission = PermissionEnum.EDIT_EMPLOYEE_IN_TURNIKET)
    @Override
    public void editUserInTurniket(Employee employee, EmployeeInfoDTO employeeInfoDTO) {

        Integer turniketId = employee.getTurniketId();

        if (Objects.isNull(turniketId))
            turniketId = getNextTurniketId();

        String name = employeeInfoDTO.getFirstName() + " " + employeeInfoDTO.getLastName();
        String gender = employeeInfoDTO.getGender().name().toLowerCase(Locale.ROOT);

        TurniketUserDTO turniketUserDTO = new TurniketUserDTO(
                turniketId,
                name,
                gender,
                employeeInfoDTO.getPhotoId()
        );

        turniketFeign.editUserInTurniket(CommonUtils.getTokenFromRequest(), turniketUserDTO);
        employee.setTurniketId(turniketId);
        employeeRepository.save(employee);
    }

    @CheckAuth(permission = PermissionEnum.DELETE_EMPLOYEE_IN_TURNIKET)
    @Override
    public void deleteUserFromTurniket(Employee employeeFromDB) {
        Integer turniketId = employeeFromDB.getTurniketId();
        if (Objects.isNull(turniketId))
            return;
        turniketFeign.deleteUserInTurniket(CommonUtils.getTokenFromRequest(), turniketId);
    }

    @Override
    public void getUpdates(TurniketDTOForMessage message) {

        Optional<Employee> optionalEmployee = employeeRepository.findFirstByTurniketId(message.getTurniketId());

        if (optionalEmployee.isEmpty())
            return;

        //ISHCHI
        Employee employee = optionalEmployee.get();

        //TURNIKET BERGAN VAQT
        Timestamp timestamp = message.getTimeStamp();

        Date date = Date.valueOf(timestamp.toLocalDateTime().toLocalDate());

        //BITTA ISHCHINI BUGUNGI KUN UCHUN KIRDI CHIQTISI BORINI OLADI YOKI YANGI YARATADI
        UserDailyInOut userDailyInOut = userDailyInOutRepository.findByDateAndEmployee(date, employee).orElseGet(UserDailyInOut::new);

        userDailyInOut.setEmployee(employee);
        userDailyInOut.setEmployeeId(employee.getId());
        userDailyInOut.setDate(date);

        //HOZIR OFFICEDAMI SHU ISHCHI
        userDailyInOut.setInOffice(Objects.equals(message.getStatus(), UserInOutEnum.ENTER));

        //CHIQGAN
        if (Objects.equals(message.getStatus(), UserInOutEnum.EXIT)) {

            //CHIQGANDA SHU PAYTGACHA QANCHA OFFICE DA BO'LDI HISOBLAB YOZIB QO'Y
            calculateWorkingTimes(userDailyInOut, timestamp);

            //ENG OXIRGI CHIQISHGA YOZIB QO'YAMIZ
            userDailyInOut.setLastExitTimeStamp(timestamp);
        }


        //KIRGAN
        if (Objects.equals(message.getStatus(), UserInOutEnum.ENTER)) {

            //ENG OXIRGI KIRISH VAQTI
            userDailyInOut.setLastEnterTimeStamp(timestamp);

            //BU ENG BIRINCHI KIRISH VAQTIMI
            if (Objects.isNull(userDailyInOut.getFirstEnterTimeStamp()))
                userDailyInOut.setFirstEnterTimeStamp(timestamp);

            //KECHIKDIMI
            if (nowLocalTime().isAfter(employeeWorkingStartTime))
                calculateEmployeeIsLate(userDailyInOut);
        }

        userDailyInOutRepository.save(userDailyInOut);

        UserInOut userInOut = new UserInOut(
                userDailyInOut,
                userDailyInOut.getId(),
                message.getStatus(),
                message.getTimeStamp()
        );

        userInOutRepository.save(userInOut);
    }

    private void checkToken(String token) {

        UserDTO userDTO = authFeign.checkPermission("Bearer " + token).getData();
        boolean havePermission = CommonUtils.havePermission(userDTO, new PermissionEnum[]{PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY});
        if (!havePermission)
            throw RestException.restThrow(HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN);

    }


    private UserDailyInOutDTO getUserDailyInOutDTO(String date, UserDailyInOut userDailyInOut) {
        EmployeeTurnikteInfoDTO employeeTurnikteInfoDTO = getEmployeeTurniketInfoDTO(userDailyInOut.getEmployee());

        List<UserInOutDTO> history = getInOutHistory(userDailyInOut.getUserInOuts());

        boolean isToday = isToday(userDailyInOut);

        long workingSeconds = getWorkingSeconds(userDailyInOut);

        return new UserDailyInOutDTO(
                employeeTurnikteInfoDTO,
                history,
                userDailyInOut.isLate(),
                workingSeconds,
                userDailyInOut.getFirstEnterTimeStamp(),
                userDailyInOut.getLastExitTimeStamp(),
                date,
                userDailyInOut.getDate(),
                isToday && userDailyInOut.isInOffice()
        );
    }


    private List<UserInOutDTO> getInOutHistory(List<UserInOut> userInOuts) {
        if (Objects.isNull(userInOuts) || userInOuts.isEmpty())
            return new ArrayList<>();
        return userInOuts
                .stream()
                .map(userInOut -> new UserInOutDTO(
                        userInOut.getId(),
                        userInOut.getTimestamp(),
                        userInOut.getStatus()
                ))
                .collect(Collectors.toList());
    }

    private Date getSqlDateFromString(String date) {
        try {
            java.util.Date parse = getDateFormat().parse(date);
            return new Date(parse.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            throw RestException.restThrow("Date is wrong");
        }
    }

    private EmployeeTurnikteInfoDTO getEmployeeTurniketInfoDTO(Employee employee) {

        EmployeeTurnikteInfoDTO employeeTurnikteInfoDTO = new EmployeeTurnikteInfoDTO(
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getPhoneNumber()
        );

        List<String> departmentNames = new ArrayList<>();
        List<String> positionNames = new ArrayList<>();
        List<Long> companyIds = new ArrayList<>();


        List<EmploymentInfoTurniketProjection> employmentInfoList = getEmployeeMentInfoByEmployeeId(employee.getId());

        for (EmploymentInfoTurniketProjection infoTurniketProjection : employmentInfoList) {
            departmentNames.add(infoTurniketProjection.getDepartmentName());
            positionNames.add(infoTurniketProjection.getPositionName());
            companyIds.add(infoTurniketProjection.getCompanyId());
        }

        List<String> companyNames = getCompaniesByIds(companyIds);

        employeeTurnikteInfoDTO.setDepartments(departmentNames);
        employeeTurnikteInfoDTO.setPositions(positionNames);
        employeeTurnikteInfoDTO.setCompanies(companyNames);

        String avatar = CommonUtils.getDownloadPath(employee.getPhotoId());
        employeeTurnikteInfoDTO.setAvatar(avatar);

        return employeeTurnikteInfoDTO;
    }

    private List<String> getCompaniesByIds(List<Long> companyIds) {
        if (companyIds.isEmpty())
            return new ArrayList<>();
        return feignService.getCompanyList()
                .stream()
                .filter(companyFeignDTO -> companyIds.contains(companyFeignDTO.getId()))
                .map(CompanyFeignDTO::getName)
                .collect(Collectors.toList());
    }

    private List<EmploymentInfoTurniketProjection> getEmployeeMentInfoByEmployeeId(UUID employeeId) {
        return employmentInfoRepository.getTurniketInfoFromEmployee(employeeId);
    }

    private Employee getEmployeeByIdOrElseThrow(UUID employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> RestException.restThrow("Employee not found"));
    }


    private void addSpecialHeaderToResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.ms-excel");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition",
                "attachment" + "; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "max-age=8640000");
    }

    private byte[] generateExcel(TurniketInOutInfoDTO turniketInOutInfoDTO) {

        Map<String, ViewColumnDTO> columnMap = turniketInOutInfoDTO.getColumns();

        try {

            HSSFWorkbook workbook = new HSSFWorkbook();

            //invoking creatSheet() method and passing the name of the sheet to be created
            HSSFSheet sheet = workbook.createSheet("Turniket Report");

            //HEADER SHAKLLANTIRISH
            int lateColumnNumber = writeHeader(workbook, sheet, columnMap);

            //BODY NI YOZIB CHIQAMIZ
            writeBody(workbook, sheet, turniketInOutInfoDTO, lateColumnNumber);

            FileOutputStream fileOut = new FileOutputStream(excelFileName);
            File file = new File(excelFileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            return FileCopyUtils.copyToByteArray(file);

        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("File yuklashda xatolik");
        }
    }

    //MAXSUS COLUMN KECHIKKAN KUNLAR SONI HR UCHUN
    private void addLateDayCountColumn(HSSFRow rowhead, int counter) {
        rowhead.createCell(counter).setCellValue(lateCountExcelColumnName);
    }

    private void writeBody(HSSFWorkbook workbook, HSSFSheet sheet, TurniketInOutInfoDTO turniketInOutInfoDTO, int lateColumnNumber) {

        List<Map<String, TurniketColumnDataDTO>> columnDataMapList = turniketInOutInfoDTO.getColumnData();

        Map<String, ViewColumnDTO> stickyColumnMap = getStickyColumns();
        Map<String, ViewColumnDTO> hiddenColumnMap = getHiddenColumns();
        Map<String, ViewColumnDTO> columnMap = turniketInOutInfoDTO.getColumns();

        int rowCount = 1;

        //BITTA QATORNI OLDIM
        for (Map<String, TurniketColumnDataDTO> oneRow : columnDataMapList) {

            //creating the 0th row using the createRow() method
            HSSFRow rowheadData = sheet.createRow(rowCount);

            HSSFCellStyle dataRowStyle = workbook.createCellStyle();
            dataRowStyle.setAlignment(HorizontalAlignment.CENTER);
            rowheadData.setRowStyle(dataRowStyle);

            int celCount = 0;
            int lateDayCounter = 0;

            //BITTA QATORNI COLUMNLARNI OLIB QIYMATINI QO'YAMIZ
            for (String columnId : columnMap.keySet()) {

                //BITTA YACHEYKA MA'LUMOTI
                TurniketColumnDataDTO columnDataDTO = oneRow.get(columnId);

                if (Objects.isNull(columnDataDTO)) {
                    celCount++;
                    continue;
                }

                //KECHIKKANLAR VAQTINI SANA
                if (columnDataDTO.isLate())
                    lateDayCounter++;

                //AGAR STICKY YOKI HIDDEN BO'LSA  QIYMAT STRVALUE DA YOTIBDI
                if (stickyColumnMap.containsKey(columnId) || hiddenColumnMap.containsKey(columnId))

                    rowheadData.createCell(celCount).setCellValue(columnDataDTO.getStrValue());

                else //AKS HOLDA ESA ARRIVED TIME NI OL

                    rowheadData.createCell(celCount).setCellValue(columnDataDTO.getArrivedTime());

                celCount++;

            }


            //KECHIKKAN LAR SONINI SET QILADI
            rowheadData.createCell(lateColumnNumber).setCellValue(lateDayCounter);
            rowCount++;

        }

    }


    private int writeHeader(HSSFWorkbook workbook, HSSFSheet sheet, Map<String, ViewColumnDTO> columnMap) {
        //creating the 0th row using the createRow() method
        HSSFRow rowhead = sheet.createRow((short) 0);

        HSSFCellStyle rowStyle = workbook.createCellStyle();
        rowStyle.setAlignment(HorizontalAlignment.CENTER);
        rowhead.setRowStyle(rowStyle);

        HSSFFont font = workbook.createFont();
        font.setBold(true);

        rowStyle.setFont(font);

        int counter = 0;
        for (String key : columnMap.keySet()) {
            rowhead.createCell(counter).setCellValue(key);
            counter++;
        }

        //MAXSUS COLUMN KECHIKKAN KUNLAR SONI HR UCHUN
        addLateDayCountColumn(rowhead, counter);
        return counter;
    }


    private List<Employee> getEmployees(TurniketFilterDTO companyFilter) {

        if (Objects.isNull(companyFilter) || Objects.isNull(companyFilter.getValues()))
            return getEmployeeByFilter();

        List<String> values = companyFilter.getValues();

        //FAQAT COMPANY BO'YICHA SO'RADI FAQAT
        if (values.size() == 1) {

            if (!Pattern.matches(RestConstants.NUMBER_REGEX, values.get(0)))
                throw RestException.restThrow("company id is wrong");

            return employeeRepository.findAllByCompanyId(Long.parseLong(values.get(0)));

            //FAQAT BITTA DEPARTMENTNI SO'RADI
        } else if (values.size() == 2) {

            //AGAR UUID BO'LMASA THROW
            if (!Pattern.matches(RestConstants.UUID_REGEX, values.get(1)))
                throw RestException.restThrow("department id is wrong");

            return employeeRepository.findAllByDepartmentIdForTurniket(UUID.fromString(values.get(1)));
        }

        throw RestException.restThrow("Wrong filter");
    }


    private long validateLongArray(List<String> values) {
        if (Objects.isNull(values) || values.size() != 1)
            throw RestException.restThrow("Company id array not null");
        boolean anyMatch = values
                .stream()
                .anyMatch(s -> !Pattern.matches(RestConstants.NUMBER_REGEX, s));
        if (anyMatch)
            throw RestException.restThrow("Have not number value in company id array");
        return Long.parseLong(values.get(0));
    }


    private LocalDate getLocalDateFromStr(String s) {
        log.info("date :{} ", LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(s)), ZoneId.of(RestConstants.TASHKENT_TIME_ZONE)).toLocalDate());
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(s)), ZoneId.of(RestConstants.TASHKENT_TIME_ZONE)).toLocalDate();
    }

    private List<UserDailyInOut> getUserDailyInOutListByDateRange(List<String> values) {

        LocalDate startDate;
        LocalDate endDate;

        if (Objects.nonNull(values)) {
            checkDateFilter(values);
            startDate = getLocalDateFromStr(values.get(0));
            endDate = getLocalDateFromStr(values.get(1));
        } else {
            startDate = nowLocalDate().minusDays(defaultDaysForHistory);
            endDate = nowLocalDate();
        }

        return userDailyInOutRepository.findAllByDateBetweenOrderByDate(
                Date.valueOf(startDate),
                Date.valueOf(endDate)
        );
    }


    private List<Employee> getEmployeeByFilter() {

        List<Employee> employeeList = employeeRepository.findAll(Sort.by(Employee.Fields.lastName));

        return employeeList;
    }


    private void checkDateFilter(List<String> values) {
        if (Objects.isNull(values) || values.size() != 2)
            throw RestException.restThrow("Value is null or not equal 2");

        try {
            Date startDate = new Date(Long.parseLong(values.get(0)));
            Date endDate = new Date(Long.parseLong(values.get(1)));
            checkDateIsCorrect(startDate.toLocalDate(), endDate.toLocalDate());
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow("filter value not valid");
        }
    }

    private void calculateWorkingTimes(UserDailyInOut userDailyInOut, Timestamp timestamp) {
        Timestamp lastExitTimeStamp = userDailyInOut.getLastExitTimeStamp();
        if (Objects.isNull(lastExitTimeStamp))
            lastExitTimeStamp = Objects.nonNull(userDailyInOut.getFirstEnterTimeStamp()) ? userDailyInOut.getFirstEnterTimeStamp() : timestamp;

        long duration = Math.abs(Duration.between(lastExitTimeStamp.toInstant(), timestamp.toInstant()).getSeconds());
        userDailyInOut.setWorkingTimes(userDailyInOut.getWorkingTimes() + duration);
    }


    private void calculateEmployeeIsLate(UserDailyInOut userDailyInOut) {
        log.info("Local time : {}", nowLocalTime());
        if (todayIsSunday())
            return;
        Timestamp firstEnterTimeStamp = userDailyInOut.getFirstEnterTimeStamp();
        if (Objects.isNull(firstEnterTimeStamp) || firstEnterTimeStamp.toLocalDateTime().toLocalTime().isAfter(employeeWorkingStartTime))
            userDailyInOut.setLate(true);
    }

    private boolean todayIsSunday() {
        return nowLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private List<TurniketColumnDataDTO> getStickyColumnData(Employee employee, Map<UUID, UUID> employeeDepartmentMap, Map<String, String> departmentMap, HashMap<UUID, Boolean> employeesInOfficeCurrentTimeMap) {

        List<TurniketColumnDataDTO> columns = new ArrayList<>();

        String departmentName = getDepartmenName(employee.getId(), employeeDepartmentMap, departmentMap);

        TurniketColumnDataDTO employeeDepartmentColumnData = getEmployeeDepartmentColumnData(departmentName);
        TurniketColumnDataDTO employeeFullNameColumnData = getEmployeeFullNameColumnData(employee);

        Boolean inOffice = employeesInOfficeCurrentTimeMap.get(employee.getId());
        employeeFullNameColumnData.setInOffice(Objects.equals(inOffice, Boolean.TRUE));

        columns.add(employeeFullNameColumnData);
        columns.add(employeeDepartmentColumnData);

        return columns;
    }


    private List<Map<String, TurniketColumnDataDTO>> getColumnData(List<UserDailyInOut> userDailyInOutList, List<Employee> employeeList) {

        List<Map<String, TurniketColumnDataDTO>> columnData = new ArrayList<>();

        //SELECT UCHUN DEPARTMENTLAR
        Map<String, String> departmentMap = getDepartmentOptions()
                .stream()
                .collect(Collectors.toMap(CustomFieldOptionDTO::getId, CustomFieldOptionDTO::getName));

        List<EmploymentInfoProjection> employeeDepartmentIdProjection = employmentInfoRepository.findAllEmploymentInfoByEmpIds(
                employeeList
                        .stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList())
        );

        //EMPLOYEE ID VA DEPARTMENT ID
        Map<UUID, UUID> employeeDepartmentIdMap = new LinkedHashMap<>();
        for (EmploymentInfoProjection employmentInfoProjection : employeeDepartmentIdProjection)
            employeeDepartmentIdMap.put(employmentInfoProjection.getEmployeeId(), employmentInfoProjection.getDepartmentId());

        HashMap<UUID, Boolean> employeesInOfficeCurrentTimeMap = getEmployeesInOfficeCurrentTime(employeeList);

        //HAR KUNLIK MA'LUMOTNI EMPLOYEE BO'YICHA GURUHLANDI
        Map<UUID, List<UserDailyInOut>> mapGroupByEmployee = userDailyInOutList
                .stream()
                .collect(Collectors.groupingBy(UserDailyInOut::getEmployeeId));


        //HAR BIR GURUHNI AYLANIB CHIQADI
        for (Employee employee : employeeList) {

            //BITTA EMPLOYEE NI 30 KUNLIK DATA SI clientga berish uchun
            Map<String, TurniketColumnDataDTO> oneRow = new LinkedHashMap<>();

            //BITTA EMPLOYEE NI 30 KUNLIK DATA SI
            List<UserDailyInOut> userDailyInOutOfOneEmployee = mapGroupByEmployee.get(employee.getId());

            //BOSHIDAGI STICKY COLUMN MA'LUMOTLARINI OLDIM
            List<TurniketColumnDataDTO> stickyColumnDataList = getStickyColumnData(employee, employeeDepartmentIdMap, departmentMap, employeesInOfficeCurrentTimeMap);

            //ISM FAMILIYA VA DEPARTMENT SET QILADI
            for (TurniketColumnDataDTO stickyColumnData : stickyColumnDataList)
                oneRow.put(stickyColumnData.getName(), stickyColumnData);

            //HIDDEN COLUMNLAR MA'LUMOTLARI
            List<TurniketColumnDataDTO> hiddenColumnDataList = getHiddenColumnData(employee);

            //ID VA PHONE NUMBER NI SET QILAMIZ
            for (TurniketColumnDataDTO hiddenColumnDataDTO : hiddenColumnDataList)
                oneRow.put(hiddenColumnDataDTO.getName(), hiddenColumnDataDTO);

            //AGAR USER DAILY DATA SI YO'Q EMPLOYEE BO'LSA O'TQAZIB YUBOR
            if (Objects.isNull(userDailyInOutOfOneEmployee)) {
                columnData.add(oneRow);
                continue;
            }

            fillOneRow(oneRow, userDailyInOutOfOneEmployee);

            columnData.add(oneRow);
        }

        return columnData;
    }

    private HashMap<UUID, Boolean> getEmployeesInOfficeCurrentTime(List<Employee> employeeList) {
        List<UUID> employeeIds = employeeList
                .stream()
                .map(Employee::getId)
                .collect(Collectors.toList());
        List<EmployeeInOfficeProjection> currentTimeEmployeesInOffice = userDailyInOutRepository.getCurrentTimeEmployeesInOffice(employeeIds, nowSqlDate());
        HashMap<UUID, Boolean> inOfficeMap = new HashMap<>();
        for (EmployeeInOfficeProjection employeeInOfficeProjection : currentTimeEmployeesInOffice)
            inOfficeMap.put(employeeInOfficeProjection.getEmployeeId(), employeeInOfficeProjection.getInOffice());
        return inOfficeMap;
    }

    private void fillOneRow(Map<String, TurniketColumnDataDTO> oneRow, List<UserDailyInOut> userDailyInOutOfOneEmployee) {
        //KUNLARNI SET QILYAPTI
        for (UserDailyInOut userDailyInOut : userDailyInOutOfOneEmployee) {

            //QANCHA SECOND DAN BERI ISHXONADA BO'LGAN
            long workingSeconds = getWorkingSeconds(userDailyInOut);

            boolean isToday = isToday(userDailyInOut);

            Timestamp firstEnterTimeStamp = userDailyInOut.getFirstEnterTimeStamp();

            if (Objects.isNull(firstEnterTimeStamp))
                continue;

            DateFormat dateFormat = getDateFormat();
            String name = dateFormat.format(firstEnterTimeStamp);

            TurniketColumnDataDTO columnDataDTO = new TurniketColumnDataDTO(
                    name,
                    userDailyInOut.getFirstEnterTimeStamp().toLocalDateTime().toLocalTime().format(RestConstants.TIME_FORMATTER),
                    userDailyInOut.isLate(),
                    workingSeconds,
                    userDailyInOut.isInOffice() && isToday,
                    userDailyInOut.getId().toString()
            );


            oneRow.put(columnDataDTO.getName(), columnDataDTO);
        }
    }


    private ArrayList<TurniketColumnDataDTO> getHiddenColumnData(Employee employee) {

        TurniketColumnDataDTO idColumnData = getIdColumnData(employee);

        TurniketColumnDataDTO phoneNumberColumnData = getPhoneNumberColumnData(employee);

        return new ArrayList<>(List.of(idColumnData, phoneNumberColumnData));
    }

    private TurniketColumnDataDTO getPhoneNumberColumnData(Employee employee) {
        return new TurniketColumnDataDTO(
                phoneNumberColumnId,
                employee.getPhoneNumber()
        );
    }

    private TurniketColumnDataDTO getIdColumnData(Employee employee) {
        return new TurniketColumnDataDTO(
                "id",
                employee.getId().toString()
        );
    }

    private boolean isToday(UserDailyInOut userDailyInOut) {
        return Objects.equals(userDailyInOut.getDate().toLocalDate(), nowLocalDate());
    }

    private long getWorkingSeconds(UserDailyInOut userDailyInOut) {

        long workingSeconds;

        if (userDailyInOut.isInOffice()) {

            //AGAR SANA BUGUN BO'LSA HOZIRGACHA QANCHA VAQTLIGINI HISOBLAYMIZ
            boolean isToday = Objects.equals(userDailyInOut.getDate(), Date.valueOf(nowLocalDate()));

            LocalTime startTime;
            LocalTime endTime;

            Timestamp lastEnterTimeStamp = userDailyInOut.getLastEnterTimeStamp();

            startTime = Objects.nonNull(lastEnterTimeStamp) ? lastEnterTimeStamp.toLocalDateTime().toLocalTime() : nowLocalTime();

            //BUGUNMI
            if (isToday) {
                endTime = nowLocalTime();
            } else {
                endTime = LocalTime.MAX;
            }

            workingSeconds = userDailyInOut.getWorkingTimes() + Math.abs(Duration.between(startTime, endTime).getSeconds());

        } else {
            workingSeconds = userDailyInOut.getWorkingTimes();
        }

        return workingSeconds;
    }


    private String getDepartmenName(UUID employeeId, Map<UUID, UUID> employeeDepartmentMap, Map<String, String> departmentMap) {
        UUID departmentId = employeeDepartmentMap.get(employeeId);
        if (Objects.isNull(departmentId))
            return null;
        return departmentMap.get(departmentId.toString());
    }

    private TurniketColumnDataDTO getEmployeeFullNameColumnData(Employee employee) {
        return new TurniketColumnDataDTO(
                fullNameColumnId,
                employee.getFirstName() + " " + employee.getLastName()
        );
    }

    private TurniketColumnDataDTO getEmployeeDepartmentColumnData(String departmentName) {
        return new TurniketColumnDataDTO(
                departmentColumnId,
                departmentName
        );
    }


    private Map<String, TurniketFilterDTO> getFilters() {

        TurniketFilterDTO dateRangeFilter = getDateRangeFilter();
        TurniketFilterDTO companyFilter = getCompanyFilter();

        Map<String, TurniketFilterDTO> turniketFilterDTOMap = new LinkedHashMap<>();

        turniketFilterDTOMap.put(dateRangeFilter.getName(), dateRangeFilter);
        turniketFilterDTOMap.put(companyFilter.getName(), companyFilter);

        return turniketFilterDTOMap;
    }

    private Map<String, TurniketFilterDTO> getFilters(List<TurniketFilterDTO> filters) {

        Map<String, TurniketFilterDTO> baseFilterMap = getFilters();

        for (TurniketFilterDTO filter : filters) {
            TurniketFilterDTO turniketFilterDTO = baseFilterMap.get(filter.getName());
            if (Objects.isNull(turniketFilterDTO))
                continue;
            turniketFilterDTO.setValues(filter.getValues());
        }

        return baseFilterMap;
    }

    public TurniketFilterDTO getCompanyFilter() {

        TurniketFilterDTO companyFilterDTO = new TurniketFilterDTO();
        companyFilterDTO.setName(companyFilterName);
        companyFilterDTO.setType(CustomFieldTypeEnum.TREE);

        List<CustomFieldTreeOptionDTO> treeOptions = getCompanyTreeOptions();

        CustomFiledTypeConfigDTO configDTO = new CustomFiledTypeConfigDTO();
        configDTO.setTreeOptions(treeOptions);

        companyFilterDTO.setTypeConfig(configDTO);

        return companyFilterDTO;
    }

    private List<CustomFieldTreeOptionDTO> getCompanyTreeOptions() {

        List<CustomFieldOptionDTO> companyOptions = getCompanyOptions();
        List<DepartmentDTO> departmentOptions = departmentService.getAllDepartmentsFromDB();

        Map<Long, List<DepartmentDTO>> companyDepartmentMap = departmentOptions
                .stream()
                .collect(Collectors.groupingBy(DepartmentDTO::getCompanyId));

        return companyOptions
                .stream()
                .map(customFieldOptionDTO -> {

                    CustomFieldTreeOptionDTO customFieldTreeOptionDTO = new CustomFieldTreeOptionDTO(
                            null,
                            customFieldOptionDTO.getId(),
                            customFieldOptionDTO.getOrderIndex(),
                            customFieldOptionDTO.getName(),
                            customFieldOptionDTO.getColorCode(),
                            null
                    );

                    //SHU COMPANY NI DEPARTMENTLARINI BERAMIZ AGAR BO'LSA
                    if (companyDepartmentMap.containsKey(Long.parseLong(customFieldOptionDTO.getId()))) {
                        List<DepartmentDTO> departmentDTOList = companyDepartmentMap.get(Long.parseLong(customFieldOptionDTO.getId()));
                        List<CustomFieldTreeOptionDTO> children = departmentDTOList
                                .stream()
                                .map(departmentDTO -> new CustomFieldTreeOptionDTO(
                                        departmentDTO.getCompanyId().toString(),
                                        departmentDTO.getId().toString(),
                                        null,
                                        departmentDTO.getName(),
                                        null,
                                        null
                                ))
                                .collect(Collectors.toList());
                        customFieldTreeOptionDTO.setChildren(children);
                    }

                    return customFieldTreeOptionDTO;
                })
                .collect(Collectors.toList());


    }


    private List<CustomFieldOptionDTO> getCompanyOptions() {

        return feignService.getCompanyList()
                .stream()
                .map(companyFeignDTO -> new CustomFieldOptionDTO(
                        companyFeignDTO.getId().toString(),
                        companyFeignDTO.getName()
                ))
                .collect(Collectors.toList());
    }

    private TurniketFilterDTO getDepartmentFilter() {

        TurniketFilterDTO departmentFilterDTO = new TurniketFilterDTO();
        departmentFilterDTO.setName(departmentFilterName);
        departmentFilterDTO.setType(CustomFieldTypeEnum.LABELS);

        List<CustomFieldOptionDTO> departmentOptions = getDepartmentOptions();

        CustomFiledTypeConfigDTO configDTO = new CustomFiledTypeConfigDTO();
        configDTO.setOptions(departmentOptions);

        departmentFilterDTO.setTypeConfig(configDTO);

        return departmentFilterDTO;
    }

    private List<CustomFieldOptionDTO> getDepartmentOptions() {
        return departmentService.getAllDepartmentsFromDB()
                .stream()
                .map(departmentDTO -> new CustomFieldOptionDTO(
                        departmentDTO.getId().toString(),
                        departmentDTO.getName()
                ))
                .collect(Collectors.toList());
    }

    public TurniketFilterDTO getDateRangeFilter() {

        TurniketFilterDTO dateRangeFilter = new TurniketFilterDTO();
        dateRangeFilter.setName(dateRangeFilterName);
        dateRangeFilter.setType(CustomFieldTypeEnum.DATE);

        return dateRangeFilter;
    }


    private Map<String, ViewColumnDTO> getColumns(LocalDate start, LocalDate end) {

        checkMaxDayFilter(start, end);

        Map<String, ViewColumnDTO> stickyColumns = getStickyColumns();

        Map<String, ViewColumnDTO> hiddenColumns = getHiddenColumns();

        Map<String, ViewColumnDTO> columnMap = new LinkedHashMap<>(stickyColumns);
        columnMap.putAll(hiddenColumns);

        Map<String, ViewColumnDTO> dateColumnMap = getDateColumns(start, end);
        columnMap.putAll(dateColumnMap);
        return columnMap;
    }

    //MAXIMUM FILTER QILISH KUNI
    private void checkMaxDayFilter(LocalDate start, LocalDate end) {
        log.info("Period : {}", Period.between(start, end).getDays());
        if (Math.abs(ChronoUnit.DAYS.between(start, end)) > maxDayFilter)
            throw RestException.restThrow("Max day filter " + maxDayFilter + " day");
    }

    private Map<String, ViewColumnDTO> getHiddenColumns() {
        Map<String, ViewColumnDTO> columnDTOMap = new LinkedHashMap<>();

        ViewColumnDTO idColumn = new ViewColumnDTO();

        idColumn.setId(idColumnId);
        idColumn.setName(idColumnId);
        idColumn.setPinned(true);
        idColumn.setEnabled(false);
        idColumn.setHidden(true);
        idColumn.setType(CustomFieldTypeEnum.SHORT_TEXT);

        ViewColumnDTO phoneNumberColumn = new ViewColumnDTO();

        phoneNumberColumn.setId(phoneNumberColumnId);
        phoneNumberColumn.setName(phoneNumberColumnId);
        phoneNumberColumn.setPinned(true);
        phoneNumberColumn.setEnabled(false);
        phoneNumberColumn.setHidden(true);
        phoneNumberColumn.setSearchable(true);
        phoneNumberColumn.setType(CustomFieldTypeEnum.SHORT_TEXT);

        columnDTOMap.put(idColumn.getId(), idColumn);
        columnDTOMap.put(phoneNumberColumn.getId(), phoneNumberColumn);

        return columnDTOMap;
    }


    private Map<String, ViewColumnDTO> getStickyColumns() {
        ViewColumnDTO fioColumn = new ViewColumnDTO();

        fioColumn.setId(fullNameColumnId);
        fioColumn.setName("F.I.O");
        fioColumn.setPinned(true);
        fioColumn.setEnabled(false);
        fioColumn.setSearchable(true);
        fioColumn.setType(CustomFieldTypeEnum.SHORT_TEXT);

        ViewColumnDTO departmentColumn = new ViewColumnDTO();

        departmentColumn.setId(departmentColumnId);
        departmentColumn.setName("Bo'lim");
        departmentColumn.setPinned(true);
        departmentColumn.setEnabled(false);
        departmentColumn.setSearchable(true);
        departmentColumn.setType(CustomFieldTypeEnum.SHORT_TEXT);

        Map<String, ViewColumnDTO> stickyCoulmns = new LinkedHashMap<>();
        stickyCoulmns.put(fioColumn.getId(), fioColumn);
        stickyCoulmns.put(departmentColumn.getId(), departmentColumn);
        return stickyCoulmns;
    }

    private Map<String, ViewColumnDTO> getDateColumns(LocalDate start, LocalDate end) {

        checkDateIsCorrect(start, end);

        LinkedHashMap<String, ViewColumnDTO> dateColumnsMap = new LinkedHashMap<>();

        long days = Math.abs(Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays());

        DateFormat dateFormat = getDateFormat();

        for (int i = 0; i <= days; i++) {
            String columnName = dateFormat.format(java.util.Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            ViewColumnDTO dateColumn = new ViewColumnDTO();

            dateColumn.setId(columnName);
            dateColumn.setName(columnName);
            dateColumn.setPinned(false);
            dateColumn.setEnabled(false);
            dateColumn.setType(CustomFieldTypeEnum.TURNIKET_DAY);

            dateColumnsMap.put(dateColumn.getId(), dateColumn);
            start = start.plusDays(1);
        }

        return dateColumnsMap;
    }

    private void checkDateIsCorrect(LocalDate start, LocalDate end) {
        if (start.isAfter(end))
            throw RestException.restThrow("START DATE AFTER END DATE");
    }


    private DateFormat getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat(RestConstants.TURNIKET_COLUMN_DATE_FORMAT);
        return dateFormat;
    }

    private Integer getNextTurniketId() {
        Integer maxEmpId = employeeRepository.maxTurniketId();
        if (Objects.isNull(maxEmpId))
            return 1;
        return ++maxEmpId;
    }
}
