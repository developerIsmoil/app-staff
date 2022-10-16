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
import java.util.Date;

// HODIM TAJRIBASI HAQIDA MA'LUMOT
// BU YERDA HODIMNI ISHLAGAN JOYLARI HAQIDAGI MA'LUMOTLARI SAQLANADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_EXPERIENCE_HISTORY)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_EXPERIENCE_HISTORY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeExperienceHistory extends AbsUUIDUserAuditEntity {

    // QAYSI HODIM EKANLIGI
    // MASALAN (Ixtiyor Xaitov)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_ID)
    private Employee employee;

    // QAYSI TASHKILOTGA ISHLAGAN EKANLIGI. TASHKILOT NOMI
    // MASALAN (ecma)
    @Column(name = ColumnKey.ORGANISATION_NAME, nullable = false)
    private String organisationName;

    // QANDAY LAVOZIMDA ISHLAGANLIGI
    // MASALAN (dasturchi)
    @Column(name = ColumnKey.POSITION, nullable = false)
    private String position;

    // ISHNI BOSHLAGAN VAQTI
    // MASALAN (01.01.2020)
    @Column(name = ColumnKey.STARTED_WORK_DATE, nullable = false)
    private Date startedWorkDate;

    // ISHDAN BO’SHAGAN VAQTI
    // MASALAN (01.01.2021)
    @Column(name = ColumnKey.FINISHED_WORK_DATE)
    private Date finishedWorkDate;

    // AGAR HOZIRDA HAM SHU YERDA ISHLASA notFinished = true BO'LADI
    // MASALAN (true)
    @Column(name = ColumnKey.NOT_FINISHED)
    private boolean notFinished;
}
