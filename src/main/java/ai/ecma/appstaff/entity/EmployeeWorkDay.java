package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.WeekDayEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

// BUNDA HODIMNING ISH GRAFIGI BO'LADI
// MASALAN HAVFTANING QAYSI KUNLARI VA SOAT NECHCHIDAN NECHCHIGACHA ISHLASHI
// TUSHLIK VAQTI BOR BO'LSA U QAYSI VAQT ORALIG'IDA EKANLIGI KIRITIB BORILADI
// BUNDA HAR BIR HODIM UCHUN 7 TA ROW B'LIB ULARNING HAR BIRI HAVFTANING BIR KUNI HISOBLANADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_WORK_DAY)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_WORK_DAY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeWorkDay extends AbsUUIDUserAuditEntity {

    // QAYSI HODIM UCHUN ISH GRAFIGILIGI
    // Employee BU HODIM EmploymentInfo ESA QAYSI BO'LIMDA QANDAY LAVOZIMDA ISHLASHI
    // demak hodimning qaysi bo'limda qaysi lavozimda ishlashiga qarab ish grafigi o'zgarishi mumkin bo'ladi
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYMENT_INFO_ID)
    private EmploymentInfo employmentInfo;

    // QAYSI KUNLIGI
    // MASALAN ( Dushanba )
    @Column(name = ColumnKey.WEEK_DAY, nullable = false)
    @Enumerated(EnumType.STRING)
    private WeekDayEnum weekDay;

    // AGAR SHU KUNDA HODIM ISHLASA working = TRUE BO'LADI
    @Column(name = ColumnKey.WORKING)
    private boolean working;

    // ISHNING BOSHLANISH VAQTI
    // BUNDA VAQT STRING KO'RINISHIDA BO'LADI
    // MASALAN ( 09:00 )
    @Column(name = ColumnKey.START_TIME)
    private String startTime;

    // ISHNING TUGASH VAQTI
    // BUNDA VAQT STRING KO'RINISHIDA BO'LADI
    // MASALAN ( 19:00 )
    @Column(name = ColumnKey.END_TIME)
    private String endTime;

    // TEPADAGI ISHNING BOSHLANISH VA TUGASH VAQTLARI ORASIDAGI SOATNI HISOBLAB SHU YERGA YOZIB QO'YILADI
    @Column(name = ColumnKey.WORKING_HOURS)
    private Double workingHours;

    // AGAR TUSHLIK VAQTI BO'LADIGAN BO'LSA lunch = TRUE BO'LADI
    @Column(name = ColumnKey.LUNCH)
    private boolean lunch;

    // AGAR lunch = TRUE BO'LSA YA'NI TUSHLIK VAQTI BO'LADIGAN BO'LSA
    // TUSHLIKNING BOSHLANISH VAQTI KIRITIB KETILADI
    // MASALAN ( 13:00 )
    @Column(name = ColumnKey.LUNCH_START_TIME)
    private String lunchStartTime;

    // AGAR lunch = TRUE BO'LSA YA'NI TUSHLIK VAQTI BO'LADIGAN BO'LSA
    // TUSHLIKNING TUGASH VAQTI KIRITIB KETILADI
    // MASALAN ( 14:00 )
    @Column(name = ColumnKey.LUNCH_END_TIME)
    private String lunchEndTime;

    // TEPADAGI TUSHLIKNING BOSHLANISH VA TUGASH VAQTLARI ORASIDAGI SOATNI HISOBLAB SHU YERGA YOZIB QO'YILADI
    @Column(name = ColumnKey.LUNCH_HOURS)
    private Double lunchHours;
}
