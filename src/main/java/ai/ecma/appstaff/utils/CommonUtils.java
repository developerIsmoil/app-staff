package ai.ecma.appstaff.utils;

import ai.ecma.appstaff.controller.view.ViewController;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.UserDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static ai.ecma.appstaff.utils.RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER;
import static ai.ecma.appstaff.utils.RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER_ID;

public class CommonUtils {

    public static String ATTACHMENT_DOWNLOAD_PATH;
    public static String ATTACHMENT_MEDIUM_VIEW_PATH;
    public static String DOMAIN;

    public static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    public static TypeFactory typeFactory = TypeFactory.defaultInstance();

    //
    public static String getDownloadPath(String fileId) {
        if (fileId == null) return null;
        return DOMAIN + "/" + ATTACHMENT_DOWNLOAD_PATH + "?id=" + fileId;
    }


    public static UserDTO getCurrentUser() {
        try {
            HttpServletRequest httpServletRequest = currentRequest();
            UserDTO currentUser = (UserDTO) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE_CURRENT_USER);
            if (currentUser == null || Objects.isNull(currentUser.getId())) {
                throw RestException.restThrow("Error! Access is not possible", HttpStatus.FORBIDDEN);
            }
            return currentUser;
        } catch (Exception e) {
            throw RestException.restThrow("Error! Access is not possible", HttpStatus.FORBIDDEN);
        }
    }

    public static String urlBuilderForViewColumnEdit() {
        return RestConstants.DOMAIN + ViewController.VIEW_CONTROLLER_PATH + ViewController.EDIT_VIEW_NAME_PATH;
    }

    public static UserDTO getUserDTOFromRequestForAuditing() {
        try {
            HttpServletRequest httpServletRequest = currentRequest();
            return (UserDTO) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE_CURRENT_USER);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(date);
    }

    //REQUEST DAN USER ID NI OLIB BERADI
    public static String getUserIdFromRequestHeader() {
        try {
            HttpServletRequest httpServletRequest = currentRequest();
            return httpServletRequest.getHeader(REQUEST_ATTRIBUTE_CURRENT_USER_ID);
        } catch (Exception e) {
            return null;
        }
    }

    //REQUEST DAN PERMISSION LARNI OLIB BERADI
    public static String getUserPermissionsFromRequestHeader() {
        try {
            HttpServletRequest httpServletRequest = currentRequest();
            return httpServletRequest.getHeader(REQUEST_ATTRIBUTE_CURRENT_USER_ID);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserIdFromRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(REQUEST_ATTRIBUTE_CURRENT_USER_ID);
    }

    public static String getUserPermissionsFromRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER_PERMISSIONS);
    }

    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional
                .ofNullable(servletRequestAttributes)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    //REQUEST DAN TOKENNI OLIB BERADI
    public static String getTokenFromRequest() {
        HttpServletRequest httpServletRequest = currentRequest();
        if (Objects.isNull(httpServletRequest)) {
            return "";
        }
        String header = httpServletRequest.getHeader(RestConstants.AUTHORIZATION_HEADER);
        return Objects.nonNull(header) ? header : "";
    }

    public static String getThisMonth() {
        LocalDate localDate = LocalDate.now();
        return localDate.getMonth().toString();
    }

    public static String getThisYear() {
        LocalDate localDate = LocalDate.now();
        return String.valueOf(localDate.getYear());
    }

    /**
     * OYNING BIRINCHI SANASINI OLISH UCHUN
     *
     * @return Date
     */
    public static Date getThisMonthFirstDay() {
        LocalDate localDate = LocalDate.now();

        return Date
                .from(localDate
                        .withDayOfMonth(1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getGivenDateMonthLastDay(Date date) {
        LocalDate localDate = convertDateToLocalDate(date);

        Calendar cal = Calendar.getInstance();
        int dateIndex = cal.getActualMaximum(Calendar.DATE);

        return Date.from(localDate.withDayOfMonth(dateIndex).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private static Calendar getCalendarWithoutTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * OYNING BIRINCHI YOKI OXIRIGI KUNINI OLISH UCHUN
     *
     * @param str FIRST | LAST
     * @return Date
     */
    public static Date getMonthDate(String str, Date date) {

        Calendar cal = getCalendarWithoutTime(date);
        LocalDate localDate = convertDateToLocalDate(date);

        int dateIndex = 0;

        // OYNING BIRINCHI KUNINI OLISH UCHUN (1) Date ko'rinishida (dd/mm/yyy)
        if (str.equals(RestConstants.FIRST)) {

            dateIndex = cal.getActualMinimum(Calendar.DATE);

            // OYNING OXIRIGI KUNINI OLISH UCHUN (28,29,30,31) Date ko'rinishida (dd/mm/yyy)
        } else if (str.equals(RestConstants.LAST)) {

            dateIndex = cal.getActualMaximum(Calendar.DATE);
        }

        return Date.from(localDate.withDayOfMonth(dateIndex).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    }

    public static Date getMonthDate(String str) {

        Calendar cal = Calendar.getInstance();
        LocalDate localDate = LocalDate.now();

        int dateIndex = 0;

        // OYNING BIRINCHI KUNINI OLISH UCHUN (1) Date ko'rinishida (dd/mm/yyy)
        if (str.equals(RestConstants.FIRST)) {

            dateIndex = cal.getActualMinimum(Calendar.DATE);

            // OYNING OXIRIGI KUNINI OLISH UCHUN (28,29,30,31) Date ko'rinishida (dd/mm/yyy)
        } else if (str.equals(RestConstants.LAST)) {

            dateIndex = cal.getActualMaximum(Calendar.DATE);
        }

        return Date.from(localDate.withDayOfMonth(dateIndex).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * Date dan dateformat ni olish uchun (dd-M-yyyy hh:mm:ss)
     *
     * @param date date
     * @return String
     */
    public static String getDateFormatWithHour(Date date) {
        return getDateFormat(date, "dd/MM/yyyy hh:mm:ss");
    }

    /**
     * Date dan dateformat ni olish uchun (dd-M-yyyy hh:mm:ss)
     *
     * @param date date
     * @return String
     */
    private static String getDateFormat(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * Date dan dateformat ni olish uchun (dd-M-yyyy hh:mm:ss)
     *
     * @param date date
     * @return String
     */
    public static String getDateFormat(Date date) {
        return getDateFormat(date, "dd/MM/yyyy");
    }

    /**
     * BUNDA SOATLAR STRINGDA KELADI VA ULAR ORASIDAGI VAQTNI TOPIB OLIB QAYTARAMIZ
     *
     * @param fromDate BUNDA BOSHLANG'ISH VAQT KELADI
     *                 MASALAN: 09:00:00
     * @param toDate   BUNDA TUGASH VAQT KELADI
     *                 MASALAN: 19:00:00
     * @return 09:00:00 DAN 19:00:00 VAQT ORALIQLARI 10 SOAT EKAN MASALAN: 10.0
     */
    public static Double getFromAndToDateInterval(String fromDate, String toDate) {

        Time from = Time.valueOf(fromDate);

        Time to = Time.valueOf(toDate);

        double interval = from.getTime() - to.getTime();

        double result = interval / 3_600_000;

        return Math.abs(result);
    }

    /**
     * Date ni LocalDate ga o'tkazish uchun method
     *
     * @param dateToConvert
     * @return
     */
    public static LocalDate convertDateToLocalDate(Long dateToConvert) {
        return new Date(dateToConvert)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Date ni LocalDate ga o'tkazish uchun method
     *
     * @param dateToConvert
     * @return
     */
    public static LocalDate convertDateToLocalDate(Date dateToConvert) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return LocalDate
                .parse(dateFormat.format(dateToConvert));
    }

    /**
     * LocalDate ni Date ga o'girish uchun method
     *
     * @param dateToConvert
     * @return
     */
    public static Date convertLocalDateToDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    /**
     * /QAVS LARNI QIRQIB TASHLAYDI
     *
     * @param object
     * @return
     */
    public static String replaceBracket(String object) {
        String s = object.replaceAll("[\\[\\]\" ]", "");
        System.out.println(s);
        return s;
    }

    //
    public static boolean havePermission(PermissionEnum[] mustPermission) {
        UserDTO userDTO = getCurrentUser();
        List<String> hasPermission = userDTO.getPermissions();
        if (hasPermission == null)
            return false;
        for (PermissionEnum permissionEnum : mustPermission) {
            if (hasPermission.contains(permissionEnum.name()))
                return true;
        }
        return false;
    }

    //AGAR TIZIMDA TURMAGAN BOSHQA USER NI TEKSHIRMOQCHI BO'LSANG USER DTO NI O'ZING BERIB YUBORASAN
    public static boolean havePermission(UserDTO userDTO, PermissionEnum[] mustPermission) {
        List<String> hasPermission = userDTO.getPermissions();
        if (hasPermission == null)
            return false;
        for (PermissionEnum permissionEnum : mustPermission) {
            if (hasPermission.contains(permissionEnum.name()))
                return true;
        }
        return false;
    }

    public static boolean havePermission(List<String> hasPermission, String tableName) {
        if (hasPermission == null || tableName == null || tableName.isEmpty())
            return false;
        List<PermissionEnum> tableNamesPermissions = Arrays.stream(PermissionEnum.values()).filter(permissionEnum -> tableName.equals(permissionEnum.getTableName())).collect(Collectors.toList());

        for (PermissionEnum permissionEnum : tableNamesPermissions) {
            if (hasPermission.contains(permissionEnum.name()))
                return true;
        }
        return false;
    }

    public static String makePascalCase(String str) {

        if (Objects.isNull(str) || str.isEmpty()) {
            return null;
        }

        String trimmedString = str.trim();

        String firstCharacter = trimmedString.substring(0, 1);
        String otherCharacter = trimmedString.substring(1);

        return firstCharacter.toUpperCase() + otherCharacter.toLowerCase();
    }

    public static String makePhoneNumber(String phoneNumber) {
        return "+" + (phoneNumber.replaceAll("[+]*", ""));
    }

    /**
     * concat two string with space
     *
     * @param firstName firstName
     * @param lastName  lastName
     * @return String
     */
    public static String concatTwoStringWithSpace(String firstName, String lastName) {
        String firstNameLocal = checkIfNotNullReturnIt(firstName);
        String lastNameLocal = checkIfNotNullReturnIt(lastName);

        return firstNameLocal + " " + lastNameLocal;
    }

    /**
     * check if not null return it
     *
     * @param str str
     * @return String
     */
    private static String checkIfNotNullReturnIt(String str) {
        if (Objects.isNull(str)) {
            return "---";
        } else {
            return str;
        }
    }

    /**
     * ikkida dateni string formatga o'tkazib taqqoslaganmiz
     *
     * @param date1 date1
     * @param date2 date2
     * @return boolean
     */
    public static boolean compareTwoDateWithStr(Date date1, Date date2) {

        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return false;
        }

        String dateS1 = getDateFormat(date1);
        String dateS2 = getDateFormat(date2);

        return Objects.equals(dateS1, dateS2);

    }

    public static String getTableName(String tableName) {
        switch (tableName) {

            case TableNameConstant.MENTOR:
            case TableNameConstant.EMPLOYEE:
                return TableNameConstant.EMPLOYEE;

            case TableNameConstant.TIMESHEET_EMPLOYEE:
            case TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE:
                return TableNameConstant.TIMESHEET_EMPLOYEE;

            case TableNameConstant.TARIFF_GRID:
                return TableNameConstant.TARIFF_GRID;

        }
        return tableName;
    }

    //  Sort.by(Sort.Direction.DESC, "createdAt")
    public static Sort sortByColumn(String sortedColumnName, Sort.Direction direction) {
        return Sort.by(direction, sortedColumnName);
    }

    public static UserDTO getCurrentUserOrNull() {
        try {
            HttpServletRequest httpServletRequest = currentRequest();
            UserDTO currentUser = (UserDTO) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE_CURRENT_USER);
            if (currentUser == null || Objects.isNull(currentUser.getId())) {
                return new UserDTO();
            }
            return currentUser;
        } catch (Exception e) {
            return new UserDTO();
        }
    }
}
