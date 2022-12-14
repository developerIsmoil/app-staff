package ai.ecma.appstaff.utils;

/**
 * @author IkhtiyorDev  <br/>
 * Date 25/02/22
 **/

public interface ResponseMessage {

    String SUCCESS_DEPARTMENT_SAVED = "SUCCESS DEPARTMENT SAVED";
    String SUCCESS_DEPARTMENT_EDITED = "SUCCESS DEPARTMENT EDITED";
    String SUCCESS_DEPARTMENT_DELETED = "SUCCESS DEPARTMENT DELETED";
    String SUCCESS_DEPARTMENT_STATUS_CHANGE = "SUCCESS DEPARTMENT STATUS CHANGE";
    String SUCCESS_EMPLOYEE_CATEGORY_TYPE_SAVED = "SUCCESS EMPLOYEE CATEGORY TYPE SAVED";
    String SUCCESS_EMPLOYEE_CATEGORY_TYPE_EDITED = "SUCCESS EMPLOYEE CATEGORY TYPE EDITED";
    String SUCCESS_EMPLOYEE_CATEGORY_TYPE_STATUS_CHANGE = "SUCCESS EMPLOYEE CATEGORY TYPE STATUS CHANGE";
    String SUCCESS_EMPLOYEE_CATEGORY_TYPE_DELETED = "SUCCESS EMPLOYEE CATEGORY TYPE DELETED";
    String SUCCESS_PRIVILEGE_TYPE_SAVED = "SUCCESS PRIVILEGE TYPE SAVED";
    String SUCCESS_PRIVILEGE_TYPE_EDITED = "SUCCESS PRIVILEGE TYPE EDITED";
    String SUCCESS_PRIVILEGE_TYPE_DELETED = "SUCCESS PRIVILEGE TYPE DELETED";
    String SUCCESS_PRIVILEGE_TYPE_STATUS_CHANGE = "SUCCESS PRIVILEGE TYPE STATUS CHANGE";
    String SUCCESS_POSITION_SAVED = "SUCCESS POSITION SAVED";
    String SUCCESS_POSITION_EDITED = "SUCCESS POSITION EDITED";
    String SUCCESS_POSITION_DELETED = "SUCCESS POSITION DELETED";
    String SUCCESS_POSITION_STATUS_CHANGE = "SUCCESS POSITION STATUS CHANGE";
    String SUCCESS_EMPLOYEE_CATEGORY_SAVED = "SUCCESS EMPLOYEE CATEGORY SAVED";
    String SUCCESS_EMPLOYEE_CATEGORY_EDITED = "SUCCESS EMPLOYEE CATEGORY EDITED";
    String SUCCESS_EMPLOYEE_CATEGORY_DELETED = "SUCCESS EMPLOYEE CATEGORY DELETED";
    String SUCCESS_EMPLOYEE_CATEGORY_STATUS_CHANGE = "SUCCESS EMPLOYEE CATEGORY STATUS CHANGE";
    String SUCCESS_EMPLOYEE_SAVED = "SUCCESS EMPLOYEE SAVED";
    String SUCCESS_EMPLOYEE_EDITED = "SUCCESS EMPLOYEE EDITED";
    String SUCCESS_EMPLOYEE_DELETED = "SUCCESS EMPLOYEE DELETED";
    String SUCCESS_EMPLOYEE_STATUS_CHANGE = "SUCCESS EMPLOYEE STATUS CHANGE";
    String SUCCESS_TEMPLATE_FOR_SICK_SAVED = "SUCCESS TEMPLATE FOR SICK SAVED";
    String SUCCESS_TEMPLATE_FOR_SICK_EDITED = "SUCCESS TEMPLATE FOR SICK EDITED";
    String SUCCESS_TEMPLATE_FOR_SICK_DELETED = "SUCCESS TEMPLATE FOR SICK DELETED";
    String SUCCESS_TEMPLATE_FOR_SICK_STATUS_CHANGE = "SUCCESS TEMPLATE FOR SICK STATUS CHANGE";
    String SUCCESS_SKILL_SAVED = "SUCCESS SKILL SAVED";
    String SUCCESS_SKILL_STATUS_CHANGE = "SUCCESS SKILL STATUS CHANGE";
    String SUCCESS_SKILL_EDITED = "SUCCESS SKILL EDITED";
    String SUCCESS_SKILL_DELETED = "SUCCESS SKILL DELETED";
    String SUCCESS_TARIFF_GRID_SAVED = "SUCCESS TARIFF GRID SAVED";
    String SUCCESS_TARIFF_GRID_EDITED = "SUCCESS TARIFF GRID EDITED";
    String SUCCESS_TARIFF_GRID_DELETED = "SUCCESS TARIFF GRID DELETED";
    String SUCCESS_TARIFF_GRID_STATUS_CHANGE = "SUCCESS TARIFF GRID STATUS CHANGE";
    String SUCCESS_WORK_DAY_ACTIVE_CHANGED = "SUCCESS WORK DAY ACTIVE CHANGED";
    String SUCCESS_PHONE_NUMBER_TYPE_SAVED = "SUCCESS PHONE NUMBER TYPE SAVED";
    String SUCCESS_PHONE_NUMBER_TYPE_EDITED = "SUCCESS PHONE NUMBER TYPE EDITED";
    String SUCCESS_PHONE_NUMBER_TYPE_DELETED = "SUCCESS PHONE NUMBER TYPE DELETED";
    String SUCCESS_HOLIDAY_SAVED = "SUCCESS HOLIDAY SAVED";
    String SUCCESS_HOLIDAY_EDITED = "SUCCESS HOLIDAY EDITED";
    String SUCCESS_HOLIDAY_DELETED = "SUCCESS HOLIDAY DELETED";
    String SUCCESS_HOLIDAY_STATUS_CHANGE = "SUCCESS HOLIDAY STATUS CHANGE";

    String MAX_GENERIC_VALUE_SIZE = "MAX GENERIC VALUE SIZE";
    //
    String CUSTOM_FIELD_TYPE_CONFIG_REQUIRED = "CUSTOM FIELD TYPE CONFIG REQUIRED";
    String CUSTOM_FIELD_OPTIONS_REQUIRED = "CUSTOM FIELD OPTIONS REQUIRED";
    String CUSTOM_FIELD_RATING_REQUIRED = "CUSTOM FIELD RATING REQUIRED";
    String CUSTOM_FIELD_VALUE_REQUIRED = "CUSTOM FIELD VALUE REQUIRED";
    String CUSTOM_FIELD_REQUIRED = "CUSTOM FIELD REQUIRED";
    String CUSTOM_FIELD_VALUE_NOT_NULL = "CUSTOM FIELD VALUE NOT NULL";
    String CUSTOM_FIELD_NOT_FOUND = "CUSTOM FIELD NOT FOUND";
    //
    String REQUIRED_NOT_NULL = "REQUIRED NOT NULL";
    String REQUIRED_FIRSTNAME = "REQUIRED FIRSTNAME";
    String REQUIRED_LASTNAME = "REQUIRED LASTNAME";
    String REQUIRED_BIRTHDATE = "REQUIRED BIRTHDATE";
    String REQUIRED_MARITAL_STATUS = "REQUIRED MARITAL STATUS";
    String REQUIRED_GENDER = "REQUIRED GENDER";
    String REQUIRED_EMAIL = "REQUIRED EMAIL";
    String REQUIRED_PHONE_NUMBER = "REQUIRED PHONE NUMBER";
    String REQUIRED_PHONE_NUMBER_TYPE_ID = "REQUIRED PHONE NUMBER TYPE ID";
    String REQUIRED_PASSPORT_SERIAL = "REQUIRED PASSPORT SERIAL";
    String REQUIRED_PASSPORT_NUMBER = "REQUIRED PASSPORT NUMBER";
    String REQUIRED_PASSPORT_GIVEN_ORGANISATION = "REQUIRED PASSPORT GIVEN ORGANISATION";
    String REQUIRED_PASSPORT_GIVEN_DATE = "REQUIRED PASSPORT GIVEN DATE";
    String REQUIRED_PASSPORT_EXPIRE_DATE = "REQUIRED PASSPORT EXPIRE DATE";
    String REQUIRED_PERMANENT_ADDRESS = "REQUIRED PERMANENT ADDRESS";
    String REQUIRED_CURRENT_ADDRESS = "REQUIRED CURRENT ADDRESS";
    String REQUIRED_BRANCH_ID = "REQUIRED BRANCH ID";
    String REQUIRED_DEPARTMENT_ID = "REQUIRED DEPARTMENT ID";
    String REQUIRED_POSITION_ID = "REQUIRED POSITION ID";
    String REQUIRED_EMPLOYEE_CATEGORY_ID = "REQUIRED EMPLOYEE CATEGORY ID";
    String REQUIRED_PAYMENT_CRITERIA_TYPE = "REQUIRED PAYMENT CRITERIA TYPE";
    String REQUIRED_CONTRACT_FORM = "REQUIRED CONTRACT FORM";
    String REQUIRED_EMPLOYEE_MODE = "REQUIRED EMPLOYEE MODE";
    String REQUIRED_MANAGE_TIMESHEET = "REQUIRED MANAGE TIMESHEET";
    String REQUIRED_STUDY_DEGREE = "REQUIRED STUDY DEGREE";
    String REQUIRED_ORGANISATION_NAME = "REQUIRED ORGANISATION NAME";
    String REQUIRED_STUDY_TYPE = "REQUIRED STUDY TYPE";
    String REQUIRED_STARTED_STUDY_DATE = "REQUIRED STARTED STUDY DATE";
    String REQUIRED_POSITION = "REQUIRED POSITION";
    String REQUIRED_STARTED_WORK_DATE = "REQUIRED STARTED WORK DATE";
    String REQUIRED_FILE_ID = "REQUIRED FILE ID";
    String REQUIRED_DESCRIPTION = "REQUIRED DESCRIPTION";
    String REQUIRED_RESIGNATION_DATE = "REQUIRED RESIGNATION DATE";

    //
    String SERVER_ERROR = "SERVER ERROR";
    //

    String ERROR_DEPARTMENT_SAVING = "ERROR DEPARTMENT SAVING";
    String ERROR_DEPARTMENT_EDITING = "ERROR DEPARTMENT EDITING";
    String ERROR_DEPARTMENT_NOT_FOUND = "ERROR DEPARTMENT NOT FOUND";
    String ERROR_DEPARTMENT_NOT_ACTIVE = "ERROR DEPARTMENT NOT ACTIVE";
    String ERROR_DEPARTMENT_ALREADY_EXIST = "ERROR DEPARTMENT ALREADY EXIST";
    String ERROR_DEPARTMENT_REQUIRED = "ERROR DEPARTMENT REQUIRED";
    String ERROR_DEPARTMENT_NAME_REQUIRED = "ERROR DEPARTMENT NAME REQUIRED";
    String ERROR_DEPARTMENT_CAN_NOT_DELETE = "ERROR DEPARTMENT CAN NOT DELETE";
    String ERROR_EMPLOYEE_CATEGORY_TYPE_ALREADY_EXIST = "ERROR EMPLOYEE CATEGORY TYPE ALREADY EXIST";
    String ERROR_EMPLOYEE_CATEGORY_TYPE_SAVING = "ERROR EMPLOYEE CATEGORY TYPE SAVING";
    String ERROR_EMPLOYEE_CATEGORY_TYPE_EDITING = "ERROR EMPLOYEE CATEGORY TYPE EDITING";
    String ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_FOUND = "ERROR EMPLOYEE CATEGORY TYPE NOT FOUND";
    String ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_ACTIVE = "ERROR EMPLOYEE CATEGORY TYPE NOT ACTIVE";
    String ERROR_PRIVILEGE_TYPE_ALREADY_EXIST = "ERROR PRIVILEGE TYPE ALREADY EXIST";
    String ERROR_PRIVILEGE_TYPE_SAVING = "ERROR PRIVILEGE TYPE SAVING";
    String ERROR_PRIVILEGE_TYPE_EDITING = "ERROR PRIVILEGE TYPE EDITING";
    String ERROR_PRIVILEGE_TYPE_NOT_FOUND = "ERROR PRIVILEGE TYPE NOT FOUND";
    String ERROR_PRIVILEGE_TYPE_NOT_ACTIVE = "ERROR PRIVILEGE TYPE NOT ACTIVE";
    String ERROR_PRIVILEGE_TYPE_NAME_REQUIRED = "ERROR PRIVILEGE TYPE NAME REQUIRED";
    String ERROR_POSITION_ALREADY_EXIST = "ERROR POSITION ALREADY EXIST";
    String ERROR_POSITION_SAVING = "ERROR POSITION SAVING";
    String ERROR_POSITION_EDITING = "ERROR POSITION EDITING";
    String ERROR_POSITION_NOT_FOUND = "ERROR POSITION NOT FOUND";
    String ERROR_POSITION_NOT_ACTIVE = "ERROR POSITION NOT ACTIVE";
    String ERROR_POSITION_NAME_REQUIRED = "ERROR POSITION NAME REQUIRED";
    String ERROR_EMPLOYEE_CATEGORY_SAVING = "ERROR EMPLOYEE CATEGORY SAVING";
    String ERROR_EMPLOYEE_CATEGORY_ALREADY_EXIST = "ERROR EMPLOYEE CATEGORY ALREADY EXIST";
    String ERROR_EMPLOYEE_CATEGORY_EDITING = "ERROR EMPLOYEE CATEGORY EDITING";
    String ERROR_EMPLOYEE_CATEGORY_NOT_FOUND = "ERROR EMPLOYEE CATEGORY NOT FOUND";
    String ERROR_EMPLOYEE_CATEGORY_NOT_ACTIVE = "ERROR EMPLOYEE CATEGORY NOT ACTIVE";
    String ERROR_EMPLOYEE_SAVING = "ERROR EMPLOYEE SAVING";
    String ERROR_EMPLOYEE_SAVING_EMPLOYEE_INFO = "ERROR EMPLOYEE SAVING EMPLOYEE INFO";
    String ERROR_EMPLOYEE_SAVING_PASSPORT_INFO = "ERROR EMPLOYEE SAVING PASSPORT INFO";
    String ERROR_EMPLOYEE_SAVING_ACCOUNT_INFO = "ERROR EMPLOYEE SAVING ACCOUNT INFO";
    String ERROR_EMPLOYEE_EDITING = "ERROR EMPLOYEE EDITING";
    String ERROR_EMPLOYEE_NOT_FOUND = "ERROR EMPLOYEE NOT FOUND";
    String ERROR_EMPLOYEE_NOT_SELECTED_MAIN_PHONE_NUMBER = "ERROR EMPLOYEE NOT SELECTED MAIN PHONE NUMBER";
    String ERROR_EMPLOYEE_SAVE_ATTACHMENT = "ERROR EMPLOYEE SAVE ATTACHMENT";
    String ERROR_EMPLOYEE_MAIN_PHONE_NUMBER_SHOULD_BE_ONE = "ERROR EMPLOYEE MAIN PHONE NUMBER SHOULD BE ONE";
    String ERROR_EMPLOYEE_SAVE_PHONE_NUMBER = "ERROR EMPLOYEE SAVE PHONE NUMBER";
    String ERROR_EMPLOYEE_PHONE_NUMBER_NOT_NULL = "ERROR EMPLOYEE PHONE NUMBER NOT NULL";
    String ERROR_EMPLOYEE_SAVE_HISTORY_EDUCATION = "ERROR EMPLOYEE SAVE HISTORY EDUCATION";
    String ERROR_EMPLOYEE_SAVE_HISTORY_EXPERIENCE = "ERROR EMPLOYEE SAVE HISTORY EXPERIENCE";
    String ERROR_EMPLOYEE_SAVE_EMPLOYMENT_INFO = "ERROR EMPLOYEE SAVE EMPLOYMENT INFO";
    String ERROR_EMPLOYEE_SAVE_SKILL_INFO = "ERROR EMPLOYEE SAVE SKILL INFO";
    String ERROR_EMPLOYEE_SAVE_FORM_WORK_DAY_NOT_FOUND_REQUIRED_INFORMATION = "ERROR EMPLOYEE SAVE FORM WORK DAY NOT FOUND REQUIRED INFORMATION";
    String ERROR_INVALID_PAST_DATE = "ERROR INVALID PAST DATE";
    String ERROR_INVALID_FUTURE_DATE = "ERROR INVALID FUTURE DATE";
    String ERROR_INVALID_DATE_MUST_BE_GREAT = "ERROR INVALID DATE MUST BE GREAT";
    String ERROR_INVALID_DATE_MUST_BE_LESS = "ERROR INVALID DATE MUST BE LESS";
    String ERROR_EMAIL_NOT_VALID = "ERROR EMAIL NOT VALID";
    String ERROR_TEMPLATE_FOR_SICK_SAVING = "ERROR TEMPLATE FOR SICK SAVING";
    String ERROR_TEMPLATE_FOR_SICK_ALREADY_EXIST = "ERROR TEMPLATE FOR SICK ALREADY EXIST";
    String ERROR_TEMPLATE_FOR_SICK_EDITING = "ERROR TEMPLATE FOR SICK EDITING";
    String ERROR_TEMPLATE_FOR_SICK_NOT_FOUND = "ERROR TEMPLATE FOR SICK NOT FOUND";
    String ERROR_TEMPLATE_FOR_SICK_NOT_ACTIVE = "ERROR TEMPLATE FOR SICK NOT ACTIVE";
    String ERROR_TEMPLATE_FOR_SICK_FROM_COUNT_DOES_NOT_NULL = "ERROR TEMPLATE FOR SICK FROM COUNT DOES NOT NULL";
    String ERROR_TEMPLATE_FOR_SICK_TO_COUNT_DOES_NOT_NULL = "ERROR TEMPLATE FOR SICK TO COUNT DOES NOT NULL";
    String ERROR_TEMPLATE_FOR_SICK_INVALID_DURATION = "ERROR TEMPLATE FOR SICK INVALID DURATION";
    String ERROR_TEMPLATE_FOR_SICK_PRIVILEGE_TYPE_ID_DOES_NOT_NULL = "ERROR TEMPLATE FOR SICK PRIVILEGE TYPE ID DOES NOT NULL";
    String ERROR_TEMPLATE_FOR_SICK_PERCENT_DOES_NOT_NULL = "ERROR TEMPLATE FOR SICK PERCENT DOES NOT NULL";
    String ERROR_SKILL_SAVING = "ERROR SKILL SAVING";
    String ERROR_SKILL_EDITING = "ERROR SKILL EDITING";
    String ERROR_SKILL_CAN_NOT_DELETE = "ERROR SKILL CAN NOT DELETE";
    String ERROR_SKILL_ALREADY_EXIST = "ERROR SKILL ALREADY EXIST";
    String ERROR_SKILL_NOT_FOUND = "ERROR SKILL NOT FOUND";
    String ERROR_SKILL_NOT_ACTIVE = "ERROR SKILL NOT ACTIVE";
    String ERROR_SKILL_NAME_REQUIRED = "ERROR SKILL NAME REQUIRED";
    String ERROR_SKILL_COLOR_REQUIRED = "ERROR SKILL COLOR REQUIRED";
    String ERROR_BRANCH_ID_REQUIRED = "ERROR BRANCH ID REQUIRED";
    String ERROR_PAYMENT_CRITERIA_TYPE_REQUIRED = "ERROR PAYMENT CRITERIA TYPE REQUIRED";
    String ERROR_TARIFF_GRID_NOT_FOUND = "ERROR TARIFF GRID NOT FOUND";
    String ERROR_SAVING_TARIFF_GRID = "ERROR SAVING TARIFF GRID";
    String ERROR_EDITING_TARIFF_GRID = "ERROR EDITING TARIFF GRID";
    String ERROR_TARIFF_GRID_SAVING_EXISTS_UNIQUE = "ERROR TARIFF GRID SAVING EXISTS UNIQUE";
    String ERROR_PAYMENT_CRITERIA = "ERROR PAYMENT CRITERIA";
    String ERROR_BONUS_TYPE = "ERROR BONUS TYPE";
    String ERROR_TARIFF_GRID_NOT_ACTIVE = "ERROR TARIFF GRID NOT ACTIVE";
    String ERROR_TARIFF_GRID_NOT_MATCH = "ERROR TARIFF GRID NOT MATCH";
    String ERROR_WORK_DAY_ACTIVE_CHANGING = "ERROR WORK DAY ACTIVE CHANGING";
    String ERROR_WORK_DAY_NOT_FOUND = "ERROR WORK DAY NOT FOUND";
    String ERROR_PHONE_NUMBER_TYPE_ALREADY_EXIST = "ERROR PHONE NUMBER TYPE ALREADY EXIST";
    String ERROR_PHONE_NUMBER_TYPE_SAVING = "ERROR PHONE NUMBER TYPE SAVING";
    String ERROR_PHONE_NUMBER_TYPE_EDITING = "ERROR PHONE NUMBER TYPE EDITING";
    String ERROR_NOT_FOUND_PHONE_NUMBER_TYPE = "ERROR NOT FOUND PHONE NUMBER TYPE";
    String ERROR_NOT_NULL_PHONE_NUMBER_TYPE = "ERROR NOT NULL PHONE NUMBER TYPE";
    String ERROR_PHONE_NUMBER_TYPE_DELETING = "ERROR PHONE NUMBER TYPE DELETING";
    String ERROR_HOLIDAY_SAVING = "ERROR HOLIDAY SAVING";
    String ERROR_HOLIDAY_EDITING = "ERROR HOLIDAY EDITING";
    String ERROR_HOLIDAY_NOT_FOUND = "ERROR HOLIDAY NOT FOUND";
    String ERROR_HOLIDAY_NOT_ACTIVE = "ERROR HOLIDAY NOT ACTIVE";
    String ERROR_HOLIDAY_ALREADY_EXIST = "ERROR HOLIDAY ALREADY EXIST";
    String ERROR_HOLIDAY_REQUIRED = "ERROR HOLIDAY REQUIRED";
    String ERROR_HOLIDAY_NAME_REQUIRED = "ERROR HOLIDAY NAME REQUIRED";
    String ERROR_HOLIDAY_CAN_NOT_DELETE = "ERROR HOLIDAY CAN NOT DELETE";
    String ERROR_CUSTOM_FIELD_NAME_ALREADY_EXIST = "ERROR CUSTOM FIELD NAME ALREADY EXIST";
    //
    String ERROR_EMPLOYMENT_INFO_NOT_FOUND = "ERROR EMPLOYMENT INFO NOT FOUND";
    String ERROR_EMPLOYEE_NOT_DELETED = "ERROR EMPLOYEE NOT DELETED";
    String ERROR_PHONE_NUMBER_TYPE_NOT_FOUND = "ERROR PHONE NUMBER TYPE NOT FOUND";
    String ERROR_SKILL_NOT_NULL = "ERROR SKILL NOT NULL";
    String ERROR_INVALID_ACTION_TYPE = "ERROR INVALID ACTION TYPE";
    String ERROR_CAN_NOT_DELETE_TIMESHEET_EMPLOYEE = "ERROR CAN NOT DELETE TIMESHEET EMPLOYEE";
    String ERROR_CAN_NOT_CONFIRM_TIMESHEET_BEFORE_HEAD_OF_DEPARTMENT = "ERROR CAN NOT CONFIRM TIMESHEET BEFORE HEAD OF DEPARTMENT %s";
    String ERROR_TIMESHEET_NOT_CONFIRMING_TODAY = "ERROR TIMESHEET NOT CONFIRMING TODAY";
    String ERROR_NOT_FOUND_DEPARTMENT_FOR_MANAGE = "ERROR NOT FOUND DEPARTMENT FOR MANAGE";
    String ERROR_EMPLOYEE_ATTENDANCE_NOT_FOUND = "ERROR EMPLOYEE ATTENDANCE NOT FOUND";
    String ERROR_NOT_FOUND_VIEW_OBJECT = "ERROR NOT FOUND VIEW OBJECT";
    String ERROR_DATA_NOT_FOUND = "ERROR DATA NOT FOUND";
    String ERROR_VIEW_NOT_FOUND = "ERROR VIEW NOT FOUND";
    String ERROR_TIMESHEET_EMPLOYEE_NOT_FOUND = "ERROR TIMESHEET EMPLOYEE NOT FOUND";
    String ERROR_CHANGED_PROPERTY_NOT_FOUND = "ERROR CHANGED PROPERTY NOT FOUND";
    String ERROR_CAN_NOT_ADD_OR_DELETE = "ERROR CAN NOT ADD OR DELETE";
    String ERROR_NOT_PERMISSION_GET_HUMAN_LIST = "ERROR NOT PERMISSION GET HUMAN LIST";
    String ERROR_THIS_USER_HAVE_MANAGE_VIEW_PERMISSION = "ERROR THIS USER HAVE MANAGE VIEW PERMISSION";
    String ERROR_CAN_NOT_GIVE_FULL_PERMISSION_PUBLIC_VIEW = "ERROR CAN NOT GIVE FULL PERMISSION PUBLIC VIEW";
    String ERROR_NOT_FOUND_USER_VIEW = "ERROR NOT FOUND USER VIEW";
    String ERROR_YOU_DONT_HAVE_PERMISSION_GET_VIEW_LIST = "ERROR YOU DONT HAVE PERMISSION GET VIEW LIST";
    String ERROR_NOT_FOUND_TABLE_MAP = "ERROR NOT FOUND TABLE MAP";
    String ERROR_NOT_FOUND_FIELD_MAP = "ERROR NOT FOUND FIELD MAP";
    String ERROR_YOU_CANNOT_GET_VIEW = "ERROR YOU CANNOT GET VIEW";
    String ERROR_YOU_HAVE_NOT_PERMISSION_ADD_VIEW = "ERROR YOU HAVE NOT PERMISSION ADD VIEW";
    String ERROR_HAVE_NOT_PERMISSION = "ERROR HAVE NOT PERMISSION";
    String ERROR_CUSTOM_FIELD_NOT_FOUND = "ERROR CUSTOM FIELD NOT FOUND";
    String ERROR_NOT_FOUND_CUSTOM_FIELD = "ERROR NOT FOUND CUSTOM FIELD";
    String ERROR_CUSTOM_FIELD_TYPE_CONFIG_REQUIRED = "ERROR CUSTOM FIELD TYPE CONFIG REQUIRED";
    String ERROR_CUSTOM_FIELD_OPTIONS_REQUIRED = "ERROR CUSTOM FIELD OPTIONS REQUIRED";
    String ERROR_CUSTOM_FIELD_RATING_REQUIRED = "ERROR CUSTOM FIELD RATING REQUIRED";

    String ACCESS_IS_DENIED_DUE_TO_INVALID_CREDENTIALS = "ACCESS IS DENIED DUE TO INVALID CREDENTIALS";
    String EXCEPTION_FORBIDDEN = "EXCEPTION FORBIDDEN";
    String EXCEPTION_PATH_NOTFOUND = "EXCEPTION PATH NOTFOUND";
    String EXCEPTION_METHOD_NOT_ALLOWED = "EXCEPTION METHOD NOT ALLOWED";
    String EXCEPTION_NOT_ACCEPTABLE = "EXCEPTION NOT ACCEPTABLE";
    String EXCEPTION_UNSUPPORTED_MEDIA_TYPE = "EXCEPTION UNSUPPORTED MEDIA TYPE";

    String YOU_CAN_NOT_CHANGE_DEFAULT_VIEW = "YOU CAN NOT CHANGE DEFAULT VIEW";
    String YOU_CAN_NOT_CHANGE_VIEW_PUBLICLY = "YOU CAN NOT CHANGE VIEW PUBLICLY";
    String YOU_HAVE_NOT_MANAGE_MEMBER_PERMISSION = "YOU HAVE NOT MANAGE MEMBER PERMISSION";
    String HAVE_NOT_FINANCE_VIEW_HUMAN_PERMISSION_IN_MEMBER = "HAVE NOT FINANCE VIEW HUMAN PERMISSION IN MEMBER";
    String YOU_HAVE_NOT_CHANGE_MEMBER_PERMISSION = "YOU HAVE NOT CHANGE MEMBER PERMISSION";
    String MEMBER_HAVE_NOT_MANAGE_VIEW_PERMISSION = "MEMBER HAVE NOT MANAGE VIEW PERMISSION";
    String MEMBER_PERMISSION_REQUIRED = "MEMBER PERMISSION REQUIRED";
    String REQUIRED_PHONE_NUMBER_TYPE_NAME = "REQUIRED PHONE NUMBER TYPE NAME";
    String REQUIRED_SKILL_NAME = "REQUIRED SKILL NAME";
    String REQUIRED_COLOR_CODE = "REQUIRED COLOR CODE";
    String ERROR_CAN_NOT_FOUND_BRANCH_OTHER_SERVER = "ERROR CAN NOT FOUND BRANCH OTHER SERVER";
    String ERROR_CAN_NOT_FOUND_DEPARTMENT_OTHER_SERVER = "ERROR CAN NOT FOUND DEPARTMENT OTHER SERVER";
    String ERROR_MINIMUM_ONE_DATE_REQUIRED = "ERROR MINIMUM ONE DATE REQUIRED";
    String VIEW_COLUMN_NOT_FOUND = "VIEW COLUMN NOT FOUND";
    String VIEW_NOT_FOUND = "VIEW NOT FOUND";
    String ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_POSITION = "ERROR CAN NOT DELETE DEPARTMENT CONNECT POSITION";
    String ERROR_YOU_CAN_NOT_DELETE_DEPARTMENT_CONNECT_EMPLOYMENT_INFO = "ERROR YOU CAN NOT DELETE DEPARTMENT CONNECT EMPLOYMENT INFO";
    String ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_EMPLOYEE_CATEGORY = "ERROR CAN NOT DELETE DEPARTMENT CONNECT EMPLOYEE CATEGORY";
    String ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_TIMESHEET = "ERROR CAN NOT DELETE DEPARTMENT CONNECT TIMESHEET";
    String ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_TARIFF_GRID = "ERROR CAN NOT DELETE DEPARTMENT CONNECT TARIFF GRID";
    String ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_CONNECT_EMPLOYMENT_INFO = "ERROR CAN NOT DELETE EMPLOYEE CATEGORY CONNECT EMPLOYMENT INFO";
    String ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_CONNECT_TARIFF_GRID = "ERROR CAN NOT DELETE EMPLOYEE CATEGORY CONNECT TARIFF GRID";
    String ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_TYPE_CONNECT_EMPLOYEE_CATEGORY = "ERROR CAN NOT DELETE EMPLOYEE CATEGORY TYPE CONNECT EMPLOYEE CATEGORY";
    String ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_TYPE_CONNECT_EMPLOYMENT_INFO = "ERROR CAN NOT DELETE EMPLOYEE CATEGORY TYPE CONNECT EMPLOYMENT INFO";
    String ERROR_CAN_NOT_DELETE_PHONE_NUMBER_TYPE_CONNECT_EMPLOYEE_PHONE_NUMBER = "ERROR CAN NOT DELETE PHONE NUMBER TYPE CONNECT EMPLOYEE PHONE NUMBER";
    String ERROR_CAN_NOT_DELETE_POSITION_CONNECT_EMPLOYEE_CATEGORY = "ERROR CAN NOT DELETE POSITION CONNECT EMPLOYEE CATEGORY";
    String ERROR_CAN_NOT_DELETE_POSITION_CONNECT_EMPLOYMENT_INFO = "ERROR CAN NOT DELETE POSITION CONNECT EMPLOYMENT INFO";
    String ERROR_CAN_NOT_DELETE_POSITION_CONNECT_TARIFF_GRID = "ERROR CAN NOT DELETE POSITION CONNECT TARIFF GRID";
    String ERROR_CAN_NOT_DELETE_PRIVILEGE_TYPE_CONNECT_TEMPLATE_FOR_SICK = "ERROR CAN NOT DELETE PRIVILEGE TYPE CONNECT TEMPLATE FOR SICK";
    String ERROR_CAN_NOT_DELETE_PRIVILEGE_TYPE_CONNECT_EMPLOYEE = "ERROR CAN NOT DELETE PRIVILEGE TYPE CONNECT EMPLOYEE";
    String ERROR_DATE_MUST_NOT_NULL = "ERROR DATE MUST NOT NULL";
    String ERROR_SAVING_TIME_SHEET_EMPLOYEE = "ERROR SAVING TIME SHEET EMPLOYEE";
    String ERROR_DOES_NOT_SORT_THIS_COLUMN = "ERROR DOES NOT SORT THIS COLUMN";
    String ERROR_DOES_NOT_SORT_THIS_CUSTOM_FIELD_COLUMN = "ERROR DOES NOT SORT THIS CUSTOM FIELD COLUMN";
    String ERROR_DOES_NOT_FILTER_THIS_COLUMN = "ERROR DOES NOT FILTER THIS COLUMN";
    String ERROR_VIEW_FILTER_UNKNOWN_REQUIRED = "ERROR VIEW FILTER UNKNOWN REQUIRED";
    String ERROR_VIEW_FILTER_VALUE = "ERROR VIEW FILTER VALUE";
    String ERROR_VIEW_FILTER_VALUE_OPTION_SELECTED_VALUE_REQUIRED = "ERROR VIEW FILTER VALUE OPTION SELECTED VALUE REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_MIN_VALUE_REQUIRED = "ERROR VIEW FILTER VALUE MIN VALUE REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_MAX_VALUE_REQUIRED = "ERROR VIEW FILTER VALUE MAX VALUE REQUIRED";
    String ERROR_DATE_COMPARE_OPERATOR_TYPE_ENUM_REQUIRED = "ERROR DATE COMPARE OPERATOR TYPE ENUM REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_START_DATE_REQUIRED = "ERROR VIEW FILTER VALUE START DATE REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_END_DATE_REQUIRED = "ERROR VIEW FILTER VALUE END DATE REQUIRED";
    String ERROR_DATE_FILTER_TYPE_ENUM_REQUIRED = "ERROR DATE FILTER TYPE ENUM REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_DATE_X_VALUE_REQUIRED = "ERROR VIEW FILTER VALUE DATE X VALUE REQUIRED";
    String ERROR_VIEW_FILTER_VALUE_REQUIRED = "ERROR VIEW FILTER VALUE REQUIRED";
    String ERROR_DOES_NOT_SEARCH_THIS_COLUMN = "ERROR DOES NOT SEARCH THIS COLUMN";


    //////////////////////////////////////////
    /////////////  NEW VERSION  //////////////
    //////////////////////////////////////////

    String NAME_IS_REQUIRED = "NAME IS REQUIRED";
    String ERROR_YOU_CANNOT_GET_VIEW_NOT_PERMISSION = "ERROR YOU CANNOT GET VIEW NOT PERMISSION";
    String ERROR_VIEW_NOT_FOUND_THIS_NAME = "ERROR VIEW NOT FOUND THIS NAME";
    String ERROR_GENERIC_VIEW = "ERROR GENERIC VIEW";
    String ERROR_REQUIRED_DEPARTMENT_ID = "ERROR REQUIRED DEPARTMENT ID";
    String ERROR_CAN_ALREADY_CONFIRM_DEPARTMENT = "ERROR CAN ALREADY CONFIRM DEPARTMENT %s";
    String ERROR_YOU_HAVE_NO_SUCH_RIGHT = "ERROR YOU HAVE NO SUCH RIGHT";
    String ERROR_CAN_NOT_CONFIRM_DEPARTMENT_STATUS = "ERROR CAN NOT CONFIRM DEPARTMENT STATUS %s";
    String ERROR_THIS_USER_ALREADY_EXISTS = "ERROR THIS USER ALREADY EXISTS";
    String ERROR_THIS_PHONE_NUMBER_ALREADY_USE_OTHER_EMPLOYEE = "ERROR THIS PHONE NUMBER ALREADY USE OTHER EMPLOYEE";
}
