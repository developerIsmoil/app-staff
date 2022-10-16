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

// SHTAT
@Entity
@Table(name = TableNameConstant.STATE)
@SQLDelete(sql = "update " + TableNameConstant.STATE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class State extends AbsUUIDUserAuditEntity {

    // NOMI
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // QAYSI BO"LIMGA TEGISHLI EKANLIGI
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    private Department department;

    // NECHTA HODIM ISHLASHI (SONI)
    @Column(name = ColumnKey.EMPLOYEE_COUNT)
    private Integer employeeCount;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;
}
