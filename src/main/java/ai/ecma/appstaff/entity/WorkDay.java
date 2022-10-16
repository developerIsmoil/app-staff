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

// TIZIM UCHUN ISH KUNLARI
// TimeSheetDA HAVFTADA QAYSI KUNLARI ISH BO'LISHINI TANLANADI
@Entity
@Table(name = TableNameConstant.WORK_DAY)
@SQLDelete(sql = "update " + TableNameConstant.WORK_DAY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkDay extends AbsUUIDUserAuditEntity {

    // HAVFTA KUNLARI
    @Column(name = ColumnKey.WEEK_DAY, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private WeekDayEnum weekDay;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    // TARTIB RAQAMI

    @Column(name = ColumnKey.INDEX)
    private Integer index;
}
