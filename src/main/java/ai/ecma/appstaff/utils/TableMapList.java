package ai.ecma.appstaff.utils;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum.*;


public interface TableMapList {

    Map<String, Map<String, Map<EntityColumnMapValuesKeyEnum, String>>> ENTITY_FIELDS = new HashMap<>() {
        {
            put(TableNameConstant.EMPLOYEE,
                    new LinkedHashMap<>() {{
                        put(ColumnKey.ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.FIRST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.LAST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.PHONE_NUMBER, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.MIDDLE_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.BIRTH_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                        }});
                        put(ColumnKey.MARITAL_STATUS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.GENDER, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.EMAIL, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.EMAIL.name());
                        }});
                        put(ColumnKey.PASSPORT_SERIAL, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.PASSPORT_NUMBER, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.PASSPORT_GIVEN_ORGANISATION, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.PASSPORT_GIVEN_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                        }});
                        put(ColumnKey.PASSPORT_EXPIRE_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                        }});
                        put(ColumnKey.PERMANENT_ADDRESS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LONG_TEXT.name());
                        }});
                        put(ColumnKey.CURRENT_ADDRESS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LONG_TEXT.name());
                        }});
                        put(ColumnKey.PERSONAL_NUMBER, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.ACCESS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.CHECKBOX.name());
                        }});
                        put(ColumnKey.USER_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.ROLES, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LABELS.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(OTHER_SERVICE, RestConstants.AUTH_SERVICE);
                        }});
                        put(ColumnKey.CREATED_AT, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                            put(FILTERABLE, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                        }});

                        put(ColumnKey.HIRE_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, "employee_id");
                            put(DIRECTIONAL_COLUMN, "hire_date");
                            put(ORDERING_COLUMN, "hire_date");
                            put(MIN, "hire_date");
                        }});

//                      TODO  // // // // // YANGILARI
                        put(ColumnKey.BRANCH_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LABELS.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.BRANCH_ID);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.DEPARTMENT_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LABELS.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.DEPARTMENT_ID);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.POSITION_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LABELS.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.POSITION_ID);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.LABELS.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.PAYMENT_CRITERIA_TYPE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.PAYMENT_CRITERIA_TYPE);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.YES);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.CONTRACT_FORM, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.CONTRACT_FORM);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.YES);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.EMPLOYEE_MODE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.EMPLOYEE_MODE);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.YES);
                            put(FILTERABLE, RestConstants.YES);
                        }});
                        put(ColumnKey.MANAGE_TABLE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.CHECKBOX.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, ColumnKey.EMPLOYEE_ID);
                            put(DIRECTIONAL_COLUMN, ColumnKey.MANAGE_TABLE);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                        }});


                    }}
            );


            put(TableNameConstant.TIMESHEET_EMPLOYEE,
                    new LinkedHashMap<>() {{
                        put(ColumnKey.ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(SHOW_COLUMN, RestConstants.NO);

                        }});
                        put(ColumnKey.TIMESHEET_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(SHOW_COLUMN, RestConstants.NO);
                        }});
                        put(ColumnKey.DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                            put(FOREIGN_TABLE, TableNameConstant.TIMESHEET);
                            put(FOREIGN_TABLE_ID, ColumnKey.TIMESHEET_ID);
                            put(ORDERING_COLUMN, ColumnKey.DATE);
                            put(SEARCHING_COLUMN, ColumnKey.DATE);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                        }});
                        put(ColumnKey.TIMESHEET_STATUS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.ENUM_DROPDOWN.name());
                            put(FOREIGN_TABLE, TableNameConstant.TIMESHEET);
                            put(FOREIGN_TABLE_ID, ColumnKey.TIMESHEET_ID);
                            put(ORDERING_COLUMN, ColumnKey.TIMESHEET_STATUS);
                            put(SEARCHING_COLUMN, ColumnKey.TIMESHEET_STATUS);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                        }});
                        put(ColumnKey.CONFIRM_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE_TIME.name());
                            put(FOREIGN_TABLE, TableNameConstant.TIMESHEET);
                            put(FOREIGN_TABLE_ID, ColumnKey.TIMESHEET_ID);
                            put(ORDERING_COLUMN, ColumnKey.CONFIRM_DATE);
                            put(SEARCHING_COLUMN, ColumnKey.CONFIRM_DATE);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                        }});
                        put(ColumnKey.FIRST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "first_name");
                        }});
                        put(ColumnKey.LAST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "last_name");
                        }});
                        put(ColumnKey.MIDDLE_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "middle_name");
                        }});
                        put(ColumnKey.BRANCH_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, ColumnKey.BRANCH_ID);
                            put(OTHER_SERVICE, RestConstants.BRANCH_SERVICE);
                        }});
                        put(ColumnKey.POSITION_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.POSITION);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "position_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "name");
                        }});
                        put(ColumnKey.DEPARTMENT_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.DEPARTMENT);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "department_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "name");
                        }});
                        put(ColumnKey.PAYMENT_CRITERIA_TYPE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.ENUM_DROPDOWN.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, "employee_id");
                            put(DIRECTIONAL_COLUMN, ColumnKey.PAYMENT_CRITERIA_TYPE);
                        }});
                        put(ColumnKey.SALARY, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.BONUS, new HashMap<>() {{
//                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.WORK_DAYS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                        }});
                        put(ColumnKey.WORK_HOURS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                        }});
                        put(ColumnKey.WORKED_HOURS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                        }});
                        put(ColumnKey.WORKED_DAYS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                        }});
                        put(ColumnKey.PREMIUM, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.ADVANCE_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.RETENTION_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.ADDITION_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.TOTAL_SALARY, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.TAX_AMOUNT, new HashMap<>() {{
//                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.PAID_SALARY, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(INLINE_SUM, "SUM");
                        }});
                        put(ColumnKey.CREATED_AT, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE_TIME.name());
                            put(FILTERABLE, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                        }});

                        //todo buni keyinroq yozamiz. JONIBEK
//                        put(ColumnKey.SALARY_TEST, new HashMap<>() {{
//                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
//                        }});
//                        put(ColumnKey.SALARY_RESULT, new HashMap<>() {{
//                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
//                            put(FUNCTIONAL_OPERATION, new HashMap() {{
//                                put("columns", new ArrayList<>(Arrays.asList(
//                                        ColumnKey.SALARY_TEST,
//                                        ColumnKey.SALARY
//                                )));
//                            }});
//                        }});

                    }}
            );

            put(TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE,
                    new LinkedHashMap<>() {{
                        put(ColumnKey.ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                        }});
                        put(ColumnKey.FIRST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "first_name");

                        }});
                        put(ColumnKey.LAST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "last_name");
                        }});
                        put(ColumnKey.MIDDLE_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.EMPLOYEE);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "employee_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "middle_name");
                        }});
                        put(ColumnKey.BRANCH_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, ColumnKey.BRANCH_ID);
                            put(OTHER_SERVICE, RestConstants.BRANCH_SERVICE);
                        }});
                        put(ColumnKey.POSITION_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.POSITION);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "position_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "name");
                        }});
                        put(ColumnKey.DEPARTMENT_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(RELATIONAL_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(RELATIONAL_TABLE_ID, "employment_info_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE, TableNameConstant.DEPARTMENT);
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_ID, "department_id");
                            put(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN, "name");
                        }});

                        put(ColumnKey.PAYMENT_CRITERIA_TYPE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.ENUM_DROPDOWN.name());
                            put(DIRECTIONAL_FOREIGN_TABLE, TableNameConstant.EMPLOYMENT_INFO);
                            put(DIRECTIONAL_OURS_COLUMN, "id");
                            put(DIRECTIONAL_COLUMN, ColumnKey.PAYMENT_CRITERIA_TYPE);
                        }});

                        put(ColumnKey.SALARY, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.WORK_HOURS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.WORKED_HOURS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.WORK_DAYS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.WORKED_DAYS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.NUMBER.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.BONUS, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.PREMIUM, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.ADVANCE_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.ADDITION_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.TAX_AMOUNT, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.MUST_BE_PAID, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.RETENTION_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.TOTAL_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.PAID_SALARY, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                            put(OTHER_SERVICE, RestConstants.FINANCE_SERVICE);
                            put(NOT_FROM_DB, RestConstants.YES);
                        }});
                        put(ColumnKey.CREATED_AT, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                            put(FILTERABLE, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                        }});
                    }}
            );
            put(TableNameConstant.TARIFF_GRID,
                    new LinkedHashMap<>() {{
                        put(ColumnKey.ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(SHOW_COLUMN, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());

                        }});
                        put(ColumnKey.BRANCH_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                        }});
                        put(ColumnKey.COMPANY_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                        }});

                        put(ColumnKey.DEPARTMENT_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(FOREIGN_TABLE_ID, ColumnKey.DEPARTMENT_ID);
                            put(FOREIGN_TABLE, ColumnKey.DEPARTMENT);
                            put(ORDERING_COLUMN, ColumnKey.NAME);
                            put(SEARCHING_COLUMN, ColumnKey.NAME);
                        }});
                        put(ColumnKey.POSITION_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DROPDOWN.name());
                            put(FOREIGN_TABLE_ID, ColumnKey.POSITION_ID);
                            put(FOREIGN_TABLE, ColumnKey.DEPARTMENT);
                            put(ORDERING_COLUMN, ColumnKey.NAME);
                            put(SEARCHING_COLUMN, ColumnKey.NAME);
                        }});

                        put(ColumnKey.EMPLOYEE_CATEGORY_ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(FOREIGN_TABLE_ID, ColumnKey.EMPLOYEE_CATEGORY_ID);
                            put(FOREIGN_TABLE, TableNameConstant.EMPLOYEE_CATEGORY);
                            put(ORDERING_COLUMN, ColumnKey.NAME);
                            put(SEARCHING_COLUMN, ColumnKey.NAME);
                        }});
                        put(ColumnKey.PAYMENT_CRITERIA_TYPE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.ENUM_DROPDOWN.name());
                        }});

                        put(ColumnKey.PAYMENT_AMOUNT, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.MONEY.name());
                        }});
                        put(ColumnKey.CREATED_AT, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE_TIME.name());
                        }});
                        put(ColumnKey.ACTIVE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.CHECKBOX.name());
                            put(SEARCHABLE, RestConstants.NO);
                        }});
                    }}
            );

            put(TableNameConstant.MENTOR,
                    new LinkedHashMap<>() {{
                        put(ColumnKey.ID, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(SORTABLE, RestConstants.NO);
                            put(FILTERABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.FIRST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.LAST_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.PHONE_NUMBER, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                            put(ROOT, RestConstants.YES);
                        }});
                        put(ColumnKey.MIDDLE_NAME, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.SHORT_TEXT.name());
                        }});
                        put(ColumnKey.BIRTH_DATE, new HashMap<>() {{
                            put(ENABLED, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                        }});
                        put(ColumnKey.CREATED_AT, new HashMap<>() {{
                            put(TYPE, CustomFieldTypeEnum.DATE.name());
                            put(FILTERABLE, RestConstants.NO);
                            put(SEARCHABLE, RestConstants.NO);
                        }});

                    }}
            );

            /*
              viewni table name va db da qaysi tabledan columnlarni qidiriyotganini korsatuvchi map
              bu mapdan table name olinadi generic view va generic get data yollarida
             */
            Map<String, String> TABLE_NAME = new LinkedHashMap<>() {{

                put(TableNameConstant.MENTOR, TableNameConstant.EMPLOYEE);

                put(TableNameConstant.TARIFF_GRID, TableNameConstant.TARIFF_GRID);

                put(TableNameConstant.TIMESHEET_EMPLOYEE, TableNameConstant.TIMESHEET_EMPLOYEE);

                put(TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE, TableNameConstant.TIMESHEET_EMPLOYEE);

                put(TableNameConstant.EMPLOYEE, TableNameConstant.EMPLOYEE);

            }};
        }
    };
}