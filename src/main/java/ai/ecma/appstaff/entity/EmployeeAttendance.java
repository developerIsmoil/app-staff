package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.AttendanceEnum;
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

// HODIMNING AYNAN QAYSIDIR Timesheet GA NISBATAN DAVOMATI YOZIB BORILADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_ATTENDANCE)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_ATTENDANCE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendance extends AbsUUIDUserAuditEntity {

    // QAYS TIMESHEET GA TEGISHLI EKANLIGI
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.TIMESHEET_EMPLOYEE_ID)
    private TimeSheetEmployee timeSheetEmployee;

    // HODIMNING SHU KUNDAGI HOLATI
    // ISHGA KELGAN, KELMAGA, KASAL, TA'TILDA VA ...
    // MASALAN (WORKING)
    @Column(name = ColumnKey.ATTENDANCE, nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceEnum attendance;

    // AYNAN QAYSI KUN EKANLIGI
    // MASALAN (01.01.2021)
    @Column(name = ColumnKey.DAY, nullable = false)
    private Date day;

    // ISH KUNIDAGI SOAT
    // AttendanceEnum WORKING BO'LSA TIZIMGA AVVAL KIRITIB QO'YILGAN BIR KUNLIK ISH SOATI OLINADI
    // MASALAN (8 SOAT). AGAR BOSHQA ENUM BO'LSA 0 BO'LADI
    // MASALAN (8)
    @Column(name = ColumnKey.WORK_HOUR)
    private Double workHour;

    // bu column indexlash uchun kerak
    @Column(name = ColumnKey.MONTH)
    private String month;

    // bu column indexlash uchun kerak
    @Column(name = ColumnKey.YEAR)
    private String year;
}
