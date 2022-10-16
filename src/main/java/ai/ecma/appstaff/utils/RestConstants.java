package ai.ecma.appstaff.utils;

import ai.ecma.appstaff.enums.WeekDayEnum;

import java.time.format.DateTimeFormatter;

public interface RestConstants {

    String DOMAIN = "http://10.10.10.92:8080";

    String FEIGN_PRODUCES = "application/json;charset=utf-8";

    String BASE_PATH = "/api/staff";
    String BASE_PATH_V1 = BASE_PATH + "/v1";

    String AUTH_BASE_PATH_V1 = "/api/auth/v1";
    String ACADEMIC_CONTENT_BASE_PATH_V1 = "/api/academic-content/v1";
    String EDUCATION_SERVICE_BASE_PATH_V1 = "/api/education-service/v1";
    String PAYMENT_SERVICE_PATH_V1 = "/api/payment-service/v1";
    String FINANCE_SERVICE_PATH_V1 = "/api/finance/v1";
    String BRANCH_SERVICE_PATH_V1 = "/api/branch/v1";
    String ATTACHMENT_SERVICE_PATH_V1 = "/api/attachment/v1";
    String TURNIKET_SERVICE_PATH_V1 = "/api/turniket/v1";


    String AUTH_SERVICE = "AUTH-SERVICE" + AUTH_BASE_PATH_V1;
    String ACADEMIC_CONTENT = "ACADEMIC-CONTENT" + ACADEMIC_CONTENT_BASE_PATH_V1;
    String EDUCATION_SERVICE = "EDUCATION-SERVICE" + EDUCATION_SERVICE_BASE_PATH_V1;
    String PAYMENT_SERVICE = "PAYMENT-SERVICE" + PAYMENT_SERVICE_PATH_V1;
    String FINANCE_SERVICE = "FINANCE-SERVICE" + FINANCE_SERVICE_PATH_V1;
    String BRANCH_SERVICE = "BRANCH-SERVICE" + BRANCH_SERVICE_PATH_V1;
    String ATTACHMENT_SERVICE = "ATTACHMENT-SERVICE" + ATTACHMENT_SERVICE_PATH_V1;
    String TURNIKET_SERVICE = "TURNIKET-SERVICE" + TURNIKET_SERVICE_PATH_V1;

    // TIZIMDAGI DEFAULT PAGE VA SIZE. BU GET METHODI BILAN PAGEABLE OLISH UCHUN KERAK
    String DEFAULT_PAGE = "0";
    String DEFAULT_SIZE = "10";

    String FIRST = "FIRST";
    String LAST = "LAST";

    String ACTUATOR_PATH = "http://localhost:8084/actuator/refresh";

    String SERVICE_USERNAME_HEADER = "ServiceUsername";
    String SERVICE_PASSWORD_HEADER = "ServicePassword";

    String[] minutes = {"00", "15", "30", "45"};
    String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    WeekDayEnum[] WEEK_DAYS = {WeekDayEnum.MONDAY, WeekDayEnum.TUESDAY, WeekDayEnum.WEDNESDAY, WeekDayEnum.THURSDAY, WeekDayEnum.FRIDAY, WeekDayEnum.SATURDAY, WeekDayEnum.SUNDAY};

    String MODULE_NAME = "HRM";
    String AUTHORIZATION_HEADER = "Authorization";

    String REQUEST_ATTRIBUTE_CURRENT_USER = "User";
    String REQUEST_ATTRIBUTE_CURRENT_USER_ID = "UserId";
    String REQUEST_ATTRIBUTE_CURRENT_USER_PERMISSIONS = "Permissions";

    String INITIAL_EXECUTING_QUERY =
            "DROP INDEX if exists uk_user_view_user_id_and_view_id_and_deleted_false;\n" +

                    "CREATE UNIQUE INDEX uk_user_view_user_id_and_view_id_and_deleted_false\n" +
                    "ON user_view (user_id,view_id) WHERE  (deleted=false);\n" +

                    "DROP INDEX if exists uk_user_view_user_id_and_view_id_and_removed_false;\n" +

                    "CREATE UNIQUE INDEX uk_user_view_user_id_and_view_id_and_removed_false\n" +
                    "ON user_view (user_id,view_id) WHERE  (removed=false);" +

                    "DROP INDEX if exists uk_view_column_name_and_view_id;\n";

//    String INITIAL_EXECUTING_QUERYS =
//            "DROP INDEX if exists uk_user_view_user_id_and_view_id_and_deleted_false;\n" +
//                    "CREATE UNIQUE INDEX uk_user_view_user_id_and_view_id_and_deleted_false\n" +
//                    "ON user_view (user_id,view_id) WHERE  (deleted = false and removed=false);\n";


    int MAX_GENERIC_VALUE_SIZE = 50;
    String YES = "YES";
    String NO = "NO";
    String CALL_CENTER_MESSAGE_URL = "blabal";
    String ATTACHMENT_SERVICE_FILE_GET_URL = "blabalyoz";


    String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
//    String DATE_REGEX = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    String DATE_REGEX = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    String UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";

    String NUMBER_REGEX = "\\d+";
    String SKILL_CRUD = "/staff/v1/skill/crud";
    String PHONE_NUMBER_TYPE_CRUD = "/staff/v1/phone-number-type/crud";

    /*REST API Error codes*/
    int INCORRECT_USERNAME_OR_PASSWORD = 3001;
    int EMAIL_OR_PHONE_EXIST = 3002;
    int EXPIRED = 3003;
    int ACCESS_DENIED = 3004;
    int NOT_FOUND = 3005;
    int INVALID = 3006;
    int REQUIRED = 3007;
    int SERVER_ERROR = 3008;
    int CONFLICT = 3009;
    int NO_ITEMS_FOUND = 3011;
    int CONFIRMATION = 3012;
    int USER_NOT_ACTIVE = 3013;
    int JWT_TOKEN_INVALID = 3014;

    String UNKNOWN = "unknown";



    String QUERY_FOR_OWNER_POSTGRES = "drop function if exists get_entity_id_list_for_generic_view;\n" +
            "create function get_entity_id_list_for_generic_view(sql_query character varying)\n" +
            "    returns TABLE\n" +
            "            (\n" +
            "                id_list    varchar\n" +
            "            )\n" +
            "    language plpgsql\n" +
            "as\n" +
            "$$\n" +
            "BEGIN\n" +
            "    RETURN QUERY\n" +
            "        EXECUTE sql_query;\n" +
            "END\n" +
            "$$;\n" +
            "alter function get_entity_id_list_for_generic_view(varchar) owner to postgres;";

    String QUERY_FOR_OWNER_STAFF = "drop function if exists get_entity_id_list_for_generic_view;\n" +
            "create function get_entity_id_list_for_generic_view(sql_query character varying)\n" +
            "    returns TABLE\n" +
            "            (\n" +
            "                id_list    varchar\n" +
            "            )\n" +
            "    language plpgsql\n" +
            "as\n" +
            "$$\n" +
            "BEGIN\n" +
            "    RETURN QUERY\n" +
            "        EXECUTE sql_query;\n" +
            "END\n" +
            "$$;\n" +
            "alter function get_entity_id_list_for_generic_view(varchar) owner to staff;";

    String TASHKENT_TIME_ZONE = "Asia/Tashkent";
    String TURNIKET_COLUMN_DATE_FORMAT = "dd/MM/yyyy EEE";
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
}

