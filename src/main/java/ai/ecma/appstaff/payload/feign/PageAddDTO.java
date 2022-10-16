package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.enums.DepartmentEnum;
import ai.ecma.appstaff.enums.PageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * BOSHQA MICROSERVICE LAR TOMONIDAN QO'SHILADIGAN PAGELAR UCHUN
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageAddDTO implements Serializable {

    //QO'SHILADIGAN PAGE NING ADMIN TOMONGA KO'RINADIGAN QISMI(Kurslar, Modullar, Xodimlar...)
    private String titleUz;

    //QO'SHILADIGAN PAGE NING ADMIN TOMONGA KO'RINADIGAN QISMI(Kurslar, Modullar, Xodimlar...)
    private String titleEn;

    //QO'SHILADIGAN PAGE NING ADMIN TOMONGA KO'RINADIGAN QISMI(Kurslar, Modullar, Xodimlar...)
    private String titleRu;

    //PAGE NING SISTEMADA FOYDALANADIGAN NOMI (COURSE, EMPLOYEE ...)
    private PageEnum name;

    //QO'SHAYOTGAN PAGE QAYSI MODULE ICHIDA EKANLIGI
    private String moduleName;

    //QO'SHAYOTGAN PAGE QAYSI DEPARTMENT ICHIDA EKANLIGI
    private DepartmentEnum departmentName;

    //QO'SHAYOTGAN PAGE NING AVVALGI MODULE QAYSI BO'LGANLIGI
    private String beforeModuleName;

    //QO'SHAYOTGAN PAGE NING AVVALGI DEPARTMENT QAYSI BO'LGANLIGI
    private DepartmentEnum beforeDepartmentName;

    //QO'SHAYOTGAN PAGE TAHRIRLANAYOTGAN BO'LSA, AVVAL QAYSI NOMDA EDI
    //AGAR BEFORE NULL BO'LSA YANGI QO'SHILAYOTGAN BO'LADI
    private PageEnum beforeName;

    //QO'SHAYOTGAN PAGE O'CHIRILISHI KERAK BO'LSA TRUE BERILADI
    private boolean deleted;
}







