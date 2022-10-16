package ai.ecma.appstaff.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

//ERP NING HRML MODULEDA MAVJUD BO'LGAN PAGELAR
@Getter
@AllArgsConstructor
public enum PageEnum {
    PRIVILEGE_TYPE("Imtiyoz turi", "Privilege type", "Привилеге тйпе", DepartmentEnum.COMPANY, null, null, false),
    TEMPLATE_FOR_SICK("Kasallik varaqasi uchun shablon", "Template for_sick", "Темплате фор_сиcк", DepartmentEnum.COMPANY, null, null, false),
    DEPARTMENTS("Bo’limlar", "Departments", "Департменц", DepartmentEnum.COMPANY, null, null, false),
    HOLIDAYS("Bayramlar", "Holidays", "Ҳолидайс", DepartmentEnum.COMPANY, null, null, false),
    POSITIONS("Lavozimlar", "Positions", "Поситионс", DepartmentEnum.COMPANY, null, null, false),
    EMPLOYEE_CATEGORY_TYPE("Kategoriyalar turi", "Category type", "Cатегорй тйпе", DepartmentEnum.COMPANY, null, null, false),
    EMPLOYEE_CATEGORY("Xodim kategoriyasi", "Employee category", "Емплойее cатегорй", DepartmentEnum.COMPANY, null, null, false),
    TARIFF_GRID("Ta’rif setkasi ", "Tariff grid", "Тарифф грид", DepartmentEnum.COMPANY, null, null, false),
    EMPLOYEES("Hodimlar ", "Hodimlar", "Hodimlar", DepartmentEnum.COMPANY, null, null, false),
    TIMESHEET("TIMESHEET", "TIMESHEET", "TIMESHEET", DepartmentEnum.COMPANY, null, null, false),
    TIMESHEET_FINANCE("TIMESHEET_FINANCE", "TIMESHEET_FINANCE", "TIMESHEET_FINANCE", DepartmentEnum.FINANCE, null, null, false),

    MENTOR("MENTOR", "MENTOR", "MENTOR", DepartmentEnum.ACADEMIC, null, null, false),

    // DELETED TRUE
    CONFIG("Sozlama", "Config", "Cонфиг", DepartmentEnum.COMPANY, null, null, false),

    TURNIKET_EMPLOYEE("turniket employee", "turniket employee", "turniket employee", DepartmentEnum.COMPANY, null, null, false),

    ORG_STRUCTURE("ORG_STRUCTURE", "ORG_STRUCTURE", "MENTOR", DepartmentEnum.COMPANY, null, null, false),

    ;


    private String titleUz;
    private String titleEn;
    private String titleRu;

    private final DepartmentEnum department;

    private final DepartmentEnum beforeDepartment;

    private final PageEnum before;

    private final boolean deleted;
}
