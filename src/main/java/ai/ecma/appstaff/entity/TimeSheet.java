package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
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
import java.util.Date;

// HODIMLARNI DAVOMATINI BOSHQARISH UCHUN
// HR VA BO'LIM BOSHLIQLARI UCHUN
@Entity
@Table(name = TableNameConstant.TIMESHEET)
@SQLDelete(sql = "update " + TableNameConstant.TIMESHEET + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheet extends AbsUUIDUserAuditEntity {

    // QAYSI BO'LIMGA TEGISHLI EKANLIGI
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    private Department department;

    // TIMESHEET STATUSI
    @Column(name = ColumnKey.TIMESHEET_STATUS, nullable = false)
    @Enumerated(EnumType.STRING)
    private TimeSheetStatusEnum timeSheetStatus;

    // QAYSI OY VA YIL UCHUNLIGI. OYNING 1 CHI SANASI YOZILADI BUNGA
    @Column(name = ColumnKey.DATE, nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    // TASDIQLANGAN SANASI
    @Column(name = ColumnKey.CONFIRM_DATE)
    private Date confirmDate;

    public TimeSheet(Department department, TimeSheetStatusEnum timeSheetStatus, Date date) {
        this.department = department;
        this.timeSheetStatus = timeSheetStatus;
        this.date = date;
    }
}
