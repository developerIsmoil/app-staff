package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

// TIMESHEET BILAN HODIMLARNI BO'GLAB TURADIGAN TABLE
@Entity
@Table(name = TableNameConstant.TIMESHEET_EMPLOYEE)
@SQLDelete(sql = "update " + TableNameConstant.TIMESHEET_EMPLOYEE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetEmployee extends AbsUUIDUserAuditEntity {

    //FIO
    //Filial
    //Bo’lim
    //Lavozim
    //Kategoriya
    //To’lov mezoni
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYMENT_INFO_ID)
    private EmploymentInfo employmentInfo;

    // QAYSI TIMESHEET UCHUN EKANLIGI
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.TIMESHEET_ID)
    private TimeSheet timeSheet;

    //Maosh
    @Column(name = ColumnKey.SALARY)
    private Double salary;

    //Ishlagan ish soati
    @Column(name = ColumnKey.WORKED_HOURS)
    private Integer workedHours;

    //Umumiy ish soati
    @Column(name = ColumnKey.WORK_HOURS)
    private Integer workHours;

    //Ishlagan kunlar
    @Column(name = ColumnKey.WORKED_DAYS)
    private Integer workedDays;

    //Umumiy ish kunlar
    @Column(name = ColumnKey.WORK_DAYS)
    private Integer workDays;

    //Bonus
    @Column(name = ColumnKey.BONUS)
    private Double bonus = 0d;

    //Premya
    @Column(name = ColumnKey.PREMIUM)
    private Double premium = 0d;

    //Berilgan avans
    @Column(name = ColumnKey.ADVANCE_SALARY)
    private Double advanceSalary = 0d;

    //Ushlanmalar
    @Column(name = ColumnKey.RETENTION_SALARY)
    private Double retentionSalary = 0d;

    //Qo’shib berish
    @Column(name = ColumnKey.ADDITION_SALARY)
    private Double additionSalary = 0d;

    //Umumiy
    @Column(name = ColumnKey.TOTAL_SALARY)
    private Double totalSalary = 0d;

    //Soliq summa
    @Column(name = ColumnKey.TAX_AMOUNT)
    private Double taxAmount = 0d;

    //Hodimga to‘lanadigan
    @Column(name = ColumnKey.PAID_SALARY)
    private Double paidSalary = 0d;

    public TimeSheetEmployee(EmploymentInfo employmentInfo, TimeSheet timeSheet, Double salary) {
        this.employmentInfo = employmentInfo;
        this.timeSheet = timeSheet;
        this.salary = salary;
    }

    public TimeSheetEmployee(EmploymentInfo employmentInfo, TimeSheet timeSheet) {
        this.employmentInfo = employmentInfo;
        this.timeSheet = timeSheet;
    }
}

