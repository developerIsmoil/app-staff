package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.enums.DepartmentEnum;
import ai.ecma.appstaff.enums.PageEnum;
import ai.ecma.appstaff.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//MICROSERVICE MIZ TOMONIDAN QO'SHILADIGAN PERMISSIONLAR UCHUN
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionAddDTO implements Serializable {

    //QO'SHILADIGAN PERMISSIONNING ADMIN TOMONGA KO'RINADIGAN QISMI(Kursni qo'shish, Kursni tahrirlash, Kursni o'chirish)
    private String title;


    //HUQUQNING SISTEMADA FOYDALANADIGAN NOMI (ADD_COURSE, EDIT_COURSE, DELETE_COURSE)
    private PermissionEnum name;

    //QO'SHAYOTGAN PERMISSION QAYSI MODULE ICHIDA EKANLIGI
    private String moduleName;

    //QO'SHAYOTGAN PERMISSION QAYSI DEPARTMENT ICHIDA EKANLIGI
    private DepartmentEnum departmentName;

    //QO'SHAYOTGAN PERMISSION QAYSI PAGE ICHIDA EKANLIGI
    private PageEnum pageName;

    //QO'SHAYOTGAN PERMISSION NING AVVALGI MODULE QAYSI BO'LGANLIGI
    private String beforeModuleName;

    //QO'SHAYOTGAN PERMISSION NING AVVALGI DEPARTMENT QAYSI BO'LGANLIGI
    private DepartmentEnum beforeDepartmentName;

    //QO'SHAYOTGAN PERMISSION NING AVVALGI PAGE QAYSI BO'LGANLIGI
    private PageEnum beforePageName;

    //QO'SHAYOTGAN PERMISSION TAHRIRLANAYOTGAN BO'LSA, AVVAL QAYSI NOMDA EDI
    //AGAR beforeName NULL BO'LSA YANGI QO'SHILAYOTGAN BO'LADI
    private PermissionEnum beforeName;

    //QO'SHAYOTGAN PERMISSION O'CHIRILISHI KERAK BO'LSA TRUE BERILADI
    private boolean deleted;
}









