package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.StudyDegreeEnum;
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

// BU YERDA HODIMNI TA'LIM HAQIDAGI MA'LUMOTLARI SAQLANADI
// HODIMNI TIZIMGA QO'SHADIGAN VAQTDAGI "TA'LIM HAQIDA MA'LUMOT"
// DEGAN BO'LIMDA KIRITILGAN MA'LUMOTLAR SHU YERDA SAQLANADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_EDUCATION_HISTORY)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_EDUCATION_HISTORY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEducationHistory extends AbsUUIDUserAuditEntity {

    // QAYSI HODIMGA TEGISHLI MA'LUMOT EKANLIGI
    // MASALAN (Ixtiyor Xaitov)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_ID)
    private Employee employee;

    // TA'LIM DARAJASI.
    // MASALAN (BAKALAVR)
    @Column(name = ColumnKey.STUDY_DEGREE, nullable = false)
    @Enumerated(EnumType.STRING)
    private StudyDegreeEnum studyDegree;

    // O'QUV MUASSASASI NOMI
    // MASALAN (INHA)
    @Column(name = ColumnKey.ORGANISATION_NAME, nullable = false)
    private String organisationName;

    // TA’LIM YO‘NALISHI
    // Masalan (Computer science)
    @Column(name = ColumnKey.STUDY_TYPE, nullable = false)
    private String studyType;

    // O’QISHNI BOSHLAGAN VAQTI
    // MASALAN (01/01/2017)
    @Column(name = ColumnKey.STARTED_STUDY_DATE, nullable = false)
    private Date startedStudyDate;

    // O’QISHNI TUGATGAN VAQTI
    // MASALAN (01/01/2021)
    @Column(name = ColumnKey.FINISHED_STUDY_DATE)
    private Date finishedStudyDate;

    // O'QISH DAVOM ETAYOTGAN BO'LSA
    // MASALAN (true)
    @Column(name = ColumnKey.NOT_FINISHED)
    private boolean notFinished;

}
