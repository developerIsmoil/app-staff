package ai.ecma.appstaff.enums;

import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// USERLARNING TIZIMDAGI HUQUQLARI
public enum PermissionEnum {

    MANAGE_TIME_SHEET_FINANCE("MANAGE_TIME_SHEET_FINANCE", PageEnum.TIMESHEET_FINANCE, null, null, false),
    MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS("MANAGE_TIME_SHEET_WITH_ALL_DEPARTMENTS", PageEnum.TIMESHEET, null, null, false),
    MANAGE_TIME_SHEET_ONLY_OWN_DEPARTMENTS("MANAGE_TIME_SHEET_ONLY_OWN_DEPARTMENTS", PageEnum.TIMESHEET, null, null, false),

    GET_VIEW_EMPLOYEE("GET_VIEW_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),
    FINANCE_MANAGE_VIEW_EMPLOYEE("FINANCE_MANAGE_VIEW_EMPLOYEE", PageEnum.EMPLOYEES, TableNameConstant.EMPLOYEE, null, false),
    GET_VIEW_TIMESHEET("GET_VIEW_TIMESHEET", PageEnum.TIMESHEET, null, null, false),
    FINANCE_MANAGE_VIEW_TIMESHEET("FINANCE_MANAGE_VIEW_TIMESHEET", PageEnum.TIMESHEET, TableNameConstant.TIMESHEET, null, false),
    //
    HRM_ADD_DEPARTMENT("HRM_ADD_DEPARTMENT", PageEnum.DEPARTMENTS, null, null, false),
    HRM_EDIT_DEPARTMENT("HRM_EDIT_DEPARTMENT", PageEnum.DEPARTMENTS, null, null, false),
    HRM_GET_ALL_DEPARTMENT("HRM_GET_ALL_DEPARTMENT", PageEnum.DEPARTMENTS, null, null, false),
    HRM_GET_ONE_DEPARTMENT("HRM_GET_ONE_DEPARTMENT", PageEnum.DEPARTMENTS, null, null, false),
    HRM_DELETE_DEPARTMENT("HRM_DELETE_DEPARTMENT", PageEnum.DEPARTMENTS, null, null, false),

    //
    HRM_ADD_EMPLOYEE_CATEGORY("HRM_ADD_EMPLOYEE_CATEGORY", PageEnum.EMPLOYEE_CATEGORY, null, null, false),

    HRM_EDIT_EMPLOYEE_CATEGORY("HRM_EDIT_EMPLOYEE_CATEGORY", PageEnum.EMPLOYEE_CATEGORY, null, null, false),

    HRM_GET_ALL_EMPLOYEE_CATEGORY("HRM_GET_ALL_EMPLOYEE_CATEGORY", PageEnum.EMPLOYEE_CATEGORY, null, null, false),

    HRM_GET_ONE_EMPLOYEE_CATEGORY("HRM_GET_ONE_EMPLOYEE_CATEGORY", PageEnum.EMPLOYEE_CATEGORY, null, null, false),

    HRM_DELETE_EMPLOYEE_CATEGORY("HRM_DELETE_EMPLOYEE_CATEGORY", PageEnum.EMPLOYEE_CATEGORY, null, null, false),

    //
    HRM_ADD_EMPLOYEE_CATEGORY_TYPE("HRM_ADD_EMPLOYEE_CATEGORY_TYPE", PageEnum.EMPLOYEE_CATEGORY_TYPE, null, null, false),

    HRM_EDIT_EMPLOYEE_CATEGORY_TYPE("HRM_EDIT_EMPLOYEE_CATEGORY_TYPE", PageEnum.EMPLOYEE_CATEGORY_TYPE, null, null, false),

    HRM_GET_ALL_EMPLOYEE_CATEGORY_TYPE("HRM_GET_ALL_EMPLOYEE_CATEGORY_TYPE", PageEnum.EMPLOYEE_CATEGORY_TYPE, null, null, false),

    HRM_GET_ONE_EMPLOYEE_CATEGORY_TYPE("HRM_GET_ONE_EMPLOYEE_CATEGORY_TYPE", PageEnum.EMPLOYEE_CATEGORY_TYPE, null, null, false),

    HRM_DELETE_EMPLOYEE_CATEGORY_TYPE("HRM_DELETE_EMPLOYEE_CATEGORY_TYPE", PageEnum.EMPLOYEE_CATEGORY_TYPE, null, null, false),

    //
    HRM_ADD_EMPLOYEE("HRM_ADD_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    HRM_EDIT_EMPLOYEE("HRM_EDIT_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_ALL_EMPLOYEE("HRM_GET_ALL_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_ONE_EMPLOYEE("HRM_GET_ONE_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    HRM_DELETE_EMPLOYEE("HRM_DELETE_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_VIEW_TYPES("HRM_GET_VIEW_TYPES", PageEnum.EMPLOYEES, null, null, false),

    //
    HRM_ADD_HOLIDAY("HRM_ADD_HOLIDAY", PageEnum.HOLIDAYS, null, null, false),

    HRM_EDIT_HOLIDAY("HRM_EDIT_HOLIDAY", PageEnum.HOLIDAYS, null, null, false),

    HRM_GET_ALL_HOLIDAY("HRM_GET_ALL_HOLIDAY", PageEnum.HOLIDAYS, null, null, false),

    HRM_GET_ONE_HOLIDAY("HRM_GET_ONE_HOLIDAY", PageEnum.HOLIDAYS, null, null, false),

    HRM_DELETE_HOLIDAY("HRM_DELETE_HOLIDAY", PageEnum.HOLIDAYS, null, null, false),

    //
    HRM_ADD_POSITION("HRM_ADD_POSITION", PageEnum.POSITIONS, null, null, false),

    HRM_EDIT_POSITION("HRM_EDIT_POSITION", PageEnum.POSITIONS, null, null, false),

    HRM_GET_ALL_POSITION("HRM_GET_ALL_POSITION", PageEnum.POSITIONS, null, null, false),

    HRM_GET_ONE_POSITION("HRM_GET_ONE_POSITION", PageEnum.POSITIONS, null, null, false),

    HRM_DELETE_POSITION("HRM_DELETE_POSITION", PageEnum.POSITIONS, null, null, false),

    //
    HRM_ADD_PRIVILEGE_TYPE("HRM_ADD_PRIVILEGE_TYPE", PageEnum.PRIVILEGE_TYPE, null, null, false),

    HRM_EDIT_PRIVILEGE_TYPE("HRM_EDIT_PRIVILEGE_TYPE", PageEnum.PRIVILEGE_TYPE, null, null, false),

    HRM_GET_ALL_PRIVILEGE_TYPE("HRM_GET_ALL_PRIVILEGE_TYPE", PageEnum.PRIVILEGE_TYPE, null, null, false),

    HRM_GET_ONE_PRIVILEGE_TYPE("HRM_GET_ONE_PRIVILEGE_TYPE", PageEnum.PRIVILEGE_TYPE, null, null, false),

    HRM_DELETE_PRIVILEGE_TYPE("HRM_DELETE_PRIVILEGE_TYPE", PageEnum.PRIVILEGE_TYPE, null, null, false),

    //
    HRM_ADD_TARIFF_GRID("HRM_ADD_TARIFF_GRID", PageEnum.TARIFF_GRID, null, null, false),

    HRM_EDIT_TARIFF_GRID("HRM_EDIT_TARIFF_GRID", PageEnum.TARIFF_GRID, null, null, false),

    HRM_GET_ALL_TARIFF_GRID("HRM_GET_ALL_TARIFF_GRID", PageEnum.TARIFF_GRID, null, null, false),

    HRM_GET_ONE_TARIFF_GRID("HRM_GET_ONE_TARIFF_GRID", PageEnum.TARIFF_GRID, null, null, false),

    HRM_DELETE_TARIFF_GRID("HRM_DELETE_TARIFF_GRID", PageEnum.TARIFF_GRID, null, null, false),

    //
    HRM_ADD_TEMPLATE_FOR_SICK("HRM_ADD_TEMPLATE_FOR_SICK", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    HRM_EDIT_TEMPLATE_FOR_SICK("HRM_EDIT_TEMPLATE_FOR_SICK", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    HRM_GET_ALL_TEMPLATE_FOR_SICK("HRM_GET_ALL_TEMPLATE_FOR_SICK", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    HRM_GET_ONE_TEMPLATE_FOR_SICK("HRM_GET_ONE_TEMPLATE_FOR_SICK", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    HRM_GET_ONE_TEMPLATE_FOR_SICK_ID("HRM_GET_ONE_TEMPLATE_FOR_SICK_ID", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    HRM_DELETE_TEMPLATE_FOR_SICK("HRM_DELETE_TEMPLATE_FOR_SICK", PageEnum.TEMPLATE_FOR_SICK, null, null, false),

    //
    HRM_CHANGE_CONFIG("HRM_CHANGE_CONFIG", PageEnum.CONFIG, null, null, false),

    FINANCE_VIEW_HUMAN("FINANCE_VIEW_HUMAN", PageEnum.CONFIG, "", null, false),

    //
    HRM_GET_EMPLOYEE_VIEW_TYPES("HRM_GET_EMPLOYEE_VIEW_TYPES", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_EMPLOYEE_VIEW("HRM_GET_EMPLOYEE_VIEW", PageEnum.EMPLOYEES, null, null, false),

    HRM_GENERIC_EMPLOYEE_VIEW("HRM_GENERIC_EMPLOYEE_VIEW", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_EMPLOYEE_VIEW_DATA("HRM_GET_EMPLOYEE_VIEW_DATA", PageEnum.EMPLOYEES, null, null, false),

    HRM_EDIT_EMPLOYEE_VIEW_ROW_DATA("HRM_EDIT_EMPLOYEE_VIEW_ROW_DATA", PageEnum.EMPLOYEES, null, null, false),
    //

    //
    HRM_GET_TIMESHEET_VIEW_TYPES("HRM_GET_TIMESHEET_VIEW_TYPES", PageEnum.TIMESHEET, null, null, false),

    HRM_GET_TIMESHEET_VIEW("HRM_GET_TIMESHEET_VIEW", PageEnum.TIMESHEET, null, null, false),

    HRM_GENERIC_TIMESHEET_VIEW("HRM_GENERIC_TIMESHEET_VIEW", PageEnum.TIMESHEET, null, null, false),

    HRM_GET_TIMESHEET_VIEW_DATA("HRM_GET_TIMESHEET_VIEW_DATA", PageEnum.TIMESHEET, null, null, false),

    HRM_EDIT_TIMESHEET_VIEW_ROW_DATA("HRM_EDIT_TIMESHEET_VIEW_ROW_DATA", PageEnum.TIMESHEET, null, null, false),
    //

    //
    HRM_GET_TIMESHEET_FINANCE_VIEW_TYPES("HRM_GET_TIMESHEET_FINANCE_VIEW_TYPES", PageEnum.TIMESHEET_FINANCE, null, null, false),

    HRM_GET_TIMESHEET_FINANCE_VIEW("HRM_GET_TIMESHEET_FINANCE_VIEW", PageEnum.TIMESHEET_FINANCE, null, null, false),

    HRM_GENERIC_TIMESHEET_FINANCE_VIEW("HRM_GENERIC_TIMESHEET_FINANCE_VIEW", PageEnum.TIMESHEET_FINANCE, null, null, false),

    HRM_GET_TIMESHEET_FINANCE_VIEW_DATA("HRM_GET_TIMESHEET_FINANCE_VIEW_DATA", PageEnum.TIMESHEET_FINANCE, null, null, false),

    HRM_EDIT_TIMESHEET_FINANCE_VIEW_ROW_DATA("HRM_EDIT_TIMESHEET_FINANCE_VIEW_ROW_DATA", PageEnum.TIMESHEET_FINANCE, null, null, false),
    //

    FINANCE_ADD_CUSTOM_FIELD("FINANCE_ADD_CUSTOM_FIELD", PageEnum.EMPLOYEES, null, null, false),

    FINANCE_DELETE_CUSTOM_FIELD("FINANCE_DELETE_CUSTOM_FIELD", PageEnum.EMPLOYEES, null, null, false),

    FINANCE_EDIT_CUSTOM_FIELD("FINANCE_EDIT_CUSTOM_FIELD", PageEnum.EMPLOYEES, null, null, false),

    FINANCE_ADD_CUSTOM_FIELD_VALUE("FINANCE_ADD_CUSTOM_FIELD_VALUE", PageEnum.EMPLOYEES, null, null, false),

    DUPLICATE_VIEW("DUPLICATE_VIEW", PageEnum.EMPLOYEES, null, null, false),

    UPDATE_VIEW("UPDATE_VIEW", PageEnum.EMPLOYEES, null, null, false),

    EDIT_VIEW("EDIT_VIEW", PageEnum.EMPLOYEES, null, null, false),

    ADD_VIEW("ADD_VIEW", PageEnum.EMPLOYEES, null, null, false),

    SHARING_PERMISSION_VIEW("SHARING_PERMISSION_VIEW", PageEnum.EMPLOYEES, null, null, false),

    CHANGE_MEMBER_PERMISSION_VIEW("CHANGE_MEMBER_PERMISSION_VIEW", PageEnum.EMPLOYEES, null, null, false),

    CHANGE_PUBLICLY_VIEW("CHANGE_PUBLICLY_VIEW", PageEnum.EMPLOYEES, null, null, false),

    FINANCE_MANAGE_FINANCE_VIEW_HUMAN("FINANCE_MANAGE_FINANCE_VIEW_HUMAN", PageEnum.EMPLOYEES, null, null, false),

    HRM_GET_ROLE_LIST("HRM_GET_ROLE_LIST", PageEnum.EMPLOYEES, null, null, false),

    HRM_RESIGNATION_EMPLOYEE("HRM_RESIGNATION_EMPLOYEE", PageEnum.EMPLOYEES, null, null, false),

    VIEW_MENTOR_VIEW("HRM_RESIGNATION_EMPLOYEE", PageEnum.MENTOR, null, null, false),
    EDIT_MENTOR_VIEW("HRM_RESIGNATION_EMPLOYEE", PageEnum.MENTOR, null, null, false),

    MANAGE_MENTOR_VIEW("MANAGE_MENTOR_VIEW", PageEnum.MENTOR, null, null, false),

    HRM_CAN_CHANGE_EMPLOYEE_ATTENDANCE("HRM_CAN_CHANGE_EMPLOYEE_ATTENDANCE", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD("HRM_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT", PageEnum.TIMESHEET, null, null, false),
    HRM_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT("HRM_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT", PageEnum.TIMESHEET, null, null, false),

    FINANCE_CAN_CHANGE_EMPLOYEE_ATTENDANCE("FINANCE_CAN_CHANGE_EMPLOYEE_ATTENDANCE", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD("FINANCE_CAN_CHANGE_TIMESHEET_CUSTOM_FIELD", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_BONUS_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_PREMIUM_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_ADVANCE_SALARY_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_RETENTION_SALARY_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_ADDITION_SALARY_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),
    FINANCE_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT("FINANCE_CAN_CHANGE_EMPLOYEE_TAX_AMOUNT_AMOUNT", PageEnum.TIMESHEET_FINANCE, null, null, false),


    //TURNIKET

    //TURNIKET PERMISSION LAR GET HISTORY
    GET_EMPLOYEE_TURNIKET_HISTORY("GET_EMPLOYEE_TURNIKET_HISTORY", PageEnum.TURNIKET_EMPLOYEE, null, null, false),

    //ADD USER IN TURNIKET
    ADD_EMPLOYEE_IN_TURNIKET("ADD_EMPLOYEE_IN_TURNIKET", PageEnum.TURNIKET_EMPLOYEE, null, null, false),

    //DELETE USER FROM TURNIKET
    DELETE_EMPLOYEE_IN_TURNIKET("DELETE_EMPLOYEE_IN_TURNIKET", PageEnum.TURNIKET_EMPLOYEE, null, null, false),

    //EDIT USER FROM TURNIKET
    EDIT_EMPLOYEE_IN_TURNIKET("EDIT_EMPLOYEE_IN_TURNIKET", PageEnum.TURNIKET_EMPLOYEE, null, null, false),


    GET_ORG_STRUCTURE("GET_ORG_STRUCTURE", PageEnum.ORG_STRUCTURE, null, null, false);


    private String title;

    //QAYSI PAGE GA TEGISHLI EKANLIGI
    private PageEnum page;

    //BU ENUMLARNI USERGA NISBATAN VIEWLAR UCHUN HUQUQLARINI TEKSHIRISH UCHUN
    private String tableName;

    private PermissionEnum before;

    private boolean deleted;

}
