package ai.ecma.appstaff.enums;

import lombok.Getter;

//ERP NING HRML MODULE DA MAVJUD BO'LGAN DEPARTMENTLAR
@Getter
public enum DepartmentEnum {

    COMPANY(ModuleEnum.HRM),

    ACADEMIC_CONTENT(ModuleEnum.ACADEMIC),

    ACADEMIC(ModuleEnum.ACADEMIC),

    FINANCE(ModuleEnum.FINANCE);

    private final ModuleEnum module;

    DepartmentEnum(ModuleEnum module) {
        this.module = module;
    }
}


