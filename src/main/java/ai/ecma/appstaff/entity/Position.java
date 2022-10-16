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

// LAVOZIM
@Entity
@Table(name = TableNameConstant.POSITION)
@SQLDelete(sql = "update " + TableNameConstant.POSITION + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position extends AbsUUIDUserAuditEntity {

    // NOMI
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // QAYSI BO'LIMGA TEGISHLI EKANLIGI
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    // timesheetni boshqara oladi, ya'ni bo'lim boshlig'i degani
    @Column(name = ColumnKey.MANAGE_TIMESHEET)
    private Boolean manageTimesheet;

    /**
     * For HRM system
     * Director_Id null keladi yoki ozimiz kiritib qoyishimiz kerak
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Position parent;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;
}
